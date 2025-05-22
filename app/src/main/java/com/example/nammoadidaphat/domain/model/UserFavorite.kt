package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class UserFavorite(
    val favorite_id: String = "",
    val user_id: String = "",
    val plan_id: String = "",
    val created_at: Timestamp? = null
) 