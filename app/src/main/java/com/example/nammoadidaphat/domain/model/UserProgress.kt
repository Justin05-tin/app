package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserProgress(
    val progress_id: String = "",
    val user_id: String = "",
    val date: Timestamp? = null,
    val weight: Float = 0f,
    val body_measurements: Map<String, Float> = emptyMap(),
    val photos: List<String> = emptyList(),
    val notes: String = "",
    val created_at: Timestamp? = null
) 