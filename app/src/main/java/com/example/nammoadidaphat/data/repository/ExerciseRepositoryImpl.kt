package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.Exercise
import com.example.nammoadidaphat.domain.repository.ExerciseRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ExerciseRepository {

    private val exercisesCollection = firestore.collection("exercises")
    
    override suspend fun getAllExercises(): Result<List<Exercise>> = try {
        val snapshot = exercisesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val exercises = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    Exercise.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping exercise document ${doc.id}")
                null
            }
        }
        
        Result.success(exercises)
    } catch (e: Exception) {
        Timber.e(e, "Error getting all exercises")
        Result.failure(e)
    }
    
    override suspend fun getExercisesForLevel(levelId: String): Result<List<Exercise>> = try {
        val snapshot = exercisesCollection
            .whereEqualTo("levelId", levelId)
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val exercises = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    Exercise.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping exercise document ${doc.id}")
                null
            }
        }
        
        Result.success(exercises)
    } catch (e: Exception) {
        Timber.e(e, "Error getting exercises for level: $levelId")
        Result.failure(e)
    }
    
    override suspend fun getExerciseById(exerciseId: String): Result<Exercise> = try {
        val docSnapshot = exercisesCollection.document(exerciseId).get().await()
        
        if (docSnapshot.exists()) {
            val data = docSnapshot.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", docSnapshot.id)
                }
                Result.success(Exercise.fromMap(dataWithId))
            } else {
                Result.failure(NoSuchElementException("Exercise data is null"))
            }
        } else {
            Result.failure(NoSuchElementException("Exercise not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting exercise by ID: $exerciseId")
        Result.failure(e)
    }
    
    override suspend fun searchExercises(query: String): Result<List<Exercise>> = try {
        // Since Firestore doesn't support full-text search, we'll do a simple startsWith query
        // on the name field. For a more robust solution, you might want to consider using
        // a service like Algolia or Elasticsearch.
        val queryLower = query.lowercase()
        
        val snapshot = exercisesCollection
            .orderBy("name")
            .get()
            .await()
            
        val exercises = snapshot.documents
            .mapNotNull { doc ->
                try {
                    val data = doc.data
                    if (data != null) {
                        // Add the document ID to the map before conversion
                        val dataWithId = data.toMutableMap().apply {
                            put("id", doc.id)
                        }
                        Exercise.fromMap(dataWithId)
                    } else null
                } catch (e: Exception) {
                    Timber.e(e, "Error mapping exercise document ${doc.id}")
                    null
                }
            }
            .filter { 
                it.name.lowercase().contains(queryLower) || 
                it.description.lowercase().contains(queryLower)
            }
        
        Result.success(exercises)
    } catch (e: Exception) {
        Timber.e(e, "Error searching exercises: $query")
        Result.failure(e)
    }
    
    override suspend fun getExercisesByWorkoutType(workoutTypeId: String): Result<List<Exercise>> = try {
        val snapshot = exercisesCollection
            .whereEqualTo("workoutTypeId", workoutTypeId)
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val exercises = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    Exercise.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping exercise document ${doc.id}")
                null
            }
        }
        
        Result.success(exercises)
    } catch (e: Exception) {
        Timber.e(e, "Error getting exercises for workout type: $workoutTypeId")
        Result.failure(e)
    }
    
    override suspend fun addExercise(exercise: Exercise): Result<Unit> = try {
        val documentRef = if (exercise.id.isBlank()) {
            exercisesCollection.document()
        } else {
            exercisesCollection.document(exercise.id)
        }
        
        documentRef.set(exercise.toMap()).await()
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error adding exercise: ${exercise.name}")
        Result.failure(e)
    }
    
    override suspend fun updateExercise(exercise: Exercise): Result<Unit> = try {
        if (exercise.id.isBlank()) {
            Result.failure(IllegalArgumentException("Exercise ID cannot be blank for updates"))
        } else {
            exercisesCollection.document(exercise.id).set(exercise.toMap()).await()
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error updating exercise: ${exercise.id}")
        Result.failure(e)
    }
    
    override suspend fun deleteExercise(id: String): Result<Unit> = try {
        if (id.isBlank()) {
            Result.failure(IllegalArgumentException("Exercise ID cannot be blank for deletion"))
        } else {
            exercisesCollection.document(id).delete().await()
            Result.success(Unit)
        }
    } catch (e: Exception) {
        Timber.e(e, "Error deleting exercise: $id")
        Result.failure(e)
    }
    
    override fun getExercises(): Flow<List<Exercise>> = callbackFlow {
        val subscription = exercisesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for exercise updates")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val exercises = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                Exercise.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping exercise document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(exercises)
                }
            }
        
        awaitClose {
            subscription.remove()
        }
    }
    
    override fun getExercisesForWorkoutType(workoutTypeId: String): Flow<List<Exercise>> = callbackFlow {
        val subscription = exercisesCollection
            .whereEqualTo("workoutTypeId", workoutTypeId)
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for exercise updates")
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val exercises = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                Exercise.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping exercise document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(exercises)
                }
            }
        
        awaitClose {
            subscription.remove()
        }
    }
} 