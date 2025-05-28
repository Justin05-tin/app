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
import kotlinx.coroutines.flow.asStateFlow
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
        getCurrentUser()
    }
    
    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                
                authRepository.getCurrentUser().collect { user ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = user,
                        error = null
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
    
    fun editProfilePicture() {
        // This will be implemented in the EditProfileScreen
        // Here we just log the action
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
    
    fun signOut(navController: NavController) {
        viewModelScope.launch {
            try {
                authRepository.signOut()
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error signing out")
            }
        }
    }
} 