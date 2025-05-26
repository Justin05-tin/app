package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    suspend fun getAllExercises(): Result<List<Exercise>>
    suspend fun getExercisesByWorkoutType(workoutTypeId: String): Result<List<Exercise>>
    suspend fun getExercisesByLevel(levelId: String): Result<List<Exercise>>
    suspend fun getExerciseById(id: String): Result<Exercise>
    suspend fun addExercise(exercise: Exercise): Result<Unit>
    suspend fun updateExercise(exercise: Exercise): Result<Unit>
    suspend fun deleteExercise(id: String): Result<Unit>
    fun getExercises(): Flow<List<Exercise>>
    fun getExercisesForWorkoutType(workoutTypeId: String): Flow<List<Exercise>>
} 