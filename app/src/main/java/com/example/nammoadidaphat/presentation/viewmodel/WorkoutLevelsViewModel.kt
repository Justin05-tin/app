package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.Level
import com.example.nammoadidaphat.domain.model.WorkoutType
import com.example.nammoadidaphat.domain.repository.LevelRepository
import com.example.nammoadidaphat.domain.repository.WorkoutTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkoutLevelsViewModel @Inject constructor(
    private val levelRepository: LevelRepository,
    private val workoutTypeRepository: WorkoutTypeRepository
) : ViewModel() {
    
    // Current workout type
    private val _workoutType = MutableStateFlow<WorkoutType?>(null)
    val workoutType: StateFlow<WorkoutType?> = _workoutType.asStateFlow()
    
    // Levels for current workout type
    private val _levels = MutableStateFlow<List<Level>>(emptyList())
    val levels: StateFlow<List<Level>> = _levels.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Set workout type and load levels
    fun loadWorkoutType(workoutTypeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Load workout type details
                Timber.d("Loading workout type: $workoutTypeId")
                workoutTypeRepository.getWorkoutTypeById(workoutTypeId).onSuccess { type ->
                    _workoutType.value = type
                    Timber.d("Loaded workout type: ${type.name}")
                    
                    // Now load levels for this workout type
                    loadLevelsForWorkoutType(workoutTypeId)
                }.onFailure { error ->
                    Timber.e(error, "Error loading workout type: $workoutTypeId")
                    _error.value = "Failed to load workout type: ${error.message}"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception when loading workout type: $workoutTypeId")
                _error.value = "Error: ${e.message}"
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun loadLevelsForWorkoutType(workoutTypeId: String) {
        try {
            Timber.d("Loading levels for workout type: $workoutTypeId")
            levelRepository.getLevelsForWorkoutType(workoutTypeId).onSuccess { levelsList ->
                _levels.value = levelsList.sortedBy { it.order }
                Timber.d("Loaded ${levelsList.size} levels for workout type $workoutTypeId")
            }.onFailure { error ->
                Timber.e(error, "Error loading levels for workout type: $workoutTypeId")
                _error.value = "Failed to load levels: ${error.message}"
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception when loading levels: ${e.message}")
            _error.value = "Error loading levels: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    fun refreshData() {
        _workoutType.value?.let { workoutType ->
            loadWorkoutType(workoutType.id)
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 