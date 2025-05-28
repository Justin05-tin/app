package com.example.nammoadidaphat.domain.repository

import com.example.nammoadidaphat.domain.model.AdminSettings
import kotlinx.coroutines.flow.Flow

interface AdminSettingsRepository {
    suspend fun getAdminSettings(): Result<AdminSettings>
    suspend fun updateAdminSettings(adminSettings: AdminSettings): Result<Unit>
    fun observeAdminSettings(): Flow<AdminSettings?>
} 