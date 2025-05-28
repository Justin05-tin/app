package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Exercise(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val video: String = "",
    val workoutTypeId: String = "",
    val levelId: String = "",
    val duration: Int = 0, // in seconds
    val reps: Int = 0,
    val restTime: Int = 0,
    val caloriesBurn: Int = 0,
    val instructions: List<String> = emptyList(),
    val tips: List<String> = emptyList(),
    val equipment: String = "",
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
            return Exercise(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                image = map["image"] as? String ?: "",
                video = map["video"] as? String ?: "",
                workoutTypeId = map["workoutTypeId"] as? String ?: "",
                levelId = map["levelId"] as? String ?: "",
                duration = (map["duration"] as? Number)?.toInt() ?: 0,
                reps = (map["reps"] as? Number)?.toInt() ?: 0,
                restTime = (map["restTime"] as? Number)?.toInt() ?: 0,
                caloriesBurn = (map["caloriesBurn"] as? Number)?.toInt() ?: 0,
                instructions = map["instructions"] as? List<String> ?: emptyList(),
                tips = map["tips"] as? List<String> ?: emptyList(),
                equipment = map["equipment"] as? String ?: "",
                equipmentNeeded = map["equipmentNeeded"] as? List<String> ?: emptyList(),
                muscleGroups = map["muscleGroups"] as? List<String> ?: emptyList(),
                order = (map["order"] as? Number)?.toInt() ?: 0,
                isActive = map["isActive"] as? Boolean ?: true,
                createdAt = map["createdAt"] as? Timestamp,
                updatedAt = map["updatedAt"] as? Timestamp
            )
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "image" to image,
            "video" to video,
            "workoutTypeId" to workoutTypeId,
            "levelId" to levelId,
            "duration" to duration,
            "reps" to reps,
            "restTime" to restTime,
            "caloriesBurn" to caloriesBurn,
            "instructions" to instructions,
            "tips" to tips,
            "equipment" to equipment,
            "equipmentNeeded" to equipmentNeeded,
            "muscleGroups" to muscleGroups,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 