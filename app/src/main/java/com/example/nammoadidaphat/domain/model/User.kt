package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp
import timber.log.Timber

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
                // First log map for debugging purposes
                Timber.d("Parsing user data: $map")
                
                // Helper function to safely get a string
                fun safeString(key: String): String {
                    val value = map[key]
                    return when (value) {
                        is String -> value
                        null -> ""
                        else -> value.toString()
                    }
                }
                
                // Helper function to safely get an int from various possible types
                fun safeInt(key: String): Int? {
                    val value = map[key]
                    return when (value) {
                        is Int -> value
                        is Long -> value.toInt()
                        is Double -> value.toInt()
                        is String -> value.toIntOrNull()
                        null -> null
                        else -> null
                    }
                }
                
                // Helper function to safely get a float from various possible types
                fun safeFloat(key: String): Float? {
                    val value = map[key]
                    return when (value) {
                        is Float -> value
                        is Double -> value.toFloat()
                        is Int -> value.toFloat()
                        is Long -> value.toFloat()
                        is String -> value.toFloatOrNull()
                        null -> null
                        else -> null
                    }
                }
                
                // Helper function to safely get a timestamp
                fun safeTimestamp(key: String): Timestamp? {
                    val value = map[key]
                    return when (value) {
                        is Timestamp -> value
                        null -> null
                        else -> null
                    }
                }
                
                // Helper function to safely get a list of strings
                @Suppress("UNCHECKED_CAST")
                fun safeStringList(key: String): List<String> {
                    val value = map[key]
                    return when (value) {
                        is List<*> -> {
                            value.filterIsInstance<String>()
                        }
                        is String -> listOf(value)
                        null -> emptyList()
                        else -> emptyList()
                    }
                }
                
                // Helper function to safely get a map
                @Suppress("UNCHECKED_CAST")
                fun safeMap(key: String): Map<String, Any> {
                    val value = map[key]
                    return when (value) {
                        is Map<*, *> -> {
                            value.entries.associate { (k, v) -> (k?.toString() ?: "") to (v ?: "") }
                        }
                        null -> emptyMap()
                        else -> emptyMap()
                    }
                }
                
                User(
                    id = safeString("id"),
                    email = safeString("email"),
                    displayName = safeString("displayName"),
                    avatar = safeString("avatar"),
                    age = safeInt("age"),
                    gender = safeString("gender"),
                    height = safeInt("height"),
                    weight = safeFloat("weight"),
                    fitnessLevel = safeString("fitnessLevel"),
                    goals = safeStringList("goals"),
                    preferences = safeMap("preferences"),
                    createdAt = safeTimestamp("createdAt"),
                    updatedAt = safeTimestamp("updatedAt"),
                    authProvider = safeString("authProvider").ifEmpty { "password" }
                )
            } catch (e: Exception) {
                Timber.e(e, "Exception creating User from map: ${e.message}")
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
        // Log avatar value before creating map
        Timber.d("Converting User to Map - Avatar value: '$avatar'")
        
        val map = mapOf(
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
        
        // Log the full map to verify all fields
        Timber.d("Full user map for Firestore: $map")
        return map
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