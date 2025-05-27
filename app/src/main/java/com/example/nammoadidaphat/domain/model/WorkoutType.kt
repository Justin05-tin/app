package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class WorkoutType(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val categoryId: String = "",
    val difficulty: String? = null,
    val duration: String? = null,
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): WorkoutType {
            return try {
                // Log the map keys and values for debugging
                val mapStr = map.entries.joinToString { "${it.key}=${it.value}" }
                android.util.Log.d("WorkoutType", "Creating WorkoutType from map: $mapStr")
                
                // Ensure we check all possible field names for categoryId
                val categoryId = when {
                    map.containsKey("categoryId") -> map["categoryId"] as? String ?: ""
                    map.containsKey("category_id") -> map["category_id"] as? String ?: ""
                    map.containsKey("categoryid") -> map["categoryid"] as? String ?: ""
                    else -> ""
                }
                
                android.util.Log.d("WorkoutType", "Extracted categoryId: $categoryId")
                
                WorkoutType(
                    id = map["id"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    description = map["description"] as? String ?: "",
                    imageUrl = map["imageUrl"] as? String ?: map["image"] as? String ?: "",
                    categoryId = categoryId,
                    difficulty = map["difficulty"] as? String,
                    duration = map["duration"] as? String,
                    order = (map["order"] as? Long)?.toInt() ?: (map["order"] as? Int) ?: 0,
                    isActive = map["isActive"] as? Boolean ?: true,
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                android.util.Log.e("WorkoutType", "Error creating WorkoutType from map: ${e.message}")
                // If any exception occurs during mapping, return a minimal valid workout type
                WorkoutType(
                    id = map["id"] as? String ?: ""
                )
            }
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "imageUrl" to imageUrl,
            "categoryId" to categoryId,
            "difficulty" to difficulty,
            "duration" to duration,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 