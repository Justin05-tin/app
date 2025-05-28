package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.WorkoutType
import kotlinx.coroutines.flow.Flow

interface WorkoutTypeRepository {
    suspend fun getAllWorkoutTypes(): Result<List<WorkoutType>>
    suspend fun getWorkoutTypesForCategory(categoryId: String): Result<List<WorkoutType>>
    suspend fun getWorkoutTypeById(id: String): Result<WorkoutType>
    suspend fun addWorkoutType(workoutType: WorkoutType): Result<Unit>
    suspend fun updateWorkoutType(workoutType: WorkoutType): Result<Unit>
    suspend fun deleteWorkoutType(id: String): Result<Unit>
    fun getWorkoutTypes(): Flow<List<WorkoutType>>
} 