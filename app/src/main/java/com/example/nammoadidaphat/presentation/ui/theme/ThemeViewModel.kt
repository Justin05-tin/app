package com.example.nammoadidaphat.presentation.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Stream of dark theme state from UserPreferencesRepository
    val isDarkTheme: Flow<Boolean> = userPreferencesRepository.getUserPreferences()
        .map { preferences -> preferences.isDarkTheme }

    // Toggle dark theme
    fun toggleDarkTheme() {
        viewModelScope.launch {
            try {
                val currentPreferences = userPreferencesRepository.getUserPreferences().first()
                userPreferencesRepository.setDarkTheme(!currentPreferences.isDarkTheme)
            } catch (e: Exception) {
                Timber.e(e, "Failed to toggle dark theme")
            }
        }
    }

    // Set dark theme
    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            try {
                userPreferencesRepository.setDarkTheme(enabled)
            } catch (e: Exception) {
                Timber.e(e, "Failed to set dark theme to $enabled")
            }
        }
    }
} 