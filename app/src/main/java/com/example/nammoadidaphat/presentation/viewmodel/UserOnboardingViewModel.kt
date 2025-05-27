package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserOnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    // User information to be collected during onboarding
    private val _gender = MutableStateFlow<String>("")
    val gender: StateFlow<String> = _gender.asStateFlow()
    
    private val _displayName = MutableStateFlow<String>("")
    val displayName: StateFlow<String> = _displayName.asStateFlow()
    
    private val _age = MutableStateFlow<Int?>(null)
    val age: StateFlow<Int?> = _age.asStateFlow()
    
    private val _weight = MutableStateFlow<Float?>(null)
    val weight: StateFlow<Float?> = _weight.asStateFlow()
    
    private val _height = MutableStateFlow<Int?>(null)
    val height: StateFlow<Int?> = _height.asStateFlow()
    
    private val _goal = MutableStateFlow<String>("")
    val goal: StateFlow<String> = _goal.asStateFlow()
    
    private val _fitnessLevel = MutableStateFlow<String>("")
    val fitnessLevel: StateFlow<String> = _fitnessLevel.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // User data
    private val _userData = MutableStateFlow<User?>(null)
    val userData: StateFlow<User?> = _userData.asStateFlow()
    
    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _userData.value = user
            }
        }
    }
    
    fun updateGender(gender: String) {
        _gender.value = gender
    }
    
    fun updateDisplayName(name: String) {
        _displayName.value = name
    }
    
    fun updateAge(age: Int) {
        _age.value = age
    }
    
    fun updateWeight(weight: Float) {
        _weight.value = weight
    }
    
    fun updateHeight(height: Int) {
        _height.value = height
    }
    
    fun updateGoal(goal: String) {
        _goal.value = goal
    }
    
    fun updateFitnessLevel(level: String) {
        _fitnessLevel.value = level
    }
    
    suspend fun saveUserOnboardingData(): Result<Unit> {
        _isLoading.value = true
        _error.value = null
        
        val currentUser = _userData.value
        if (currentUser == null) {
            _error.value = "User not found"
            _isLoading.value = false
            return Result.failure(Exception("User not found"))
        }
        
        if (currentUser.id.isBlank()) {
            _error.value = "User ID is blank"
            _isLoading.value = false
            return Result.failure(Exception("User ID is blank"))
        }
        
        return try {
            // Update the user object with the collected data
            val updatedUser = currentUser.copy(
                gender = _gender.value,
                displayName = _displayName.value,
                age = _age.value,
                weight = _weight.value,
                height = _height.value,
                goals = listOf(_goal.value),
                fitnessLevel = _fitnessLevel.value
            )
            
            // Log the data before saving - for debugging
            android.util.Log.d("UserOnboardingViewModel", "Saving user data: ${updatedUser.id}, name: ${updatedUser.displayName}")
            
            val result = authRepository.updateUserProfile(updatedUser)
            
            result.onSuccess {
                _userData.value = updatedUser
                android.util.Log.d("UserOnboardingViewModel", "Successfully saved user data")
            }.onFailure { error ->
                _error.value = error.message
                android.util.Log.e("UserOnboardingViewModel", "Failed to save user data: ${error.message}")
            }
            
            _isLoading.value = false
            result
        } catch (e: Exception) {
            android.util.Log.e("UserOnboardingViewModel", "Exception when saving user data: ${e.message}")
            _error.value = e.message ?: "Error updating user profile"
            _isLoading.value = false
            Result.failure(e)
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 