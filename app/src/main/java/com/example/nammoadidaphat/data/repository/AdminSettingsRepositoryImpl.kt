package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.AdminSettings
import com.example.nammoadidaphat.domain.repository.AdminSettingsRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class AdminSettingsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminSettingsRepository {

    private val settingsCollection = firestore.collection("admin_settings")
    private val settingsDocId = "app_settings" // The ID of our single settings document
    
    override suspend fun getAdminSettings(): Result<AdminSettings> = try {
        val doc = settingsCollection.document(settingsDocId).get().await()
        
        if (doc.exists()) {
            val data = doc.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", doc.id)
                }
                val settings = AdminSettings.fromMap(dataWithId)
                Result.success(settings)
            } else {
                // If the document exists but has no data, return default settings
                Result.success(AdminSettings())
            }
        } else {
            // If the document doesn't exist, create it with default settings
            val defaultSettings = AdminSettings()
            val dataToAdd = defaultSettings.toMap().toMutableMap().apply {
                put("createdAt", Timestamp.now())
                put("updatedAt", Timestamp.now())
                // Don't include ID in the document data
                remove("id")
            }
            
            settingsCollection.document(settingsDocId).set(dataToAdd).await()
            Result.success(defaultSettings)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting admin settings")
        Result.failure(e)
    }

    override suspend fun updateAdminSettings(adminSettings: AdminSettings): Result<Unit> = try {
        val dataToUpdate = adminSettings.toMap().toMutableMap().apply {
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        // Always use the predefined document ID
        settingsCollection.document(settingsDocId).update(dataToUpdate).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error updating admin settings")
        Result.failure(e)
    }

    override fun observeAdminSettings(): Flow<AdminSettings?> = callbackFlow {
        val listenerRegistration = settingsCollection.document(settingsDocId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for admin settings updates")
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val data = snapshot.data
                        if (data != null) {
                            // Add the document ID to the map before conversion
                            val dataWithId = data.toMutableMap().apply {
                                put("id", snapshot.id)
                            }
                            val settings = AdminSettings.fromMap(dataWithId)
                            trySend(settings)
                        } else {
                            // Send default settings if data is null
                            trySend(AdminSettings())
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error mapping admin settings document")
                        // Send default settings in case of error
                        trySend(AdminSettings())
                    }
                } else {
                    // Document doesn't exist, send default settings
                    trySend(AdminSettings())
                }
            }
            
        // Clean up the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }
} 