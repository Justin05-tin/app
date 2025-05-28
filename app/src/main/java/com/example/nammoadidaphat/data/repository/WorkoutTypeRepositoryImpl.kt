package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.WorkoutType
import com.example.nammoadidaphat.domain.repository.WorkoutTypeRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class WorkoutTypeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WorkoutTypeRepository {

    private val workoutTypesCollection = firestore.collection("workoutTypes")
    
    override suspend fun getAllWorkoutTypes(): Result<List<WorkoutType>> = try {
        val snapshot = workoutTypesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val workoutTypes = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    WorkoutType.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping workout type document ${doc.id}")
                null
            }
        }
        
        Result.success(workoutTypes)
    } catch (e: Exception) {
        Timber.e(e, "Error getting all workout types")
        Result.failure(e)
    }

    override suspend fun getWorkoutTypesForCategory(categoryId: String): Result<List<WorkoutType>> = try {
        val snapshot = workoutTypesCollection
            .whereEqualTo("categoryId", categoryId)
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val workoutTypes = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    WorkoutType.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping workout type document ${doc.id}")
                null
            }
        }
        
        Result.success(workoutTypes)
    } catch (e: Exception) {
        Timber.e(e, "Error getting workout types for category: $categoryId")
        Result.failure(e)
    }

    override suspend fun getWorkoutTypeById(id: String): Result<WorkoutType> = try {
        val doc = workoutTypesCollection.document(id).get().await()
        
        if (doc.exists()) {
            val data = doc.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", doc.id)
                }
                val workoutType = WorkoutType.fromMap(dataWithId)
                Result.success(workoutType)
            } else {
                Result.failure(Exception("Workout type data is null"))
            }
        } else {
            Result.failure(Exception("Workout type not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting workout type by ID: $id")
        Result.failure(e)
    }

    override suspend fun addWorkoutType(workoutType: WorkoutType): Result<Unit> = try {
        val dataToAdd = workoutType.toMap().toMutableMap().apply {
            put("createdAt", Timestamp.now())
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        if (workoutType.id.isNotEmpty()) {
            // If ID is provided, use it as document ID
            workoutTypesCollection.document(workoutType.id).set(dataToAdd).await()
        } else {
            // Otherwise, let Firestore generate an ID
            workoutTypesCollection.add(dataToAdd).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error adding workout type")
        Result.failure(e)
    }

    override suspend fun updateWorkoutType(workoutType: WorkoutType): Result<Unit> = try {
        val dataToUpdate = workoutType.toMap().toMutableMap().apply {
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        workoutTypesCollection.document(workoutType.id).update(dataToUpdate).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error updating workout type: ${workoutType.id}")
        Result.failure(e)
    }

    override suspend fun deleteWorkoutType(id: String): Result<Unit> = try {
        workoutTypesCollection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error deleting workout type: $id")
        Result.failure(e)
    }

    override fun getWorkoutTypes(): Flow<List<WorkoutType>> = callbackFlow {
        val listenerRegistration = workoutTypesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for workout type updates")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val workoutTypes = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                WorkoutType.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping workout type document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(workoutTypes)
                }
            }
            
        // Clean up the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }
} 