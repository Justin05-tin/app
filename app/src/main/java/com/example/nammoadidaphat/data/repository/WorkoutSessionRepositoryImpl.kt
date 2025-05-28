package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.WorkoutSession
import com.example.nammoadidaphat.domain.repository.WorkoutSessionRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class WorkoutSessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WorkoutSessionRepository {

    private val workoutSessionsCollection = firestore.collection("workoutSessions")
    
    override suspend fun getAllWorkoutSessions(): Result<List<WorkoutSession>> = try {
        val snapshot = workoutSessionsCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            
        val workoutSessions = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    WorkoutSession.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping workout session document ${doc.id}")
                null
            }
        }
        
        Result.success(workoutSessions)
    } catch (e: Exception) {
        Timber.e(e, "Error getting all workout sessions")
        Result.failure(e)
    }

    override suspend fun getWorkoutSessionsForUser(userId: String): Result<List<WorkoutSession>> = try {
        val snapshot = workoutSessionsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            
        val workoutSessions = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    WorkoutSession.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping workout session document ${doc.id}")
                null
            }
        }
        
        Result.success(workoutSessions)
    } catch (e: Exception) {
        Timber.e(e, "Error getting workout sessions for user: $userId")
        Result.failure(e)
    }

    override suspend fun getWorkoutSessionById(id: String): Result<WorkoutSession> = try {
        val doc = workoutSessionsCollection.document(id).get().await()
        
        if (doc.exists()) {
            val data = doc.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", doc.id)
                }
                val workoutSession = WorkoutSession.fromMap(dataWithId)
                Result.success(workoutSession)
            } else {
                Result.failure(Exception("Workout session data is null"))
            }
        } else {
            Result.failure(Exception("Workout session not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting workout session by ID: $id")
        Result.failure(e)
    }

    override suspend fun addWorkoutSession(workoutSession: WorkoutSession): Result<Unit> = try {
        val dataToAdd = workoutSession.toMap().toMutableMap().apply {
            put("createdAt", Timestamp.now())
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        if (workoutSession.id.isNotEmpty()) {
            // If ID is provided, use it as document ID
            workoutSessionsCollection.document(workoutSession.id).set(dataToAdd).await()
        } else {
            // Otherwise, let Firestore generate an ID
            workoutSessionsCollection.add(dataToAdd).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error adding workout session")
        Result.failure(e)
    }

    override suspend fun updateWorkoutSession(workoutSession: WorkoutSession): Result<Unit> = try {
        val dataToUpdate = workoutSession.toMap().toMutableMap().apply {
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        workoutSessionsCollection.document(workoutSession.id).update(dataToUpdate).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error updating workout session: ${workoutSession.id}")
        Result.failure(e)
    }

    override suspend fun deleteWorkoutSession(id: String): Result<Unit> = try {
        workoutSessionsCollection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error deleting workout session: $id")
        Result.failure(e)
    }

    override fun getWorkoutSessions(userId: String): Flow<List<WorkoutSession>> = callbackFlow {
        val listenerRegistration = workoutSessionsCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for workout session updates")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val workoutSessions = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                WorkoutSession.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping workout session document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(workoutSessions)
                }
            }
            
        // Clean up the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }
} 