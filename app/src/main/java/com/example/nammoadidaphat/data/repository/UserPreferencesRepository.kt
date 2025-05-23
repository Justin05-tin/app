package com.example.nammoadidaphat.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

// Create a single DataStore instance for the entire application
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    companion object {
        private val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
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

    suspend fun resetOnboardingState() {
        Timber.d("Resetting onboarding state to false")
        saveOnboardingState(false)
    }
} 