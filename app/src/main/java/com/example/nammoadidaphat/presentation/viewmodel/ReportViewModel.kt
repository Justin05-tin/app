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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
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
    
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    
    private val _lastRefreshTime = MutableStateFlow(0L)
    val lastRefreshTime: StateFlow<Long> = _lastRefreshTime.asStateFlow()

    init {
        Timber.d("ReportViewModel: Initializing...")
        loadUserProgress()
    }
    
    // Call this when the screen becomes visible or when you need fresh data
    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            Timber.d("ReportViewModel: Refresh data called, triggering loadUserProgress")
            loadUserProgress(isRefresh = true)
            _isRefreshing.value = false
            _lastRefreshTime.value = System.currentTimeMillis()
        }
    }
    
    fun loadUserProgress(isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                _isLoading.value = true
                Timber.d("ReportViewModel: Loading started, isLoading set to true")
            } else {
                Timber.d("ReportViewModel: Refreshing data")
            }
            
            try {
                Timber.d("ReportViewModel: Attempting to get current user")
                authRepository.getCurrentUser().collectLatest { currentUser ->
                    Timber.d("ReportViewModel: Current user: ${currentUser?.email}, ID: ${currentUser?.id}")
                    
                    if (currentUser != null) {
                        Timber.d("ReportViewModel: Getting progress for user ${currentUser.id}")
                        userProgressRepository.getUserProgress(currentUser.id)
                            .catch { e -> 
                                Timber.e(e, "ReportViewModel: Error collecting user progress") 
                            }
                            .collectLatest { progressList ->
                                Timber.d("ReportViewModel: Received ${progressList.size} progress records")
                                _userProgress.value = progressList
                                
                                // Calculate today's stats
                                calculateTodayStats(progressList)
                                
                                // Update last refresh time
                                _lastRefreshTime.value = System.currentTimeMillis()
                                
                                // Set loading to false if we received data
                                if (!isRefresh) {
                                    _isLoading.value = false
                                    Timber.d("ReportViewModel: Loading finished, isLoading set to false")
                                }
                            }
                    } else {
                        Timber.e("ReportViewModel: Current user is null")
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "ReportViewModel: Error loading user progress")
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
        
        Timber.d("ReportViewModel: Calculated today's stats - exercises: $exerciseCount, calories: $totalCalories, duration: $totalDuration")
        
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