package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository {
    suspend fun getAllUserProgress(): Result<List<UserProgress>>
    suspend fun getUserProgressForUser(userId: String): Result<List<UserProgress>>
    suspend fun getUserProgressById(id: String): Result<UserProgress>
    suspend fun addUserProgress(userProgress: UserProgress): Result<Unit>
    suspend fun updateUserProgress(userProgress: UserProgress): Result<Unit>
    suspend fun deleteUserProgress(id: String): Result<Unit>
    fun getUserProgress(userId: String): Flow<List<UserProgress>>
} 