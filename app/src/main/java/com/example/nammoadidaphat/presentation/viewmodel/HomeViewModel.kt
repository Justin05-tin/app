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
class HomeViewModel @Inject constructor(
    private val workoutTypeRepository: WorkoutTypeRepository,
    private val levelRepository: LevelRepository
) : ViewModel() {
    
    // State for workouts grouped by difficulty
    private val _beginnerWorkouts = MutableStateFlow<List<WorkoutType>>(emptyList())
    val beginnerWorkouts: StateFlow<List<WorkoutType>> = _beginnerWorkouts.asStateFlow()
    
    private val _intermediateWorkouts = MutableStateFlow<List<WorkoutType>>(emptyList())
    val intermediateWorkouts: StateFlow<List<WorkoutType>> = _intermediateWorkouts.asStateFlow()
    
    private val _advancedWorkouts = MutableStateFlow<List<WorkoutType>>(emptyList())
    val advancedWorkouts: StateFlow<List<WorkoutType>> = _advancedWorkouts.asStateFlow()
    
    // Featured workouts by difficulty
    private val _featuredBeginnerWorkouts = MutableStateFlow<List<WorkoutType>>(emptyList())
    val featuredBeginnerWorkouts: StateFlow<List<WorkoutType>> = _featuredBeginnerWorkouts.asStateFlow()
    
    private val _featuredIntermediateWorkouts = MutableStateFlow<List<WorkoutType>>(emptyList())
    val featuredIntermediateWorkouts: StateFlow<List<WorkoutType>> = _featuredIntermediateWorkouts.asStateFlow()
    
    private val _featuredAdvancedWorkouts = MutableStateFlow<List<WorkoutType>>(emptyList())
    val featuredAdvancedWorkouts: StateFlow<List<WorkoutType>> = _featuredAdvancedWorkouts.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadWorkouts()
    }
    
    fun loadWorkouts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                workoutTypeRepository.getAllWorkoutTypes().onSuccess { allWorkouts ->
                    Timber.d("Successfully loaded ${allWorkouts.size} workout types")
                    
                    // Group workouts by difficulty level
                    val beginnerWorkouts = allWorkouts.filter { it.difficulty?.lowercase() == "beginner" }
                    val intermediateWorkouts = allWorkouts.filter { it.difficulty?.lowercase() == "intermediate" }
                    val advancedWorkouts = allWorkouts.filter { it.difficulty?.lowercase() == "advanced" }
                    
                    // Update state flows
                    _beginnerWorkouts.value = beginnerWorkouts
                    _intermediateWorkouts.value = intermediateWorkouts
                    _advancedWorkouts.value = advancedWorkouts
                    
                    // Select a subset for featured workouts (up to 5 from each category)
                    _featuredBeginnerWorkouts.value = beginnerWorkouts.take(5)
                    _featuredIntermediateWorkouts.value = intermediateWorkouts.take(5)
                    _featuredAdvancedWorkouts.value = advancedWorkouts.take(5)
                    
                    Timber.d("Categorized workouts - Beginner: ${beginnerWorkouts.size}, Intermediate: ${intermediateWorkouts.size}, Advanced: ${advancedWorkouts.size}")
                    
                }.onFailure { error ->
                    Timber.e(error, "Error loading workout types")
                    _error.value = "Failed to load workout types: ${error.message}"
                }
            } catch (e: Exception) {
                Timber.e(e, "Exception when loading workout types")
                _error.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Helper function to get featured workouts by difficulty level
    fun getFeaturedWorkoutsByLevel(level: String): List<WorkoutType> {
        return when (level.lowercase()) {
            "beginner" -> featuredBeginnerWorkouts.value
            "intermediate" -> featuredIntermediateWorkouts.value
            "advanced" -> featuredAdvancedWorkouts.value
            else -> featuredBeginnerWorkouts.value
        }
    }
    
    // Helper function to get all workouts by difficulty level
    fun getWorkoutsByLevel(level: String): List<WorkoutType> {
        return when (level.lowercase()) {
            "beginner" -> beginnerWorkouts.value
            "intermediate" -> intermediateWorkouts.value
            "advanced" -> advancedWorkouts.value
            else -> beginnerWorkouts.value
        }
    }
} 