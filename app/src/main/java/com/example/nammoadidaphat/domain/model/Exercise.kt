package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Exercise(
    val id: String = "",
    val levelId: String = "",
    val name: String = "",
    val description: String = "",
    val instructions: List<String> = emptyList(),
    val duration: Int = 0,
    val reps: Int = 0,
    val sets: Int = 0,
    val restTime: Int = 0,
    val image: String = "",
    val video: String = "",
    val tips: List<String> = emptyList(),
    val muscleGroups: List<String> = emptyList(),
    val equipment: String = "",
    val caloriesBurn: Int = 0,
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) 