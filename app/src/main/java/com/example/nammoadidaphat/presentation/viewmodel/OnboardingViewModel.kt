package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Sử dụng MutableStateFlow để kiểm soát trạng thái của onboarding
    private val _hasSeenOnboarding = MutableStateFlow(false)
    val hasSeenOnboarding: StateFlow<Boolean> = _hasSeenOnboarding

    init {
        // Lấy trạng thái onboarding từ repository
        viewModelScope.launch {
            userPreferencesRepository.hasSeenOnboarding.collect { hasSeenOnboarding ->
                _hasSeenOnboarding.value = hasSeenOnboarding
            }
        }
    }

    suspend fun saveOnboardingCompleted() {
        userPreferencesRepository.saveOnboardingState(true)
        _hasSeenOnboarding.value = true
    }
} 