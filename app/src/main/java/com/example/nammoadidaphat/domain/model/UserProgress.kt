package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserProgress(
    val id: String = "",
    val userId: String = "",
    val levelId: String = "",
    val exerciseId: String = "",
    val completionDate: Timestamp? = null,
    val duration: Int = 0,
    val repsCompleted: Int = 0,
    val setsCompleted: Int = 0,
    val caloriesBurned: Int = 0,
    val difficulty: String = "",
    val notes: String = "",
    val createdAt: Timestamp? = null
) 