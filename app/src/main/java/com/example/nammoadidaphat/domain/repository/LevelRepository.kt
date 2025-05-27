package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.Level
import kotlinx.coroutines.flow.Flow

interface LevelRepository {
    suspend fun getAllLevels(): Result<List<Level>>
    suspend fun getLevelsForWorkoutType(workoutTypeId: String): Result<List<Level>>
    suspend fun getLevelById(id: String): Result<Level>
    fun getLevels(): Flow<List<Level>>
    
    // Add missing methods to match implementation
    suspend fun addLevel(level: Level): Result<Unit>
    suspend fun updateLevel(level: Level): Result<Unit>
    suspend fun deleteLevel(id: String): Result<Unit>
} 