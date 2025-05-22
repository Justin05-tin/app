package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class SubCategory(
    val sub_category_id: String = "",
    val category_id: String = "",
    val name: String = "",
    val name_en: String = "",
    val description: String = "",
    val target_body_parts: List<String> = emptyList(),
    val color: String = "",
    val icon_url: String = "",
    val sort_order: Int = 0,
    val is_active: Boolean = true,
    val created_at: Timestamp? = null
) 