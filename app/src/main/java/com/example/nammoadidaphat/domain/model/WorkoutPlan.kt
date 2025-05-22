package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class WorkoutPlan(
    val plan_id: String = "",
    val level_id: String = "",
    val name: String = "",
    val description: String = "",
    val thumbnail_url: String = "",
    val estimated_duration: Int = 0,
    val estimated_calories: Int = 0,
    val equipment_needed: List<String> = emptyList(),
    val sort_order: Int = 0,
    val is_premium: Boolean = false,
    val is_active: Boolean = true,
    val created_at: Timestamp? = null
) 