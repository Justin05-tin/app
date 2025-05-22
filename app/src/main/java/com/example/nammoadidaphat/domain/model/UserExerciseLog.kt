package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserExerciseLog(
    val log_id: String = "",
    val workout_id: String = "",
    val exercise_id: String = "",
    val order_complete: Int = 0,
    val duration_seconds: Int = 0,
    val reps_completed: Int = 0,
    val sets_completed: Int = 0,
    val weight_used: Float = 0f,
    val rest_time_taken: Int = 0,
    val calories_burned: Int = 0,
    val difficulty_felt: Int = 0,
    val notes: String = "",
    val completed_at: Timestamp? = null
) 