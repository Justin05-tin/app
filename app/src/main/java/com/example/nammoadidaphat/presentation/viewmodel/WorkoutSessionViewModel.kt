package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.Exercise
import com.example.nammoadidaphat.domain.repository.ExerciseRepository
import com.example.nammoadidaphat.domain.repository.LevelRepository
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WorkoutSessionViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val levelRepository: LevelRepository
) : ViewModel() {
    
    // Public state
    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()
    
    private val _currentExerciseIndex = MutableStateFlow(0)
    val currentExerciseIndex: StateFlow<Int> = _currentExerciseIndex.asStateFlow()
    
    private val _workoutState = MutableStateFlow(WorkoutState.READY)
    val workoutState: StateFlow<WorkoutState> = _workoutState.asStateFlow()
    
    private val _timeRemaining = MutableStateFlow(12) // 12 seconds for ready state
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()
    
    private val _totalExercises = MutableStateFlow(0)
    val totalExercises: StateFlow<Int> = _totalExercises.asStateFlow()
    
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Private state
    private var timerJob: Job? = null
    
    fun loadWorkoutSession(levelId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Load exercises for this level
                val result = exerciseRepository.getExercisesForLevel(levelId)
                
                if (result.isSuccess) {
                    val loadedExercises = result.getOrNull() ?: emptyList()
                    _exercises.value = loadedExercises
                    _totalExercises.value = loadedExercises.size
                    
                    // Start the session with ready countdown
                    startReadyCountdown()
                } else {
                    Timber.e("Error loading exercises: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error starting workout session")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun startReadyCountdown() {
        _workoutState.value = WorkoutState.READY
        _timeRemaining.value = 12 // 12 seconds countdown
        
        startTimer {
            // When ready countdown finishes, start the first exercise
            startExercise(0)
        }
    }
    
    private fun startExercise(index: Int) {
        if (index >= _exercises.value.size) {
            // All exercises completed
            finishWorkout()
            return
        }
        
        _currentExerciseIndex.value = index
        _workoutState.value = WorkoutState.EXERCISE
        
        // Get current exercise
        val exercise = _exercises.value[index]
        
        // Set time based on exercise duration, or default to 30 seconds if not specified
        val duration = if (exercise.duration > 0) exercise.duration else 30
        _timeRemaining.value = duration
        
        startTimer {
            // When exercise finishes, start rest period
            startRest(index)
        }
    }
    
    private fun startRest(completedExerciseIndex: Int) {
        _workoutState.value = WorkoutState.REST
        
        // Get completed exercise
        val exercise = _exercises.value[completedExerciseIndex]
        
        // Set rest time from exercise, or default to 10 seconds
        val restTime = if (exercise.restTime > 0) exercise.restTime else 10
        _timeRemaining.value = restTime
        
        startTimer {
            // When rest finishes, start next exercise
            startExercise(completedExerciseIndex + 1)
        }
    }
    
    private fun finishWorkout() {
        // Logic for finishing the workout
        // This could include saving the workout history, showing a completion screen, etc.
        Timber.d("Workout completed!")
    }
    
    private fun startTimer(onComplete: () -> Unit) {
        // Cancel any existing timer
        timerJob?.cancel()
        
        // Start a new timer
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                if (!_isPaused.value) {
                    delay(1000) // 1 second
                    _timeRemaining.value = _timeRemaining.value - 1
                } else {
                    delay(100) // Check pause state more frequently
                }
            }
            onComplete()
        }
    }
    
    fun togglePause() {
        _isPaused.value = !_isPaused.value
    }
    
    fun skipToNext() {
        when (_workoutState.value) {
            WorkoutState.READY -> {
                // Skip ready countdown and start first exercise
                timerJob?.cancel()
                startExercise(0)
            }
            WorkoutState.EXERCISE -> {
                // Skip to rest
                timerJob?.cancel()
                startRest(_currentExerciseIndex.value)
            }
            WorkoutState.REST -> {
                // Skip rest and go to next exercise
                timerJob?.cancel()
                startExercise(_currentExerciseIndex.value + 1)
            }
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
} 