package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class MainCategory(
    val category_id: String = "",
    val name: String = "",
    val name_en: String = "",
    val description: String = "",
    val icon_url: String = "",
    val color: String = "",
    val sort_order: Int = 0,
    val is_active: Boolean = true,
    val updated_at: Timestamp? = null
) 