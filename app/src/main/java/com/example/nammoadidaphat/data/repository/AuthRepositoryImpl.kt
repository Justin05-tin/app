package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    private val usersCollection = firestore.collection("users")

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        // Auth sign in
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            // Update last login timestamp
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            
            // Update the user's document in Firestore
            val userDoc = usersCollection.document(user.uid).get().await()
            if (userDoc.exists()) {
                usersCollection.document(user.uid).update("updated_at", timestamp).await()
            }
            
            // Get user data from Firestore
            val userData = getUserFromFirestore(user.uid)
            Result.success(userData)
        } else {
            Result.failure(Exception("Authentication failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String,
        dateOfBirth: String,
        gender: String,
        height: Int?,
        weight: Float?,
        fitnessLevel: String,
        goals: String
    ): Result<User> = try {
        // Create auth account
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val timestamp = dateFormat.format(Date())
            
            val userData = User(
                userId = user.uid,
                email = email,
                passwordHash = "", // Firebase Auth handles this
                fullName = fullName,
                avatarUrl = "",
                dateOfBirth = dateOfBirth,
                gender = gender,
                height = height,
                weight = weight,
                fitnessLevel = fitnessLevel,
                goals = goals,
                isPremium = false,
                createdAt = timestamp,
                updatedAt = timestamp
            )
            
            // Save user data to Firestore
            usersCollection.document(user.uid).set(userData.toMap()).await()
            Result.success(userData)
        } else {
            Result.failure(Exception("Registration failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun updateUserProfile(user: User): Result<Unit> = try {
        val updates = user.toMap().toMutableMap()
        updates["updated_at"] = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        
        usersCollection.document(user.userId).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun getUserById(userId: String): Result<User> = try {
        val user = getUserFromFirestore(userId)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        // Listen for auth state changes
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                // User is signed in, get their data from Firestore
                usersCollection.document(firebaseUser.uid).get()
                    .addOnSuccessListener { document ->
                        if (document != null && document.exists()) {
                            val userData = User.fromMap(document.data ?: emptyMap())
                            trySend(userData)
                        } else {
                            // Create basic profile if Firestore data doesn't exist
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val timestamp = dateFormat.format(Date())
                            
                            val userData = User(
                                userId = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                createdAt = timestamp,
                                updatedAt = timestamp
                            )
                            
                            // Don't block the flow - just create the user record in the background
                            usersCollection.document(firebaseUser.uid).set(userData.toMap())
                            
                            trySend(userData)
                        }
                    }
                    .addOnFailureListener {
                        // Even if Firestore fails, we still know the user is authenticated
                        val basicUser = User(
                            userId = firebaseUser.uid,
                            email = firebaseUser.email ?: ""
                        )
                        trySend(basicUser)
                    }
            }
        }
        
        // Register the auth state listener
        auth.addAuthStateListener(authStateListener)
        
        // Clean up when the flow is closed
        awaitClose { 
            auth.removeAuthStateListener(authStateListener) 
        }
    }

    private suspend fun getUserFromFirestore(userId: String): User = suspendCoroutine { continuation ->
        usersCollection.document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val userData = User.fromMap(document.data ?: emptyMap())
                    continuation.resume(userData)
                } else {
                    // If user exists in Auth but not in Firestore, create a basic record
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val timestamp = dateFormat.format(Date())
                    
                    val userData = User(
                        userId = userId,
                        email = auth.currentUser?.email ?: "",
                        createdAt = timestamp,
                        updatedAt = timestamp
                    )
                    
                    // Create the user in Firestore
                    usersCollection.document(userId).set(userData.toMap())
                        .addOnSuccessListener {
                            continuation.resume(userData)
                        }
                        .addOnFailureListener {
                            continuation.resumeWithException(it)
                        }
                }
            }
            .addOnFailureListener {
                continuation.resumeWithException(it)
            }
    }
} 