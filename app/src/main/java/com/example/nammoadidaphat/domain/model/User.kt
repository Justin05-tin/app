package com.example.nammoadidaphat.domain.model

data class User(
    val userId: String = "",
    val email: String = "",
    val passwordHash: String = "",
    val fullName: String = "",
    val avatarUrl: String = "",
    val age: Int? = null,
    val gender: String = "",
    val height: Int? = null,
    val weight: Float? = null,
    val fitnessLevel: String = "",
    val goals: String = "",
    val isPremium: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): User {
            return User(
                userId = map["user_id"] as? String ?: "",
                email = map["email"] as? String ?: "",
                passwordHash = map["password_hash"] as? String ?: "",
                fullName = map["full_name"] as? String ?: "",
                avatarUrl = map["avatar_url"] as? String ?: "",
                age = (map["age"] as? Long)?.toInt() ?: (map["age"] as? Int),
                gender = map["gender"] as? String ?: "",
                height = (map["height"] as? Long)?.toInt() ?: (map["height"] as? Int),
                weight = (map["weight"] as? Double)?.toFloat() ?: (map["weight"] as? Float),
                fitnessLevel = map["fitness_level"] as? String ?: "",
                goals = map["goals"] as? String ?: "",
                isPremium = map["is_premium"] as? Boolean ?: false,
                createdAt = map["created_at"] as? String ?: "",
                updatedAt = map["updated_at"] as? String ?: ""
            )
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "user_id" to userId,
            "email" to email,
            "password_hash" to passwordHash,
            "full_name" to fullName,
            "avatar_url" to avatarUrl,
            "age" to age,
            "gender" to gender,
            "height" to height,
            "weight" to weight,
            "fitness_level" to fitnessLevel,
            "goals" to goals,
            "is_premium" to isPremium,
            "created_at" to createdAt,
            "updated_at" to updatedAt
        )
    }
} 