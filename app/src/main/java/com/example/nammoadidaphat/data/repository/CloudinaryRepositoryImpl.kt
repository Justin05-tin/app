package com.example.nammoadidaphat.data.repository

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.nammoadidaphat.domain.repository.CloudinaryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import kotlin.coroutines.resume

class CloudinaryRepositoryImpl @Inject constructor(
    private val context: Context
) : CloudinaryRepository {
    
    companion object {
        // Flag to track if Cloudinary is already initialized
        private var isCloudinaryInitialized = false
        
        // Cloudinary config
        private const val CLOUD_NAME = "dphvpkczy"
        private const val UPLOAD_PRESET = "exc-app"
    }
    
    init {
        initCloudinary()
    }
    
    private fun initCloudinary() {
        if (!isCloudinaryInitialized) {
            try {
                val config = mapOf(
                    "cloud_name" to CLOUD_NAME
                    // No API key or secret needed for unsigned uploads
                )
                MediaManager.init(context, config)
                isCloudinaryInitialized = true
                Timber.d("Cloudinary initialized successfully")
            } catch (e: Exception) {
                Timber.e(e, "Failed to initialize Cloudinary")
            }
        }
    }
    
    override suspend fun uploadImage(imageUri: Uri): Result<String> = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            try {
                // Convert Uri to File
                val file = uriToFile(imageUri)
                if (file == null) {
                    continuation.resume(Result.failure(Exception("Failed to create file from URI")))
                    return@suspendCancellableCoroutine
                }
                
                // Use unsigned upload with upload preset
                val requestId = MediaManager.get().upload(file.absolutePath)
                    .unsigned(UPLOAD_PRESET)  // This is the upload preset configured in Cloudinary dashboard
                    .option("folder", "profile_images")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String?) {
                            Timber.d("Started uploading image to Cloudinary")
                        }
                        
                        override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                            val progress = (bytes.toDouble() / totalBytes.toDouble() * 100).toInt()
                            Timber.d("Upload progress: $progress%")
                        }
                        
                        override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                            val url = resultData?.get("secure_url") as? String
                            if (url != null) {
                                Timber.d("Upload successful, URL: $url")
                                continuation.resume(Result.success(url))
                            } else {
                                Timber.e("Upload successful but URL is null")
                                continuation.resume(Result.failure(Exception("Failed to get image URL")))
                            }
                        }
                        
                        override fun onError(requestId: String?, error: ErrorInfo?) {
                            Timber.e("Upload error: ${error?.description}")
                            continuation.resume(Result.failure(Exception(error?.description ?: "Unknown error")))
                        }
                        
                        override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                            Timber.w("Upload rescheduled: ${error?.description}")
                        }
                    })
                    .dispatch()
                
                continuation.invokeOnCancellation {
                    Timber.d("Cancelling upload: $requestId")
                    MediaManager.get().cancelRequest(requestId)
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Exception during image upload")
                continuation.resume(Result.failure(e))
            }
        }
    }
    
    private fun uriToFile(uri: Uri): File? {
        return try {
            val stream: InputStream? = context.contentResolver.openInputStream(uri)
            if (stream != null) {
                val tempFile = File(context.cacheDir, "temp_profile_image_${System.currentTimeMillis()}.jpg")
                val output = FileOutputStream(tempFile)
                stream.copyTo(output)
                stream.close()
                output.close()
                tempFile
            } else {
                Timber.e("Failed to open input stream for URI: $uri")
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Error converting URI to file: $uri")
            null
        }
    }
} 