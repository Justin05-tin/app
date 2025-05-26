package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val icon: String = "",
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) 