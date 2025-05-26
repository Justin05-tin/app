package com.example.nammoadidaphat.domain.model

import com.google.firebase.Timestamp

data class AdminSettings(
    val id: String = "app_settings",
    val appVersion: String = "1.0.0",
    val adminEmailList: List<String> = emptyList(),
    val featureMode: Map<String, Boolean> = emptyMap(),
    val notifications: Map<String, Any> = emptyMap(),
    val privacyPolicyUrl: String = "",
    val termsOfServiceUrl: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
) {
    companion object {
        @Suppress("UNCHECKED_CAST")
        fun fromMap(map: Map<String, Any?>): AdminSettings {
            return try {
                AdminSettings(
                    id = map["id"] as? String ?: "app_settings",
                    appVersion = map["appVersion"] as? String ?: "1.0.0",
                    adminEmailList = map["adminEmailList"] as? List<String> ?: emptyList(),
                    featureMode = map["featureMode"] as? Map<String, Boolean> ?: emptyMap(),
                    notifications = map["notifications"] as? Map<String, Any> ?: emptyMap(),
                    privacyPolicyUrl = map["privacyPolicyUrl"] as? String ?: "",
                    termsOfServiceUrl = map["termsOfServiceUrl"] as? String ?: "",
                    createdAt = map["createdAt"] as? Timestamp,
                    updatedAt = map["updatedAt"] as? Timestamp
                )
            } catch (e: Exception) {
                // If any exception occurs during mapping, return default settings
                AdminSettings()
            }
        }
    }
    
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "appVersion" to appVersion,
            "adminEmailList" to adminEmailList,
            "featureMode" to featureMode,
            "notifications" to notifications,
            "privacyPolicyUrl" to privacyPolicyUrl,
            "termsOfServiceUrl" to termsOfServiceUrl,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
} 