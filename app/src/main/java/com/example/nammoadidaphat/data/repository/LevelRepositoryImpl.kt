package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.Level
import com.example.nammoadidaphat.domain.repository.LevelRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class LevelRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : LevelRepository {

    private val levelsCollection = firestore.collection("levels")
    
    override suspend fun getAllLevels(): Result<List<Level>> = try {
        val snapshot = levelsCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val levels = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    Level.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping level document ${doc.id}")
                null
            }
        }
        
        Result.success(levels)
    } catch (e: Exception) {
        Timber.e(e, "Error getting all levels")
        Result.failure(e)
    }

    override suspend fun getLevelsForWorkoutType(workoutTypeId: String): Result<List<Level>> = try {
        Timber.d("Getting levels for workout type: $workoutTypeId")
        val snapshot = levelsCollection
            .whereEqualTo("workoutTypeId", workoutTypeId)
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val levels = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    Timber.d("Level data: $dataWithId")
                    Level.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping level document ${doc.id}")
                null
            }
        }
        
        Timber.d("Found ${levels.size} levels for workout type $workoutTypeId")
        Result.success(levels)
    } catch (e: Exception) {
        Timber.e(e, "Error getting levels for workout type: $workoutTypeId")
        Result.failure(e)
    }

    override suspend fun getLevelById(id: String): Result<Level> = try {
        val doc = levelsCollection.document(id).get().await()
        
        if (doc.exists()) {
            val data = doc.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", doc.id)
                }
                val level = Level.fromMap(dataWithId)
                Result.success(level)
            } else {
                Result.failure(Exception("Level data is null"))
            }
        } else {
            Result.failure(Exception("Level not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting level by ID: $id")
        Result.failure(e)
    }

    override suspend fun addLevel(level: Level): Result<Unit> = try {
        val dataToAdd = level.toMap().toMutableMap().apply {
            put("createdAt", Timestamp.now())
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        if (level.id.isNotEmpty()) {
            // If ID is provided, use it as document ID
            levelsCollection.document(level.id).set(dataToAdd).await()
        } else {
            // Otherwise, let Firestore generate an ID
            levelsCollection.add(dataToAdd).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error adding level")
        Result.failure(e)
    }

    override suspend fun updateLevel(level: Level): Result<Unit> = try {
        val dataToUpdate = level.toMap().toMutableMap().apply {
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        levelsCollection.document(level.id).update(dataToUpdate).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error updating level: ${level.id}")
        Result.failure(e)
    }

    override suspend fun deleteLevel(id: String): Result<Unit> = try {
        levelsCollection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error deleting level: $id")
        Result.failure(e)
    }

    override fun getLevels(): Flow<List<Level>> = callbackFlow {
        val listenerRegistration = levelsCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for level updates")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val levels = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                Level.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping level document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(levels)
                }
            }
            
        // Clean up the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }
} 