package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Level(
    val id: String = "",
    val workoutTypeId: String = "",
    val name: String = "",
    val description: String = "",
    val difficulty: String = "",
    val extraDescription: String = "",
    val caloriesBurn: Int = 0,
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) 