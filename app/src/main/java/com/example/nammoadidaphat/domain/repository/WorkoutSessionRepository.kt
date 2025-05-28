package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutSessionRepository {
    suspend fun getAllWorkoutSessions(): Result<List<WorkoutSession>>
    suspend fun getWorkoutSessionsForUser(userId: String): Result<List<WorkoutSession>>
    suspend fun getWorkoutSessionById(id: String): Result<WorkoutSession>
    suspend fun addWorkoutSession(workoutSession: WorkoutSession): Result<Unit>
    suspend fun updateWorkoutSession(workoutSession: WorkoutSession): Result<Unit>
    suspend fun deleteWorkoutSession(id: String): Result<Unit>
    fun getWorkoutSessions(userId: String): Flow<List<WorkoutSession>>
} 