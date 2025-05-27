package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val avatar: String = "",
    val age: Int? = null,
    val gender: String = "",
    val height: Int? = null,
    val weight: Float? = null,
    val fitnessLevel: String = "",
    val goals: List<String> = emptyList(),
    val preferences: Map<String, Any> = emptyMap(),
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val authProvider: String = "password" // "password", "google.com", "facebook.com"
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): User {
            return try {
                User(
                    id = map["id"] as? String ?: "",
                    email = map["email"] as? String ?: "",
                    displayName = map["displayName"] as? String ?: "",
                    avatar = map["avatar"] as? String ?: "",
                    age = (map["age"] as? Long)?.toInt() ?: (map["age"] as? Int),
                    gender = map["gender"] as? String ?: "",
                    height = (map["height"] as? Long)?.toInt() ?: (map["height"] as? Int),
                    weight = (map["weight"] as? Double)?.toFloat() ?: (map["weight"] as? Float),
                    fitnessLevel = map["fitnessLevel"] as? String ?: "",
                    goals = map["goals"] as? List<String> ?: emptyList(),
                    preferences = map["preferences"] as? Map<String, Any> ?: emptyMap(),
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp,
                    authProvider = map["authProvider"] as? String ?: "password"
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return a minimal valid user
                User(
                    id = map["id"] as? String ?: "",
                    email = map["email"] as? String ?: "",
                    authProvider = map["authProvider"] as? String ?: "password"
                )
            }
        }
        
        // Create a safe user object with just the essential fields
        fun createMinimalUser(userId: String, email: String, authProvider: String = "password"): User {
            return User(
                id = userId,
                email = email,
                authProvider = authProvider
            )
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "email" to email,
            "displayName" to displayName,
            "avatar" to avatar,
            "age" to age,
            "gender" to gender,
            "height" to height,
            "weight" to weight,
            "fitnessLevel" to fitnessLevel,
            "goals" to goals,
            "preferences" to preferences,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt,
            "authProvider" to authProvider
        )
    }
    
    // Check if all required profile fields are present
    fun hasCompleteProfile(): Boolean {
        return displayName.isNotBlank() && 
               gender.isNotBlank() && 
               age != null && 
               height != null && 
               weight != null && 
               fitnessLevel.isNotBlank()
    }
} 