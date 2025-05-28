package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.UserProgress
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.example.nammoadidaphat.domain.repository.UserProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val userProgressRepository: UserProgressRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProgress = MutableStateFlow<List<UserProgress>>(emptyList())
    val userProgress: StateFlow<List<UserProgress>> = _userProgress.asStateFlow()
    
    private val _todayStats = MutableStateFlow(DailyStats())
    val todayStats: StateFlow<DailyStats> = _todayStats.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadUserProgress()
    }
    
    fun loadUserProgress() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentUser = authRepository.getCurrentUser().first()
                if (currentUser != null) {
                    val progressFlow = userProgressRepository.getUserProgress(currentUser.id)
                    val progressList = progressFlow.firstOrNull() ?: emptyList()
                    
                    _userProgress.value = progressList
                    
                    // Calculate today's stats
                    calculateTodayStats(progressList)
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading user progress")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun calculateTodayStats(progressList: List<UserProgress>) {
        val calendar = Calendar.getInstance()
        
        // Start of today
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        
        // End of today
        val endOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
        
        // Filter progress records for today
        val todayProgress = progressList.filter { progress ->
            val timestamp = progress.date?.toDate()?.time ?: 0
            timestamp in startOfDay..endOfDay
        }
        
        // Calculate stats
        val exerciseCount = todayProgress.size
        val totalCalories = todayProgress.sumOf { it.caloriesBurned }
        val totalDuration = todayProgress.sumOf { it.workoutDuration }
        
        _todayStats.value = DailyStats(
            exerciseCount = exerciseCount,
            totalCalories = totalCalories,
            totalDuration = totalDuration
        )
    }
    
    data class DailyStats(
        val exerciseCount: Int = 0,
        val totalCalories: Int = 0,
        val totalDuration: Int = 0
    )
} 