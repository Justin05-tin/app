package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.UserProgress
import com.example.nammoadidaphat.domain.repository.UserProgressRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class UserProgressRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserProgressRepository {

    private val userProgressCollection = firestore.collection("userProgress")
    
    override suspend fun getAllUserProgress(): Result<List<UserProgress>> = try {
        val snapshot = userProgressCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            
        val userProgressList = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    UserProgress.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping user progress document ${doc.id}")
                null
            }
        }
        
        Result.success(userProgressList)
    } catch (e: Exception) {
        Timber.e(e, "Error getting all user progress")
        Result.failure(e)
    }

    override suspend fun getUserProgressForUser(userId: String): Result<List<UserProgress>> = try {
        val snapshot = userProgressCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
            
        val userProgressList = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    UserProgress.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping user progress document ${doc.id}")
                null
            }
        }
        
        Result.success(userProgressList)
    } catch (e: Exception) {
        Timber.e(e, "Error getting user progress for user: $userId")
        Result.failure(e)
    }

    override suspend fun getUserProgressById(id: String): Result<UserProgress> = try {
        val doc = userProgressCollection.document(id).get().await()
        
        if (doc.exists()) {
            val data = doc.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", doc.id)
                }
                val userProgress = UserProgress.fromMap(dataWithId)
                Result.success(userProgress)
            } else {
                Result.failure(Exception("User progress data is null"))
            }
        } else {
            Result.failure(Exception("User progress not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting user progress by ID: $id")
        Result.failure(e)
    }

    override suspend fun addUserProgress(userProgress: UserProgress): Result<Unit> = try {
        val dataToAdd = userProgress.toMap().toMutableMap().apply {
            put("createdAt", Timestamp.now())
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        if (userProgress.id.isNotEmpty()) {
            // If ID is provided, use it as document ID
            userProgressCollection.document(userProgress.id).set(dataToAdd).await()
        } else {
            // Otherwise, let Firestore generate an ID
            userProgressCollection.add(dataToAdd).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error adding user progress")
        Result.failure(e)
    }

    override suspend fun updateUserProgress(userProgress: UserProgress): Result<Unit> = try {
        val dataToUpdate = userProgress.toMap().toMutableMap().apply {
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        userProgressCollection.document(userProgress.id).update(dataToUpdate).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error updating user progress: ${userProgress.id}")
        Result.failure(e)
    }

    override suspend fun deleteUserProgress(id: String): Result<Unit> = try {
        userProgressCollection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error deleting user progress: $id")
        Result.failure(e)
    }

    override fun getUserProgress(userId: String): Flow<List<UserProgress>> = callbackFlow {
        val listenerRegistration = userProgressCollection
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for user progress updates")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val userProgressList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                UserProgress.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping user progress document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(userProgressList)
                }
            }
            
        // Clean up the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }
} 