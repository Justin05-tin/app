package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            Result.success(user.toDomain())
        } else {
            Result.failure(Exception("Authentication failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signUp(email: String, password: String, name: String): Result<User> = try {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            val userData = User(
                id = user.uid,
                email = email,
                name = name
            )
            database.reference.child("users").child(user.uid).setValue(userData).await()
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

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain())
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    private fun com.google.firebase.auth.FirebaseUser.toDomain(): User {
        return User(
            id = uid,
            email = email ?: "",
            name = displayName ?: "",
            profilePictureUrl = photoUrl?.toString()
        )
    }
} 