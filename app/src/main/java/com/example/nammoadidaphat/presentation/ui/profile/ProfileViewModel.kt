package com.example.nammoadidaphat.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import com.example.nammoadidaphat.data.repository.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val isDarkTheme: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState
    
    init {
        loadUserProfile()
        loadUserPreferences()
    }
    
    fun getCurrentUser() {
        loadUserProfile()
    }
    
    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                authRepository.getCurrentUser().collect { user ->
                    if (user != null) {
                        _uiState.value = _uiState.value.copy(isLoading = false, user = user, error = null)
                    } else {
                        if (_uiState.value.user != null) {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = null
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "User not found",
                                user = User()
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load user profile")
                
                val currentUser = _uiState.value.user ?: User()
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load profile: ${e.message}",
                    user = currentUser
                )
            }
        }
    }
    
    private fun loadUserPreferences() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.getUserPreferences().collect { preferences ->
                    _uiState.value = _uiState.value.copy(isDarkTheme = preferences.isDarkTheme)
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load user preferences")
            }
        }
    }
    
    fun toggleDarkTheme() {
        val newThemeValue = !_uiState.value.isDarkTheme
        _uiState.value = _uiState.value.copy(isDarkTheme = newThemeValue)
        
        viewModelScope.launch {
            try {
                userPreferencesRepository.setDarkTheme(newThemeValue)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save dark theme preference")
            }
        }
    }
    
    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                authRepository.signOut()
                // Result will be handled by the auth state listener
            } catch (e: Exception) {
                Timber.e(e, "Error during logout")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to logout: ${e.message}"
                )
            }
        }
    }
    
    fun editProfilePicture() {
        // Implement image picker and upload functionality
        Timber.d("Edit profile picture clicked")
    }
    
    fun navigateToEditProfile(navController: NavController) {
        navController.navigate("edit_profile")
    }
    
    fun navigateToNotifications(navController: NavController) {
        navController.navigate("notifications")
    }
    
    fun navigateToSecurity(navController: NavController) {
        navController.navigate("security")
    }
    
    fun navigateToHelp(navController: NavController) {
        navController.navigate("help")
    }
    
    fun upgradeToProVersion() {
        // Implement subscription or purchase flow
        Timber.d("Upgrade to Pro clicked")
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
} 