package com.example.nammoadidaphat.domain.model

data class PlanExercise(
    val plan_exercise_id: String = "",
    val plan_id: String = "",
    val exercise_id: String = "",
    val order_index: Int = 0,
    val reps: Int = 0,
    val sets: Int = 0,
    val rest_time_seconds: Int = 0,
    val weight_default: Float = 0f,
    val notes: String = "",
    val is_warmup: Boolean = false,
    val is_cooldown: Boolean = false
) 