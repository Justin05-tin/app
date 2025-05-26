package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class WorkoutType(
    val id: String = "",
    val categoryId: String = "",
    val name: String = "",
    val description: String = "",
    val duration: String = "",
    val difficulty: String = "",
    val image: String = "",
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) 