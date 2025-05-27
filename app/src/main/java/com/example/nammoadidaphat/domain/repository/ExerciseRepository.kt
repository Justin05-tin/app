package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.Exercise
import kotlin.Result
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    /**
     * Get all exercises 
     */
    suspend fun getAllExercises(): Result<List<Exercise>>
    
    /**
     * Get exercises for specific level
     */
    suspend fun getExercisesForLevel(levelId: String): Result<List<Exercise>>
    
    /**
     * Get exercise by ID
     */
    suspend fun getExerciseById(exerciseId: String): Result<Exercise>
    
    /**
     * Search exercises by name or description
     */
    suspend fun searchExercises(query: String): Result<List<Exercise>>

    suspend fun getExercisesByWorkoutType(workoutTypeId: String): Result<List<Exercise>>
    suspend fun addExercise(exercise: Exercise): Result<Unit>
    suspend fun updateExercise(exercise: Exercise): Result<Unit>
    suspend fun deleteExercise(id: String): Result<Unit>
    fun getExercises(): Flow<List<Exercise>>
    fun getExercisesForWorkoutType(workoutTypeId: String): Flow<List<Exercise>>
} 