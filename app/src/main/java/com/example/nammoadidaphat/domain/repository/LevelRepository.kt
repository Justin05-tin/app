package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.Level
import kotlinx.coroutines.flow.Flow

interface LevelRepository {
    suspend fun getAllLevels(): Result<List<Level>>
    suspend fun getLevelById(id: String): Result<Level>
    suspend fun addLevel(level: Level): Result<Unit>
    suspend fun updateLevel(level: Level): Result<Unit>
    suspend fun deleteLevel(id: String): Result<Unit>
    fun getLevels(): Flow<List<Level>>
} 