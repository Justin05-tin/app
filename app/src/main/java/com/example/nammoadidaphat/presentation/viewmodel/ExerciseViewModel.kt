package com.example.nammoadidaphat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nammoadidaphat.domain.model.Category
import com.example.nammoadidaphat.domain.model.WorkoutType
import com.example.nammoadidaphat.domain.repository.CategoryRepository
import com.example.nammoadidaphat.domain.repository.WorkoutTypeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val workoutTypeRepository: WorkoutTypeRepository
) : ViewModel() {
    
    // State for categories
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    
    // State for workout types, grouped by category ID
    private val _workoutTypesByCategory = MutableStateFlow<Map<String, List<WorkoutType>>>(emptyMap())
    val workoutTypesByCategory: StateFlow<Map<String, List<WorkoutType>>> = _workoutTypesByCategory.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadData()
    }
    
    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Load categories first
                Timber.d("ExerciseViewModel: Starting to load categories")
                val categoriesResult = categoryRepository.getAllCategories()
                categoriesResult.onSuccess { loadedCategories ->
                    Timber.d("ExerciseViewModel: Successfully loaded ${loadedCategories.size} categories")
                    if (loadedCategories.isEmpty()) {
                        Timber.w("ExerciseViewModel: No categories found in Firestore")
                        _error.value = "No categories found"
                    } else {
                        loadedCategories.forEach { category ->
                            Timber.d("ExerciseViewModel: Category - ID: ${category.id}, Name: ${category.name}")
                        }
                        _categories.value = loadedCategories
                        
                        // Load all workout types
                        Timber.d("ExerciseViewModel: Starting to load workout types")
                        val allTypesResult = workoutTypeRepository.getAllWorkoutTypes()
                        allTypesResult.onSuccess { workoutTypes ->
                            Timber.d("ExerciseViewModel: Successfully loaded ${workoutTypes.size} workout types")
                            
                            if (workoutTypes.isEmpty()) {
                                Timber.w("ExerciseViewModel: No workout types found in Firestore")
                                _error.value = "No workout types found"
                            } else {
                                // Log all workout types for debugging
                                workoutTypes.forEachIndexed { index, type ->
                                    Timber.d("ExerciseViewModel: WorkoutType[$index] - ID: ${type.id}, Name: ${type.name}, CategoryID: ${type.categoryId}")
                                    if (type.categoryId.isBlank()) {
                                        Timber.w("ExerciseViewModel: WorkoutType ${type.id} has no categoryId!")
                                    }
                                }
                                
                                // Group workout types by category ID
                                val typesWithCategory = workoutTypes.filter { it.categoryId.isNotBlank() }
                                if (typesWithCategory.size < workoutTypes.size) {
                                    Timber.w("ExerciseViewModel: ${workoutTypes.size - typesWithCategory.size} workout types have no categoryId!")
                                }
                                
                                val typesByCategory = typesWithCategory.groupBy { it.categoryId }
                                Timber.d("ExerciseViewModel: Grouped workout types by category, total groups: ${typesByCategory.size}")
                                
                                // Log grouped data
                                typesByCategory.forEach { (categoryId, types) ->
                                    Timber.d("ExerciseViewModel: Category ID: $categoryId has ${types.size} workout types")
                                    val category = loadedCategories.find { it.id == categoryId }
                                    if (category != null) {
                                        Timber.d("ExerciseViewModel: This matches category: ${category.name}")
                                    } else {
                                        Timber.w("ExerciseViewModel: No matching category found for ID: $categoryId")
                                    }
                                }
                                
                                _workoutTypesByCategory.value = typesByCategory
                                
                                // Check if any categories have no workout types
                                val categoriesWithNoWorkouts = loadedCategories.filter { category -> 
                                    !typesByCategory.containsKey(category.id) 
                                }
                                if (categoriesWithNoWorkouts.isNotEmpty()) {
                                    Timber.w("ExerciseViewModel: ${categoriesWithNoWorkouts.size} categories have no workout types!")
                                    categoriesWithNoWorkouts.forEach { category ->
                                        Timber.w("ExerciseViewModel: Category with no workouts - ID: ${category.id}, Name: ${category.name}")
                                    }
                                }
                            }
                        }.onFailure { error ->
                            Timber.e(error, "ExerciseViewModel: Error loading workout types")
                            _error.value = "Failed to load workout types: ${error.message}"
                        }
                    }
                }.onFailure { error ->
                    Timber.e(error, "ExerciseViewModel: Error loading categories")
                    _error.value = "Failed to load categories: ${error.message}"
                }
            } catch (e: Exception) {
                Timber.e(e, "ExerciseViewModel: Exception during data loading")
                _error.value = "Failed to load data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshData() {
        Timber.d("ExerciseViewModel: Refreshing data")
        loadData()
    }
    
    fun clearError() {
        _error.value = null
    }
} 