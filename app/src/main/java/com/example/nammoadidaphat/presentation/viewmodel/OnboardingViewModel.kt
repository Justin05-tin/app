package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Sử dụng StateFlow từ repository với caching policy tốt hơn
    val hasSeenOnboarding: StateFlow<Boolean> = userPreferencesRepository.hasSeenOnboarding
        .stateIn(
            viewModelScope,
            // Use WhileSubscribed with timeout to keep the value cached briefly
            SharingStarted.WhileSubscribed(5000),
            // Default value is explicitly false
            false
        )

    init {
        // Pre-load the preference value when ViewModel is created
        viewModelScope.launch {
            // Access the flow's latest value to trigger collection
            Timber.d("OnboardingViewModel initialized, hasSeenOnboarding: ${hasSeenOnboarding.value}")
        }
    }

    /**
     * Lưu trạng thái hoàn thành onboarding.
     * Trạng thái này được lưu vào DataStore và sẽ tồn tại ngay cả khi ứng dụng đóng.
     * No longer suspend - launches in viewModelScope for easier calling from Composables
     */
    fun saveOnboardingCompleted() {
        viewModelScope.launch {
            userPreferencesRepository.saveOnboardingState(true)
            Timber.d("Onboarding state saved as completed")
        }
    }

    /**
     * Reset trạng thái onboarding (chỉ dùng cho mục đích debugging)
     */
    fun resetOnboarding() {
        viewModelScope.launch {
            userPreferencesRepository.resetOnboardingState()
            Timber.d("Onboarding state reset")
        }
    }
} 