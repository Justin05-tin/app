package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    init {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { user ->
                _currentUser.value = user
                if (user != null) {
                    _authState.value = AuthState.Authenticated(user)
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            }
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        _authState.value = AuthState.Loading
        
        val result = authRepository.signIn(email, password)
        result.onSuccess { user ->
            _authState.value = AuthState.Authenticated(user)
        }.onFailure { error ->
            _authState.value = AuthState.Error(error.message ?: "Authentication failed")
        }
        return result
    }

    suspend fun signUp(
        email: String, 
        password: String, 
        fullName: String,
        age: Int? = null,
        gender: String = "",
        height: Int? = null,
        weight: Float? = null,
        fitnessLevel: String = "",
        goals: String = ""
    ): Result<User> {
        _authState.value = AuthState.Loading
        
        val result = authRepository.signUp(
            email, 
            password, 
            fullName,
            age,
            gender,
            height,
            weight,
            fitnessLevel,
            goals
        )
        result.onSuccess { user ->
            _authState.value = AuthState.Authenticated(user)
        }.onFailure { error ->
            _authState.value = AuthState.Error(error.message ?: "Registration failed")
        }
        return result
    }

    suspend fun resetPassword(email: String): Result<Unit> {
        _authState.value = AuthState.Loading
        
        val result = authRepository.resetPassword(email)
        result.onSuccess {
            _authState.value = AuthState.PasswordResetSent
        }.onFailure { error ->
            _authState.value = AuthState.Error(error.message ?: "Password reset failed")
        }
        return result
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> {
        val result = authRepository.updateUserProfile(user)
        result.onSuccess {
            _currentUser.value = user
        }
        return result
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _authState.value = AuthState.Unauthenticated
            _currentUser.value = null
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: User) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
    object PasswordResetSent : AuthState()
} 