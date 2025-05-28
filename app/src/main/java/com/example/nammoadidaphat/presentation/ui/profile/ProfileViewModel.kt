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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.delay
import kotlinx.coroutines.CancellationException
import kotlin.time.Duration.Companion.milliseconds

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
                
                // Create a minimal placeholder user while loading
                val currentUser = authRepository.auth?.currentUser
                if (currentUser != null) {
                    val placeholderUser = User.createMinimalUser(
                        userId = currentUser.uid,
                        email = currentUser.email ?: "",
                        authProvider = currentUser.providerData.firstOrNull()?.providerId ?: "password"
                    )
                    
                    // Update state with placeholder but keep loading
                    _uiState.value = _uiState.value.copy(
                        user = placeholderUser,
                        isLoading = true
                    )
                }
                
                // Process user data on IO thread with timeout and retry
                withContext(Dispatchers.IO) {
                    authRepository.getCurrentUser()
                        .timeout(8000.milliseconds) // Increase timeout to 8 seconds
                        .retryWhen { cause, attempt ->
                            // Retry up to 2 times with delay between attempts
                            if (attempt < 2 && cause !is CancellationException) {
                                Timber.d("Retrying getCurrentUser attempt: $attempt")
                                delay(1000)
                                true
                            } else {
                                false
                            }
                        }
                        .catch { e ->
                            if (e is CancellationException) throw e
                            
                            Timber.e(e, "Error retrieving user data: ${e.message}")
                            
                            // Don't throw, instead emit the last placeholder user we have
                            val safeUser = _uiState.value.user ?: User.createMinimalUser(
                                userId = currentUser?.uid ?: "",
                                email = currentUser?.email ?: "",
                                authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                            )
                            
                            withContext(Dispatchers.Main) {
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    user = safeUser,
                                    error = "Could not load full profile: ${e.message}"
                                )
                            }
                        }
                        .collect { user ->
                            withContext(Dispatchers.Main) {
                                if (user != null) {
                                    // Fix: Always wrap the user in a safe wrapper to prevent crashes
                                    // on accessing fields that might not exist in the Firestore document
                                    val safeUser = try {
                                        // Đảm bảo rằng avatar luôn được khởi tạo với chuỗi rỗng nếu nó là null
                                        // và vẫn đảm bảo các trường khác có giá trị mặc định hợp lý
                                        User(
                                            id = user.id,
                                            email = user.email,
                                            displayName = user.displayName.ifEmpty { user.email.substringBefore('@') },
                                            avatar = user.avatar.let { if (it.isNullOrBlank()) "" else it },
                                            age = user.age ?: 25,
                                            gender = user.gender.ifEmpty { "Not specified" },
                                            height = user.height ?: 170,
                                            weight = user.weight ?: 70f,
                                            fitnessLevel = user.fitnessLevel.ifEmpty { "beginner" },
                                            goals = user.goals.ifEmpty { listOf("Get fit") },
                                            authProvider = user.authProvider.ifEmpty { "password" }
                                        )
                                    } catch (e: Exception) {
                                        Timber.e(e, "Error creating safe user: ${e.message}")
                                        
                                        // Fallback user with safe defaults và đảm bảo avatar không bao giờ null
                                        user.copy(
                                            avatar = "",
                                            displayName = user.displayName.ifEmpty { user.email.substringBefore('@') },
                                            gender = user.gender.ifEmpty { "Not specified" },
                                            fitnessLevel = user.fitnessLevel.ifEmpty { "beginner" },
                                            goals = user.goals.ifEmpty { listOf("Get fit") }
                                        )
                                    }
                                    
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        user = safeUser,
                                        error = null
                                    )
                                } else {
                                    _uiState.value = _uiState.value.copy(
                                        isLoading = false,
                                        error = "User not authenticated"
                                    )
                                }
                            }
                        }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                
                Timber.e(e, "Error loading user profile: ${e.message}")
                
                // Even if we have an exception, try to keep the latest user data we have
                val safeUser = _uiState.value.user
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    user = safeUser,  // Keep existing user data if available
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
        safeNavigate(navController, "edit_profile")
    }
    
    fun navigateToNotifications(navController: NavController) {
        safeNavigate(navController, "notifications")
    }
    
    fun navigateToSecurity(navController: NavController) {
        safeNavigate(navController, "security")
    }
    
    fun navigateToHelp(navController: NavController) {
        safeNavigate(navController, "help")
    }
    
    // Safely navigate without crashing if navigation fails
    private fun safeNavigate(navController: NavController, route: String) {
        try {
            navController.navigate(route)
        } catch (e: Exception) {
            Timber.e(e, "Navigation error to $route: ${e.message}")
            // Don't propagate exceptions from navigation
        }
    }
    
    fun upgradeToProVersion() {
        // Implement subscription or purchase flow
        Timber.d("Upgrade to Pro clicked")
    }
    
    fun signOut(navController: NavController) {
        viewModelScope.launch {
            try {
                // First clear local state
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    user = null
                )
                
                // Then sign out on IO thread with timeout
                withContext(Dispatchers.IO) {
                    withTimeout(5000.milliseconds) { // 5 second timeout with proper Duration
                        authRepository.signOut()
                    }
                }
                
                // Navigate even if sign out fails
                try {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Navigation error after sign out: ${e.message}")
                    // Try again with a simpler approach
                    try {
                        navController.navigate("login")
                    } catch (e2: Exception) {
                        Timber.e(e2, "Second navigation error: ${e2.message}")
                        // Last resort - try to navigate without options
                        try {
                            navController.popBackStack(navController.graph.startDestinationId, true)
                        } catch (e3: Exception) {
                            Timber.e(e3, "Final navigation attempt failed: ${e3.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                
                Timber.e(e, "Error signing out: ${e.message}")
                
                // Still try to navigate to login even if signOut fails
                try {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                    }
                } catch (e2: Exception) {
                    Timber.e(e2, "Navigation error after sign out failure: ${e2.message}")
                }
            }
        }
    }
} 