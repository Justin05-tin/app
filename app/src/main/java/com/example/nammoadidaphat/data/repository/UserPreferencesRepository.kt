package com.example.nammoadidaphat.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Create a single DataStore instance for the entire application
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val hasSeenOnboarding: Boolean = false,
    val isDarkTheme: Boolean = false,
    // Notification settings
    val generalNotificationsEnabled: Boolean = true,
    val notificationSoundEnabled: Boolean = false,
    val notificationVibrateEnabled: Boolean = false,
    val appUpdatesNotificationsEnabled: Boolean = true,
    val newServiceNotificationsEnabled: Boolean = false,
    val newTipsNotificationsEnabled: Boolean = false,
    // Generic preferences map for storing additional settings
    val preferences: Map<String, Any> = mapOf(
        "faceIdEnabled" to false,
        "rememberMeEnabled" to true,
        "touchIdEnabled" to true
    )
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore
    private val gson = Gson()

    companion object {
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        private val IS_DARK_THEME = booleanPreferencesKey("is_dark_theme")
        
        // Notification settings keys
        private val GENERAL_NOTIFICATIONS = booleanPreferencesKey("general_notifications")
        private val NOTIFICATION_SOUND = booleanPreferencesKey("notification_sound")
        private val NOTIFICATION_VIBRATE = booleanPreferencesKey("notification_vibrate")
        private val APP_UPDATES_NOTIFICATIONS = booleanPreferencesKey("app_updates_notifications")
        private val NEW_SERVICE_NOTIFICATIONS = booleanPreferencesKey("new_service_notifications")
        private val NEW_TIPS_NOTIFICATIONS = booleanPreferencesKey("new_tips_notifications")
        
        // Generic preferences map key
        private val PREFERENCES_MAP = stringPreferencesKey("preferences_map")
    }

    suspend fun saveOnboardingState(completed: Boolean) {
        try {
            Timber.d("Saving onboarding state: $completed")
            dataStore.edit { preferences ->
                preferences[HAS_SEEN_ONBOARDING] = completed
                Timber.d("Onboarding state saved successfully: $completed")
            }
        } catch (e: IOException) {
            Timber.e(e, "Error saving onboarding state")
        }
    }
    
    suspend fun setDarkTheme(enabled: Boolean) {
        try {
            Timber.d("Saving dark theme preference: $enabled")
            dataStore.edit { preferences ->
                preferences[IS_DARK_THEME] = enabled
            }
        } catch (e: IOException) {
            Timber.e(e, "Error saving dark theme preference")
        }
    }
    
    suspend fun getPreferences(): UserPreferences {
        return getUserPreferences().first()
    }
    
    suspend fun updatePreferences(update: (UserPreferences) -> UserPreferences) {
        try {
            val currentPreferences = getPreferences()
            val updatedPreferences = update(currentPreferences)
            
            dataStore.edit { preferences ->
                // Save all preferences
                preferences[HAS_SEEN_ONBOARDING] = updatedPreferences.hasSeenOnboarding
                preferences[IS_DARK_THEME] = updatedPreferences.isDarkTheme
                preferences[GENERAL_NOTIFICATIONS] = updatedPreferences.generalNotificationsEnabled
                preferences[NOTIFICATION_SOUND] = updatedPreferences.notificationSoundEnabled
                preferences[NOTIFICATION_VIBRATE] = updatedPreferences.notificationVibrateEnabled
                preferences[APP_UPDATES_NOTIFICATIONS] = updatedPreferences.appUpdatesNotificationsEnabled
                preferences[NEW_SERVICE_NOTIFICATIONS] = updatedPreferences.newServiceNotificationsEnabled
                preferences[NEW_TIPS_NOTIFICATIONS] = updatedPreferences.newTipsNotificationsEnabled
                
                // Save the preferences map as a JSON string
                preferences[PREFERENCES_MAP] = gson.toJson(updatedPreferences.preferences)
            }
        } catch (e: IOException) {
            Timber.e(e, "Error updating preferences")
        }
    }

    val hasSeenOnboarding: Flow<Boolean> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading onboarding preferences")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val hasSeenValue = preferences[HAS_SEEN_ONBOARDING] ?: false
            Timber.d("Retrieved onboarding state: $hasSeenValue")
            hasSeenValue
        }
        
    fun getUserPreferences(): Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Timber.e(exception, "Error reading user preferences")
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val preferencesMapJson = preferences[PREFERENCES_MAP]
            val prefMap = if (preferencesMapJson.isNullOrEmpty()) {
                mapOf(
                    "faceIdEnabled" to false,
                    "rememberMeEnabled" to true,
                    "touchIdEnabled" to true
                )
            } else {
                try {
                    val type = object : TypeToken<Map<String, Any>>() {}.type
                    gson.fromJson<Map<String, Any>>(preferencesMapJson, type)
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing preferences map")
                    mapOf(
                        "faceIdEnabled" to false,
                        "rememberMeEnabled" to true,
                        "touchIdEnabled" to true
                    )
                }
            }
            
            UserPreferences(
                hasSeenOnboarding = preferences[HAS_SEEN_ONBOARDING] ?: false,
                isDarkTheme = preferences[IS_DARK_THEME] ?: false,
                generalNotificationsEnabled = preferences[GENERAL_NOTIFICATIONS] ?: true,
                notificationSoundEnabled = preferences[NOTIFICATION_SOUND] ?: false,
                notificationVibrateEnabled = preferences[NOTIFICATION_VIBRATE] ?: false,
                appUpdatesNotificationsEnabled = preferences[APP_UPDATES_NOTIFICATIONS] ?: true,
                newServiceNotificationsEnabled = preferences[NEW_SERVICE_NOTIFICATIONS] ?: false,
                newTipsNotificationsEnabled = preferences[NEW_TIPS_NOTIFICATIONS] ?: false,
                preferences = prefMap
            )
        }

    suspend fun resetOnboardingState() {
        Timber.d("Resetting onboarding state to false")
        saveOnboardingState(false)
    }
} 