package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserWorkout(
    val workout_id: String = "",
    val user_id: String = "",
    val plan_id: String = "",
    val start_time: Timestamp? = null,
    val end_time: Timestamp? = null,
    val total_duration: Int = 0,
    val calories_burned: Int = 0,
    val exercises_completed: Int = 0,
    val completion_status: String = "",
    val completion_rating: Int = 0,
    val notes: String = "",
    val difficulty_rating: Int = 0,
    val created_at: Timestamp? = null
) 