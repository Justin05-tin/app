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

    // Sử dụng StateFlow từ repository trực tiếp để luôn có trạng thái mới nhất
    val hasSeenOnboarding: StateFlow<Boolean> = userPreferencesRepository.hasSeenOnboarding
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

    /**
     * Lưu trạng thái hoàn thành onboarding.
     * Trạng thái này được lưu vào DataStore và sẽ tồn tại ngay cả khi ứng dụng đóng.
     */
    suspend fun saveOnboardingCompleted() {
        userPreferencesRepository.saveOnboardingState(true)
    }

    /**
     * Reset trạng thái onboarding (chỉ dùng cho mục đích debugging)
     */
    suspend fun resetOnboarding() {
        userPreferencesRepository.resetOnboardingState()
    }
} 