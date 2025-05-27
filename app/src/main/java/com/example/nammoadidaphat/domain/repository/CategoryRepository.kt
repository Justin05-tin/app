package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getAllCategories(): Result<List<Category>>
    suspend fun getCategoryById(id: String): Result<Category>
    suspend fun addCategory(category: Category): Result<Unit>
    suspend fun updateCategory(category: Category): Result<Unit>
    suspend fun deleteCategory(id: String): Result<Unit>
    fun getCategories(): Flow<List<Category>>
} 