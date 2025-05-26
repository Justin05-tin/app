package com.example.nammoadidaphat.domain.model

data class AdminSettings(
    val id: String = "",
    val appVersion: String = "",
    val adminEmailList: List<String> = emptyList(),
    val featureModeSetting: Map<String, Boolean> = emptyMap(),
    val notifications: Map<String, Any> = emptyMap()
) 