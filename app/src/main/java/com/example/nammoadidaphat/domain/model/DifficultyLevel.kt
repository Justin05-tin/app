package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class DifficultyLevel(
    val level_id: String = "",
    val sub_category_id: String = "",
    val name: String = "",
    val description: String = "",
    val icon_url: String = "",
    val color: String = "",
    val level_requirements: String = "",
    val is_active: Boolean = true,
    val created_at: Timestamp? = null
) 