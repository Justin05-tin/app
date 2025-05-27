package com.example.nammoadidaphat.data.repository

import com.example.nammoadidaphat.domain.model.Category
import com.example.nammoadidaphat.domain.repository.CategoryRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CategoryRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : CategoryRepository {

    private val categoriesCollection = firestore.collection("categories")
    
    override suspend fun getAllCategories(): Result<List<Category>> = try {
        val snapshot = categoriesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .get()
            .await()
            
        val categories = snapshot.documents.mapNotNull { doc ->
            try {
                val data = doc.data
                if (data != null) {
                    // Add the document ID to the map before conversion
                    val dataWithId = data.toMutableMap().apply {
                        put("id", doc.id)
                    }
                    Category.fromMap(dataWithId)
                } else null
            } catch (e: Exception) {
                Timber.e(e, "Error mapping category document ${doc.id}")
                null
            }
        }
        
        Result.success(categories)
    } catch (e: Exception) {
        Timber.e(e, "Error getting all categories")
        Result.failure(e)
    }

    override suspend fun getCategoryById(id: String): Result<Category> = try {
        val doc = categoriesCollection.document(id).get().await()
        
        if (doc.exists()) {
            val data = doc.data
            if (data != null) {
                // Add the document ID to the map before conversion
                val dataWithId = data.toMutableMap().apply {
                    put("id", doc.id)
                }
                val category = Category.fromMap(dataWithId)
                Result.success(category)
            } else {
                Result.failure(Exception("Category data is null"))
            }
        } else {
            Result.failure(Exception("Category not found"))
        }
    } catch (e: Exception) {
        Timber.e(e, "Error getting category by ID: $id")
        Result.failure(e)
    }

    override suspend fun addCategory(category: Category): Result<Unit> = try {
        val dataToAdd = category.toMap().toMutableMap().apply {
            put("createdAt", Timestamp.now())
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        if (category.id.isNotEmpty()) {
            // If ID is provided, use it as document ID
            categoriesCollection.document(category.id).set(dataToAdd).await()
        } else {
            // Otherwise, let Firestore generate an ID
            categoriesCollection.add(dataToAdd).await()
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error adding category")
        Result.failure(e)
    }

    override suspend fun updateCategory(category: Category): Result<Unit> = try {
        val dataToUpdate = category.toMap().toMutableMap().apply {
            put("updatedAt", Timestamp.now())
            // Don't include ID in the document data
            remove("id")
        }
        
        categoriesCollection.document(category.id).update(dataToUpdate).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error updating category: ${category.id}")
        Result.failure(e)
    }

    override suspend fun deleteCategory(id: String): Result<Unit> = try {
        categoriesCollection.document(id).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Error deleting category: $id")
        Result.failure(e)
    }

    override fun getCategories(): Flow<List<Category>> = callbackFlow {
        val listenerRegistration = categoriesCollection
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening for category updates")
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val categories = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                // Add the document ID to the map before conversion
                                val dataWithId = data.toMutableMap().apply {
                                    put("id", doc.id)
                                }
                                Category.fromMap(dataWithId)
                            } else null
                        } catch (e: Exception) {
                            Timber.e(e, "Error mapping category document ${doc.id}")
                            null
                        }
                    }
                    
                    trySend(categories)
                }
            }
            
        // Clean up the listener when the flow is cancelled
        awaitClose {
            listenerRegistration.remove()
        }
    }
}