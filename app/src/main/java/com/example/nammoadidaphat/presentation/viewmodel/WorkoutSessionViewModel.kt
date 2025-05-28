package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.Exercise
import com.example.nammoadidaphat.domain.model.UserProgress
import com.example.nammoadidaphat.domain.model.WorkoutSession
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.example.nammoadidaphat.domain.repository.ExerciseRepository
import com.example.nammoadidaphat.domain.repository.LevelRepository
import com.example.nammoadidaphat.domain.repository.UserProgressRepository
import com.example.nammoadidaphat.domain.repository.WorkoutSessionRepository
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutState
import com.google.firebase.Timestamp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class WorkoutSessionViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val levelRepository: LevelRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val userProgressRepository: UserProgressRepository,
    private val authRepository: AuthRepository
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
    
    private val _navigateBack = MutableSharedFlow<Boolean>()
    val navigateBack: SharedFlow<Boolean> = _navigateBack.asSharedFlow()
    
    // Session tracking
    private var sessionStartTime: Timestamp? = null
    private var currentLevelId: String = ""
    private var totalDuration: Int = 0
    private var totalCaloriesBurned: Int = 0
    private val completedExercises = mutableListOf<String>()
    
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
                    
                    // Save level ID
                    currentLevelId = levelId
                    
                    // Record session start time
                    sessionStartTime = Timestamp.now()
                    
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
            // When exercise finishes, mark it as completed and start rest period
            completeCurrentExercise()
            startRest(index)
        }
    }
    
    private fun completeCurrentExercise() {
        val currentExercise = _exercises.value[_currentExerciseIndex.value]
        
        // Add to completed exercises
        completedExercises.add(currentExercise.id)
        
        // Add exercise calories to total
        totalCaloriesBurned += currentExercise.caloriesBurn
        
        // Add exercise duration to total
        totalDuration += currentExercise.duration
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
    
    fun completeWorkout() {
        // Complete current exercise
        completeCurrentExercise()
        
        // Finish the workout
        finishWorkout()
    }
    
    private fun finishWorkout() {
        viewModelScope.launch {
            try {
                // Get current user ID
                val currentUser = authRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val userId = currentUser.id
                    
                    // Get detailed user data to access metrics like height, weight, BMI
                    val userResult = authRepository.getUserById(userId)
                    val userData = userResult.getOrNull()
                    
                    // Create a workout session record
                    val workoutSession = WorkoutSession(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        levelId = currentLevelId,
                        startTime = sessionStartTime,
                        endTime = Timestamp.now(),
                        duration = totalDuration,
                        caloriesBurned = totalCaloriesBurned,
                        exercises = completedExercises,
                        completed = true,
                        notes = "",
                        rating = 0
                    )
                    
                    // Save workout session to Firestore
                    val sessionResult = workoutSessionRepository.addWorkoutSession(workoutSession)
                    
                    if (sessionResult.isSuccess) {
                        Timber.d("Workout session saved successfully")
                        
                        // Create a single user progress record for the entire workout
                        val userProgress = UserProgress(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            levelId = currentLevelId,
                            workoutSessionId = workoutSession.id,
                            date = Timestamp.now(),
                            // Include user metrics from profile
                            height = userData?.height,
                            weight = userData?.weight,
                            bmi = calculateBMI(userData?.height, userData?.weight),
                            workoutDuration = totalDuration,
                            caloriesBurned = totalCaloriesBurned,
                            notes = ""
                        )
                        
                        userProgressRepository.addUserProgress(userProgress)
                        
                        // Navigate back to the exercise screen
                        _navigateBack.emit(true)
                    } else {
                        Timber.e("Failed to save workout session: ${sessionResult.exceptionOrNull()}")
                    }
                } else {
                    Timber.e("Failed to get current user")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error finishing workout")
            }
        }
    }
    
    private fun calculateBMI(height: Int?, weight: Float?): Float? {
        if (height == null || weight == null || height <= 0) return null
        
        // BMI formula: weight(kg) / (height(m) * height(m))
        // Height is in cm, so convert to meters
        val heightInMeters = height / 100f
        return weight / (heightInMeters * heightInMeters)
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
                completeCurrentExercise()
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