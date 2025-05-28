package com.example.nammoadidaphat.presentation.ui.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.example.nammoadidaphat.domain.repository.CloudinaryRepository
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingImage: Boolean = false,
    val uploadProgress: Float = 0f,
    val error: String? = null,
    val user: User? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cloudinaryRepository: CloudinaryRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EditProfileUiState(isLoading = true))
    val uiState: StateFlow<EditProfileUiState> = _uiState
    
    // Form fields
    var displayName by mutableStateOf("")
    var gender by mutableStateOf("")
    var age by mutableStateOf("")
    var height by mutableStateOf("")
    var weight by mutableStateOf("")
    var fitnessLevel by mutableStateOf("")
    
    // Dropdown states
    var isGenderMenuExpanded by mutableStateOf(false)
    var isFitnessLevelMenuExpanded by mutableStateOf(false)
    
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val currentUser = authRepository.getCurrentUser().first()
                if (currentUser != null) {
                    // Populate form fields with existing user data
                    displayName = currentUser.displayName
                    gender = currentUser.gender
                    age = currentUser.age?.toString() ?: ""
                    height = currentUser.height?.toString() ?: ""
                    weight = currentUser.weight?.toString() ?: ""
                    fitnessLevel = currentUser.fitnessLevel
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = currentUser,
                        error = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not found"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load user profile")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load profile: ${e.message}"
                )
            }
        }
    }
    
    fun saveUserProfile(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isSaving = true, error = null)
                
                val currentUser = _uiState.value.user
                if (currentUser != null) {
                    // Create updated user object
                    val updatedUser = currentUser.copy(
                        displayName = displayName.trim(),
                        gender = gender,
                        age = age.toIntOrNull(),
                        height = height.toIntOrNull(),
                        weight = weight.toFloatOrNull(),
                        fitnessLevel = fitnessLevel,
                        updatedAt = Timestamp.now()
                    )
                    
                    // Update user profile in Firestore
                    val result = authRepository.updateUserProfile(updatedUser)
                    
                    if (result.isSuccess) {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            user = updatedUser,
                            error = null
                        )
                        onComplete(true)
                    } else {
                        val error = result.exceptionOrNull()
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            error = "Failed to save profile: ${error?.message}"
                        )
                        onComplete(false)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        error = "No user data to update"
                    )
                    onComplete(false)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to save user profile")
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = "Failed to save profile: ${e.message}"
                )
                onComplete(false)
            }
        }
    }
    
    fun uploadProfileImage(imageUri: Uri, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = true,
                    uploadProgress = 0f,
                    error = null
                )
                
                val currentUser = _uiState.value.user ?: run {
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        error = "User not found"
                    )
                    onComplete(false)
                    return@launch
                }
                
                // Upload image to Cloudinary
                Timber.d("Starting image upload to Cloudinary")
                val uploadResult = cloudinaryRepository.uploadImage(imageUri)
                
                if (uploadResult.isSuccess) {
                    val imageUrl = uploadResult.getOrNull()
                    if (imageUrl != null) {
                        // Update user avatar in Firestore
                        Timber.d("Image uploaded successfully, updating user avatar: $imageUrl")
                        val updateResult = authRepository.updateUserAvatar(currentUser.id, imageUrl)
                        
                        if (updateResult.isSuccess) {
                            // Update local user state
                            val updatedUser = currentUser.copy(avatar = imageUrl)
                            _uiState.value = _uiState.value.copy(
                                isUploadingImage = false,
                                uploadProgress = 1f,
                                user = updatedUser,
                                error = null
                            )
                            onComplete(true)
                        } else {
                            val error = updateResult.exceptionOrNull()
                            _uiState.value = _uiState.value.copy(
                                isUploadingImage = false,
                                error = "Failed to update avatar: ${error?.message}"
                            )
                            onComplete(false)
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            error = "Failed to get image URL"
                        )
                        onComplete(false)
                    }
                } else {
                    val error = uploadResult.exceptionOrNull()
                    _uiState.value = _uiState.value.copy(
                        isUploadingImage = false,
                        error = "Failed to upload image: ${error?.message}"
                    )
                    onComplete(false)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to upload profile image")
                _uiState.value = _uiState.value.copy(
                    isUploadingImage = false,
                    error = "Failed to upload image: ${e.message}"
                )
                onComplete(false)
            }
        }
    }
} 