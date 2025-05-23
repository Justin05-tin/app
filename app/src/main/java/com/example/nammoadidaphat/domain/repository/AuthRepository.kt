package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(
        email: String, 
        password: String, 
        fullName: String,
        age: Int? = null,
        gender: String = "",
        height: Int? = null,
        weight: Float? = null,
        fitnessLevel: String = "",
        goals: String = ""
    ): Result<User>
    suspend fun signOut()
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun updateUserProfile(user: User): Result<Unit>
    suspend fun getUserById(userId: String): Result<User>
    fun getCurrentUser(): Flow<User?>
} 