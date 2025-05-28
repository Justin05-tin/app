package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Category {
            return try {
                Category(
                    id = map["id"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    description = map["description"] as? String ?: "",
                    imageUrl = map["imageUrl"] as? String ?: "",
                    order = (map["order"] as? Long)?.toInt() ?: (map["order"] as? Int) ?: 0,
                    isActive = map["isActive"] as? Boolean ?: true,
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return a minimal valid category
                Category(
                    id = map["id"] as? String ?: ""
                )
            }
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "imageUrl" to imageUrl,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 