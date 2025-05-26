package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserProgress(
    val id: String = "",
    val userId: String = "",
    val date: Timestamp? = null,
    val weight: Float? = null,
    val height: Int? = null,
    val bmi: Float? = null,
    val bodyFat: Float? = null,
    val workoutSessionId: String = "",
    val workoutTypeId: String = "",
    val levelId: String = "",
    val workoutDuration: Int = 0, // in seconds
    val caloriesBurned: Int = 0,
    val notes: String = "",
    val mood: String = "", // "great", "good", "neutral", "bad", "terrible"
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): UserProgress {
            return try {
                UserProgress(
                    id = map["id"] as? String ?: "",
                    userId = map["userId"] as? String ?: "",
                    date = map["date"] as? Timestamp,
                    weight = (map["weight"] as? Double)?.toFloat() ?: (map["weight"] as? Float),
                    height = (map["height"] as? Long)?.toInt() ?: (map["height"] as? Int),
                    bmi = (map["bmi"] as? Double)?.toFloat() ?: (map["bmi"] as? Float),
                    bodyFat = (map["bodyFat"] as? Double)?.toFloat() ?: (map["bodyFat"] as? Float),
                    workoutSessionId = map["workoutSessionId"] as? String ?: "",
                    workoutTypeId = map["workoutTypeId"] as? String ?: "",
                    levelId = map["levelId"] as? String ?: "",
                    workoutDuration = (map["workoutDuration"] as? Long)?.toInt() ?: (map["workoutDuration"] as? Int) ?: 0,
                    caloriesBurned = (map["caloriesBurned"] as? Long)?.toInt() ?: (map["caloriesBurned"] as? Int) ?: 0,
                    notes = map["notes"] as? String ?: "",
                    mood = map["mood"] as? String ?: "",
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return a minimal valid user progress
                UserProgress(
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
            "date" to date,
            "weight" to weight,
            "height" to height,
            "bmi" to bmi,
            "bodyFat" to bodyFat,
            "workoutSessionId" to workoutSessionId,
            "workoutTypeId" to workoutTypeId,
            "levelId" to levelId,
            "workoutDuration" to workoutDuration,
            "caloriesBurned" to caloriesBurned,
            "notes" to notes,
            "mood" to mood,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 