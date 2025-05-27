package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.Exercise
import com.example.nammoadidaphat.domain.model.Level
import com.example.nammoadidaphat.domain.repository.ExerciseRepository
import com.example.nammoadidaphat.domain.repository.LevelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkoutLevelDetailViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val levelRepository: LevelRepository
) : ViewModel() {
    
    // Current level details
    private val _level = MutableStateFlow<Level?>(null)
    val level: StateFlow<Level?> = _level.asStateFlow()
    
    // Exercises for current level
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Load level details and exercises
    fun loadLevelWithExercises(levelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Load level details
                Timber.d("Loading level: $levelId")
                levelRepository.getLevelById(levelId).onSuccess { levelData ->
                    _level.value = levelData
                    Timber.d("Loaded level: ${levelData.name}")
                    
                    // Now load exercises for this level
                    loadExercisesForLevel(levelId)
                }.onFailure { error ->
                    Timber.e(error, "Error loading level: $levelId")
                    _error.value = "Failed to load level: ${error.message}"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception when loading level: $levelId")
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun loadExercisesForLevel(levelId: String) {
        try {
            Timber.d("Loading exercises for level: $levelId")
            exerciseRepository.getExercisesForLevel(levelId).onSuccess { exercisesList ->
                _exercises.value = exercisesList.sortedBy { it.order }
                Timber.d("Loaded ${exercisesList.size} exercises for level $levelId")
                _isLoading.value = false
            }.onFailure { error ->
                Timber.e(error, "Error loading exercises for level: $levelId")
                _error.value = "Failed to load exercises: ${error.message}"
                _isLoading.value = false
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception when loading exercises: ${e.message}")
            _error.value = "Error loading exercises: ${e.message}"
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 