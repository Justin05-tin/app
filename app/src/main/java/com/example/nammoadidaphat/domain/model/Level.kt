package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class Level(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val rank: Int = 0,
    val imageUrl: String = "",
    val color: String = "",
    val order: Int = 0,
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): Level {
            return try {
                Level(
                    id = map["id"] as? String ?: "",
                    name = map["name"] as? String ?: "",
                    description = map["description"] as? String ?: "",
                    rank = (map["rank"] as? Long)?.toInt() ?: (map["rank"] as? Int) ?: 0,
                    imageUrl = map["imageUrl"] as? String ?: "",
                    color = map["color"] as? String ?: "",
                    order = (map["order"] as? Long)?.toInt() ?: (map["order"] as? Int) ?: 0,
                    isActive = map["isActive"] as? Boolean ?: true,
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return a minimal valid level
                Level(
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
            "rank" to rank,
            "imageUrl" to imageUrl,
            "color" to color,
            "order" to order,
            "isActive" to isActive,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 