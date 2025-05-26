package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class WorkoutSession(
    val id: String = "",
    val userId: String = "",
    val levelId: String = "",
    val startTime: Timestamp? = null,
    val endTime: Timestamp? = null,
    val duration: Int = 0,
    val totalCaloriesBurned: Int = 0,
    val completedExercises: List<String> = emptyList(),
    val status: String = "pending", // "pending", "completed", "cancelled"
    val notes: String = ""
) 