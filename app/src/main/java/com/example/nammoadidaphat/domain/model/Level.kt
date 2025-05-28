package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Level(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val durationMinutes: Int = 0,
    val caloriesBurn: Int = 0,
    val workoutTypeId: String = "",
    val imageUrl: String = "",
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Level {
            return try {
                // Log the map keys and values for debugging
                val mapStr = map.entries.joinToString { "${it.key}=${it.value}" }
                android.util.Log.d("Level", "Creating Level from map: $mapStr")
                
                Level(
                    id = map["id"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    description = map["description"] as? String ?: "",
                    difficulty = map["difficulty"] as? String ?: "",
                    durationMinutes = (map["durationMinutes"] as? Long)?.toInt() 
                        ?: (map["durationMinutes"] as? Int) ?: 0,
                    caloriesBurn = (map["caloriesBurn"] as? Long)?.toInt() 
                        ?: (map["caloriesBurn"] as? Int) ?: 0,
                    workoutTypeId = map["workoutTypeId"] as? String ?: "",
                    imageUrl = map["imageUrl"] as? String ?: map["image"] as? String ?: "",
                    order = (map["order"] as? Long)?.toInt() ?: (map["order"] as? Int) ?: 0,
                    isActive = map["isActive"] as? Boolean ?: true,
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                android.util.Log.e("Level", "Error creating Level from map: ${e.message}")
                // If any exception occurs during mapping, return a minimal valid level
                Level(
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
            "difficulty" to difficulty,
            "durationMinutes" to durationMinutes,
            "caloriesBurn" to caloriesBurn,
            "workoutTypeId" to workoutTypeId,
            "imageUrl" to imageUrl,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 