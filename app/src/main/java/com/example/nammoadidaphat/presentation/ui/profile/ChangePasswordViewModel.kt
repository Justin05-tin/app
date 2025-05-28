package com.example.nammoadidaphat.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val currentPasswordError: String? = null,
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()
    
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        // Reset previous errors
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            error = null,
            currentPasswordError = null,
            newPasswordError = null,
            confirmPasswordError = null,
            isSuccess = false
        )
        
        // Validation
        var hasError = false
        
        if (currentPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                currentPasswordError = "Current password is required"
            )
            hasError = true
        }
        
        if (newPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = "New password is required"
            )
            hasError = true
        } else if (newPassword.length < 6) {
            _uiState.value = _uiState.value.copy(
                newPasswordError = "Password must be at least 6 characters"
            )
            hasError = true
        }
        
        if (confirmPassword.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                confirmPasswordError = "Confirm password is required"
            )
            hasError = true
        } else if (confirmPassword != newPassword) {
            _uiState.value = _uiState.value.copy(
                confirmPasswordError = "Passwords don't match"
            )
            hasError = true
        }
        
        if (hasError) {
            _uiState.value = _uiState.value.copy(isLoading = false)
            return
        }
        
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                
                if (currentUser != null && currentUser.email != null) {
                    // Re-authenticate user
                    val credential = EmailAuthProvider.getCredential(currentUser.email!!, currentPassword)
                    
                    currentUser.reauthenticate(credential)
                        .addOnSuccessListener {
                            // Change password
                            currentUser.updatePassword(newPassword)
                                .addOnSuccessListener {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        isSuccess = true
                                    )
                                    
                                    // Auto-dismiss success message after a delay
                                    viewModelScope.launch {
                                        delay(3000)
                                        _uiState.value = _uiState.value.copy(isSuccess = false)
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Timber.e(e, "Failed to change password")
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = "Failed to change password: ${e.message}"
                                    )
                                }
                        }
                        .addOnFailureListener { e ->
                            Timber.e(e, "Failed to re-authenticate")
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                currentPasswordError = "Incorrect current password"
                            )
                        }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "No user is signed in"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error changing password")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An error occurred: ${e.message}"
                )
            }
        }
    }
} 