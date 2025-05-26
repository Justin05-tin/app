package com.example.nammoadidaphat.data.repository

import android.content.Context
import android.content.Intent
import android.app.Activity
import androidx.activity.result.ActivityResultLauncher
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val usersCollection = firestore.collection("users")
    
    // Web Client ID cho Google Sign-In
    private val webClientId = "1026435770130-gha0scpj0328af1nnc5cl6ehmq3l40su.apps.googleusercontent.com"
    
    // Google sign in
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        
        Timber.d("Using Google Sign-In web client ID: $webClientId")
        
        GoogleSignIn.getClient(context, gso)
    }
    
    // Facebook sign in
    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

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
            } else {
                // Create a basic user document if it doesn't exist
                val basicUserData = User(
                    userId = user.uid,
                    email = email,
                    fullName = user.displayName ?: "",
                    avatarUrl = user.photoUrl?.toString() ?: "",
                    createdAt = timestamp,
                    updatedAt = timestamp,
                    authProvider = "password"
                )
                usersCollection.document(user.uid).set(basicUserData.toMap()).await()
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
        age: Int?,
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
                age = age,
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
        
        // Sign out from Google
        try {
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            Timber.e(e, "Error signing out from Google")
        }
        
        // Sign out from Facebook
        try {
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
            Timber.e(e, "Error signing out from Facebook")
        }
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
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                try {
                    // User is signed in, get their data from Firestore
                    usersCollection.document(firebaseUser.uid).get()
                        .addOnSuccessListener { document ->
                            try {
                                if (document != null && document.exists()) {
                                    val userData = try {
                                        User.fromMap(document.data ?: emptyMap())
                                    } catch (e: Exception) {
                                        Timber.e(e, "Error parsing user data")
                                        // Create minimal user to prevent crashes
                                        User.createMinimalUser(
                                            userId = firebaseUser.uid,
                                            email = firebaseUser.email ?: "",
                                            authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                                        )
                                    }
                                    trySend(userData)
                                } else {
                                    // Create basic profile if Firestore data doesn't exist
                                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                    val timestamp = dateFormat.format(Date())
                                    
                                    val providerId = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                                    
                                    val userData = User(
                                        userId = firebaseUser.uid,
                                        email = firebaseUser.email ?: "",
                                        fullName = firebaseUser.displayName ?: "",
                                        avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
                                        createdAt = timestamp,
                                        updatedAt = timestamp,
                                        authProvider = providerId
                                    )
                                    
                                    // Don't block the flow - just create the user record in the background
                                    usersCollection.document(firebaseUser.uid)
                                        .set(userData.toMap())
                                        .addOnFailureListener { e ->
                                            Timber.e(e, "Failed to create user document")
                                        }
                                    
                                    trySend(userData)
                                }
                            } catch (e: Exception) {
                                Timber.e(e, "Error processing user data in getCurrentUser")
                                // Even if data processing fails, return a basic user object so the app doesn't crash
                                val basicUser = User.createMinimalUser(
                                    userId = firebaseUser.uid,
                                    email = firebaseUser.email ?: "",
                                    authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                                )
                                trySend(basicUser)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Timber.e(exception, "Failed to get user data from Firestore")
                            // Even if Firestore fails, we still know the user is authenticated
                            val basicUser = User.createMinimalUser(
                                userId = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                            )
                            trySend(basicUser)
                        }
                } catch (e: Exception) {
                    Timber.e(e, "Unexpected error in getCurrentUser")
                    // Return a basic user even in case of unexpected errors
                    val basicUser = User.createMinimalUser(
                        userId = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                    ) 
                    trySend(basicUser)
                }
            }
        }
        
        // Register the auth state listener
        auth.addAuthStateListener(listener)
        
        // Clean up when the flow is closed
        awaitClose { 
            auth.removeAuthStateListener(listener) 
        }
    }
    
    override fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    override suspend fun handleGoogleSignInResult(data: Intent?): Result<User> {
        if (data == null) {
            return Result.failure(Exception("Sign-in intent data is null"))
        }
        
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val user = signInWithCredential(credential)
                
                // Create or update user profile
                val userData = createOrUpdateSocialUserProfile(
                    firebaseUser = user,
                    providerId = "google.com",
                    fullName = account.displayName ?: "",
                    email = account.email ?: "",
                    photoUrl = account.photoUrl?.toString() ?: ""
                )
                
                Result.success(userData)
            } else {
                Result.failure(Exception("Google sign in failed"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Google sign in error")
            Result.failure(e)
        }
    }
    
    override fun getFacebookSignInIntent(): Intent {
        // This is a dummy intent as Facebook uses its own mechanism
        return Intent()
    }
    
    // Function to initiate Facebook login - separate from the intent getter
    fun initiateLoginWithFacebook(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }
    
    override suspend fun handleFacebookSignInResult(data: Intent?): Result<User> {
        return suspendCoroutine { continuation ->
            try {
                LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        // This runs on the main thread, so we need to handle it carefully
                        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        
                        // Use a simple coroutine scope instead of lifecycleScope
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val firebaseUser = signInWithCredential(credential)
                                
                                // Get user data from the token
                                val userData = createOrUpdateSocialUserProfile(
                                    firebaseUser = firebaseUser,
                                    providerId = "facebook.com",
                                    fullName = firebaseUser.displayName ?: "",
                                    email = firebaseUser.email ?: "",
                                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                                )
                                
                                continuation.resume(Result.success(userData))
                            } catch (e: Exception) {
                                Timber.e(e, "Facebook Firebase auth error")
                                continuation.resume(Result.failure(e))
                            }
                        }
                    }
                    
                    override fun onCancel() {
                        Timber.d("Facebook login cancelled")
                        continuation.resume(Result.failure(Exception("Facebook login was cancelled")))
                    }
                    
                    override fun onError(error: FacebookException) {
                        Timber.e(error, "Facebook login error")
                        continuation.resume(Result.failure(error))
                    }
                })
                
                callbackManager.onActivityResult(
                    0, // These parameters will be replaced by the actual values
                    0, // from the activity result
                    data
                )
                
            } catch (e: Exception) {
                Timber.e(e, "Facebook login general error")
                continuation.resume(Result.failure(e))
            }
        }
    }

    // Helper to sign in with credential
    private suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser {
        val result = auth.signInWithCredential(credential).await()
        return result.user ?: throw Exception("Failed to sign in with credential")
    }
    
    // Helper to create or update user profile for social logins
    private suspend fun createOrUpdateSocialUserProfile(
        firebaseUser: FirebaseUser,
        providerId: String,
        fullName: String,
        email: String,
        photoUrl: String
    ): User {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timestamp = dateFormat.format(Date())
        
        // Check if user exists
        val userDoc = usersCollection.document(firebaseUser.uid).get().await()
        
        return if (userDoc.exists()) {
            // Update existing user
            val updates = mapOf(
                "updated_at" to timestamp,
                "email" to email,
                "avatar_url" to photoUrl
            )
            
            usersCollection.document(firebaseUser.uid).update(updates).await()
            
            // Get updated user data
            getUserFromFirestore(firebaseUser.uid)
        } else {
            // Create new user
            val userData = User(
                userId = firebaseUser.uid,
                email = email,
                passwordHash = "", // Not used for social logins
                fullName = fullName,
                avatarUrl = photoUrl,
                isPremium = false,
                createdAt = timestamp,
                updatedAt = timestamp,
                authProvider = providerId
            )
            
            usersCollection.document(firebaseUser.uid).set(userData.toMap()).await()
            userData
        }
    }

    private suspend fun getUserFromFirestore(userId: String): User {
        return suspendCoroutine { continuation ->
            try {
                usersCollection.document(userId).get()
                    .addOnSuccessListener { document ->
                        try {
                            if (document != null && document.exists()) {
                                val userData = try {
                                    User.fromMap(document.data ?: emptyMap())
                                } catch (e: Exception) {
                                    Timber.e(e, "Error parsing user data in getUserFromFirestore")
                                    // Return minimal user to prevent crashes
                                    val currentUser = auth.currentUser
                                    User.createMinimalUser(
                                        userId = userId,
                                        email = currentUser?.email ?: "",
                                        authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                                    )
                                }
                                continuation.resume(userData)
                            } else {
                                // If user exists in Auth but not in Firestore, create a basic record
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val timestamp = dateFormat.format(Date())
                                
                                val currentUser = auth.currentUser
                                val userData = User(
                                    userId = userId,
                                    email = currentUser?.email ?: "",
                                    fullName = currentUser?.displayName ?: "",
                                    avatarUrl = currentUser?.photoUrl?.toString() ?: "",
                                    createdAt = timestamp,
                                    updatedAt = timestamp,
                                    authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                                )
                                
                                // Create the user in Firestore
                                usersCollection.document(userId).set(userData.toMap())
                                    .addOnSuccessListener {
                                        continuation.resume(userData)
                                    }
                                    .addOnFailureListener { error ->
                                        Timber.e(error, "Failed to create user in Firestore")
                                        // Return basic user data instead of failing
                                        continuation.resume(userData)
                                    }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing user data in getUserFromFirestore")
                            // Return basic user data instead of failing
                            val currentUser = auth.currentUser
                            val basicUser = User.createMinimalUser(
                                userId = userId,
                                email = currentUser?.email ?: "",
                                authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                            )
                            continuation.resume(basicUser)
                        }
                    }
                    .addOnFailureListener { error ->
                        Timber.e(error, "Failed to get user document")
                        // Return basic user object instead of throwing exception
                        val currentUser = auth.currentUser
                        val basicUser = User.createMinimalUser(
                            userId = userId,
                            email = currentUser?.email ?: "",
                            authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                        )
                        continuation.resume(basicUser)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in getUserFromFirestore")
                // Handle any unexpected exceptions by returning a basic user
                val currentUser = auth.currentUser
                val basicUser = User.createMinimalUser(
                    userId = userId,
                    email = currentUser?.email ?: "",
                    authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                ) 
                continuation.resume(basicUser)
            }
        }
    }
    
    // Handle activity result from Facebook login
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
} 