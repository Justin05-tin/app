package com.example.nammoadidaphat.domain.repository

import android.net.Uri

interface CloudinaryRepository {
    suspend fun uploadImage(imageUri: Uri): Result<String>
} 