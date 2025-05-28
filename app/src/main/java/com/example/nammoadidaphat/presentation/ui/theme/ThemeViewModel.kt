package com.example.nammoadidaphat.presentation.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    init {
        loadThemePreference()
    }
    
    private fun loadThemePreference() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.getUserPreferences().collect { preferences ->
                    _isDarkTheme.value = preferences.isDarkTheme
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to load theme preference")
            }
        }
    }
    
    fun toggleDarkTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
        
        viewModelScope.launch {
            try {
                userPreferencesRepository.setDarkTheme(_isDarkTheme.value)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save dark theme preference")
            }
        }
    }
} 