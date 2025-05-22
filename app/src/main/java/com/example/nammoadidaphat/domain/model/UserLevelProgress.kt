package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserLevelProgress(
    val progress_id: String = "",
    val user_id: String = "",
    val level_id: String = "",
    val plans_completed: Int = 0,
    val total_workouts: Int = 0,
    val best_time: Int = 0,
    val stars_earned: Int = 0,
    val last_workout_at: Timestamp? = null
) 