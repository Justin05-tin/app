package com.example.nammoadidaphat.presentation.ui.profile

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

data class NotificationUiState(
    val generalNotification: Boolean = true,
    val sound: Boolean = false,
    val vibrate: Boolean = false,
    val appUpdates: Boolean = true,
    val newServiceAvailable: Boolean = false,
    val newTipsAvailable: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()
    
    init {
        loadNotificationSettings()
    }
    
    private fun loadNotificationSettings() {
        viewModelScope.launch {
            try {
                val preferences = userPreferencesRepository.getPreferences()
                
                _uiState.value = NotificationUiState(
                    generalNotification = preferences.generalNotificationsEnabled,
                    sound = preferences.notificationSoundEnabled,
                    vibrate = preferences.notificationVibrateEnabled,
                    appUpdates = preferences.appUpdatesNotificationsEnabled,
                    newServiceAvailable = preferences.newServiceNotificationsEnabled,
                    newTipsAvailable = preferences.newTipsNotificationsEnabled
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading notification settings")
            }
        }
    }
    
    fun toggleGeneralNotification() {
        _uiState.value = _uiState.value.copy(
            generalNotification = !_uiState.value.generalNotification
        )
        saveNotificationSettings()
    }
    
    fun toggleSound() {
        _uiState.value = _uiState.value.copy(
            sound = !_uiState.value.sound
        )
        saveNotificationSettings()
    }
    
    fun toggleVibrate() {
        _uiState.value = _uiState.value.copy(
            vibrate = !_uiState.value.vibrate
        )
        saveNotificationSettings()
    }
    
    fun toggleAppUpdates() {
        _uiState.value = _uiState.value.copy(
            appUpdates = !_uiState.value.appUpdates
        )
        saveNotificationSettings()
    }
    
    fun toggleNewServiceAvailable() {
        _uiState.value = _uiState.value.copy(
            newServiceAvailable = !_uiState.value.newServiceAvailable
        )
        saveNotificationSettings()
    }
    
    fun toggleNewTipsAvailable() {
        _uiState.value = _uiState.value.copy(
            newTipsAvailable = !_uiState.value.newTipsAvailable
        )
        saveNotificationSettings()
    }
    
    private fun saveNotificationSettings() {
        viewModelScope.launch {
            try {
                userPreferencesRepository.updatePreferences { preferences ->
                    preferences.copy(
                        generalNotificationsEnabled = _uiState.value.generalNotification,
                        notificationSoundEnabled = _uiState.value.sound,
                        notificationVibrateEnabled = _uiState.value.vibrate,
                        appUpdatesNotificationsEnabled = _uiState.value.appUpdates,
                        newServiceNotificationsEnabled = _uiState.value.newServiceAvailable,
                        newTipsNotificationsEnabled = _uiState.value.newTipsAvailable
                    )
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving notification settings")
            }
        }
    }
} 