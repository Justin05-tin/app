package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Exercise(
    val exercise_id: String = "",
    val name: String = "",
    val name_en: String = "",
    val description: String = "",
    val instructions: List<String> = emptyList(),
    val thumbnail_url: String = "",
    val video_url: String = "",
    val gif_url: String = "",
    val target_muscles: List<String> = emptyList(),
    val equipment_needed: List<String> = emptyList(),
    val calories_per_minute: Int = 0,
    val is_time_default: Boolean = false,
    val is_reps_based: Boolean = true,
    val created_at: Timestamp? = null,
    val updated_at: Timestamp? = null
) 