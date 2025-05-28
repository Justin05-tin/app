package com.example.nammoadidaphat.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class SecurityUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val faceIdEnabled: Boolean = false,
    val rememberMeEnabled: Boolean = true,
    val touchIdEnabled: Boolean = true,
    val isPasswordBasedUser: Boolean = true, // Default to showing change password option
    val user: User? = null
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()
    
    init {
        loadSecuritySettings()
        getCurrentUser()
    }
    
    private fun loadSecuritySettings() {
        viewModelScope.launch {
            try {
                val preferences = userPreferencesRepository.getPreferences()
                
                _uiState.value = _uiState.value.copy(
                    faceIdEnabled = preferences.preferences?.get("faceIdEnabled") as? Boolean ?: false,
                    rememberMeEnabled = preferences.preferences?.get("rememberMeEnabled") as? Boolean ?: true,
                    touchIdEnabled = preferences.preferences?.get("touchIdEnabled") as? Boolean ?: true
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading security settings")
            }
        }
    }
    
    private fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                val user = authRepository.getCurrentUser().first()
                
                if (user != null) {
                    val isPasswordUser = user.authProvider == "password"
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        isPasswordBasedUser = isPasswordUser,
                        error = null
                    )
                    
                    Timber.d("User auth provider: ${user.authProvider}, isPasswordUser: $isPasswordUser")
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "User not found"
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading user profile")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load user profile"
                )
            }
        }
    }
    
    fun toggleFaceId() {
        val newValue = !_uiState.value.faceIdEnabled
        _uiState.value = _uiState.value.copy(faceIdEnabled = newValue)
        saveSecuritySettings()
    }
    
    fun toggleRememberMe() {
        val newValue = !_uiState.value.rememberMeEnabled
        _uiState.value = _uiState.value.copy(rememberMeEnabled = newValue)
        saveSecuritySettings()
    }
    
    fun toggleTouchId() {
        val newValue = !_uiState.value.touchIdEnabled
        _uiState.value = _uiState.value.copy(touchIdEnabled = newValue)
        saveSecuritySettings()
    }
    
    private fun saveSecuritySettings() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.updatePreferences { preferences ->
                    // Create a new map with updated security settings
                    val updatedPreferences = preferences.preferences.toMutableMap().apply {
                        put("faceIdEnabled", _uiState.value.faceIdEnabled)
                        put("rememberMeEnabled", _uiState.value.rememberMeEnabled)
                        put("touchIdEnabled", _uiState.value.touchIdEnabled)
                    }
                    
                    // Return a copy of the preferences with the updated map
                    preferences.copy(
                        preferences = updatedPreferences
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving security settings")
            }
        }
    }
    
    fun navigateToGoogleAuthenticator(navController: NavController) {
        navController.navigate("google_authenticator")
    }
    
    fun navigateToChangePassword(navController: NavController) {
        navController.navigate("change_password")
    }
} 