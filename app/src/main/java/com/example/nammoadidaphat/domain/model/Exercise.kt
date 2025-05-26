package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Exercise(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val videoUrl: String = "",
    val workoutTypeId: String = "",
    val levelId: String = "",
    val duration: Int = 0, // in seconds
    val caloriesBurn: Int = 0,
    val instructions: List<String> = emptyList(),
    val equipmentNeeded: List<String> = emptyList(),
    val muscleGroups: List<String> = emptyList(),
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Exercise {
            return try {
                Exercise(
                    id = map["id"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    description = map["description"] as? String ?: "",
                    imageUrl = map["imageUrl"] as? String ?: "",
                    videoUrl = map["videoUrl"] as? String ?: "",
                    workoutTypeId = map["workoutTypeId"] as? String ?: "",
                    levelId = map["levelId"] as? String ?: "",
                    duration = (map["duration"] as? Long)?.toInt() ?: (map["duration"] as? Int) ?: 0,
                    caloriesBurn = (map["caloriesBurn"] as? Long)?.toInt() ?: (map["caloriesBurn"] as? Int) ?: 0,
                    instructions = map["instructions"] as? List<String> ?: emptyList(),
                    equipmentNeeded = map["equipmentNeeded"] as? List<String> ?: emptyList(),
                    muscleGroups = map["muscleGroups"] as? List<String> ?: emptyList(),
                    order = (map["order"] as? Long)?.toInt() ?: (map["order"] as? Int) ?: 0,
                    isActive = map["isActive"] as? Boolean ?: true,
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return a minimal valid exercise
                Exercise(
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
            "videoUrl" to videoUrl,
            "workoutTypeId" to workoutTypeId,
            "levelId" to levelId,
            "duration" to duration,
            "caloriesBurn" to caloriesBurn,
            "instructions" to instructions,
            "equipmentNeeded" to equipmentNeeded,
            "muscleGroups" to muscleGroups,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 