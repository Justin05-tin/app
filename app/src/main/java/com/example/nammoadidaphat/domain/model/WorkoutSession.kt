package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class WorkoutSession(
    val id: String = "",
    val userId: String = "",
    val workoutTypeId: String = "",
    val levelId: String = "",
    val title: String = "",
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val duration: Int = 0, // in seconds
    val caloriesBurned: Int = 0,
    val exercises: List<String> = emptyList(),
    val completed: Boolean = false,
    val notes: String = "",
    val rating: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): WorkoutSession {
            return try {
                WorkoutSession(
                    id = map["id"] as? String ?: "",
                    userId = map["userId"] as? String ?: "",
                    workoutTypeId = map["workoutTypeId"] as? String ?: "",
                    levelId = map["levelId"] as? String ?: "",
                    title = map["title"] as? String ?: "",
                    startTime = map["startTime"] as? Timestamp,
                    endTime = map["endTime"] as? Timestamp,
                    duration = (map["duration"] as? Long)?.toInt() ?: (map["duration"] as? Int) ?: 0,
                    caloriesBurned = (map["caloriesBurned"] as? Long)?.toInt() ?: (map["caloriesBurned"] as? Int) ?: 0,
                    exercises = map["exercises"] as? List<String> ?: emptyList(),
                    completed = map["completed"] as? Boolean ?: false,
                    notes = map["notes"] as? String ?: "",
                    rating = (map["rating"] as? Long)?.toInt() ?: (map["rating"] as? Int) ?: 0,
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return a minimal valid workout session
                WorkoutSession(
                    id = map["id"] as? String ?: "",
                    userId = map["userId"] as? String ?: ""
                )
            }
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "userId" to userId,
            "workoutTypeId" to workoutTypeId,
            "levelId" to levelId,
            "title" to title,
            "startTime" to startTime,
            "endTime" to endTime,
            "duration" to duration,
            "caloriesBurned" to caloriesBurned,
            "exercises" to exercises,
            "completed" to completed,
            "notes" to notes,
            "rating" to rating,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 