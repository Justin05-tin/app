package com.example.nammoadidaphat.domain.repository

import android.content.Intent
import com.example.nammoadidaphat.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    // Add FirebaseAuth property for safety checks
    val auth: FirebaseAuth?
    
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(
        email: String,
        password: String,
        fullName: String = ""
    ): Result<User>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<Unit>
    suspend fun getUserById(userId: String): Result<User>
    fun getCurrentUser(): Flow<User?>
    
    // Social login methods
    fun getGoogleSignInIntent(): Intent
    suspend fun handleGoogleSignInResult(data: Intent?): Result<User>
    
    fun getFacebookSignInIntent(): Intent
    suspend fun handleFacebookSignInResult(data: Intent?): Result<User>
} 