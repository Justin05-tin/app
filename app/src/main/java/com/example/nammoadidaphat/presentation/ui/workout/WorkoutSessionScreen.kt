package com.example.nammoadidaphat.presentation.ui.workout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.Exercise
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutSessionViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

enum class WorkoutState {
    READY,      // Initial ready countdown
    EXERCISE,   // Performing exercise
    REST        // Rest between exercises
}

@Composable
fun WorkoutSessionScreen(
    navController: NavController,
    viewModel: WorkoutSessionViewModel,
    levelId: String
) {
    val context = LocalContext.current
    
    // States
    val exercises by viewModel.exercises.collectAsState()
    val currentExerciseIndex by viewModel.currentExerciseIndex.collectAsState()
    val workoutState by viewModel.workoutState.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val totalExercises by viewModel.totalExercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Listen for navigation events
    LaunchedEffect(Unit) {
        viewModel.navigateBack.collect { shouldNavigate ->
            if (shouldNavigate) {
                navController.popBackStack()
            }
        }
    }
    
    // Progress animation
    val progressPercentage = if (workoutState == WorkoutState.READY) {
        (timeRemaining.toFloat() / 12f).coerceIn(0f, 1f)
    } else if (workoutState == WorkoutState.EXERCISE) {
        val currentExercise = exercises.getOrNull(currentExerciseIndex)
        val totalTime = currentExercise?.duration?.toFloat() ?: 30f
        (timeRemaining.toFloat() / totalTime).coerceIn(0f, 1f)
    } else { // REST
        val currentExercise = exercises.getOrNull(currentExerciseIndex)
        val restTime = currentExercise?.restTime?.toFloat() ?: 10f
        (timeRemaining.toFloat() / restTime).coerceIn(0f, 1f)
    }
    
    val animatedProgress by animateFloatAsState(
        targetValue = progressPercentage,
        label = "progressAnimation"
    )
    
    // Load exercises when screen is created
    LaunchedEffect(levelId) {
        viewModel.loadWorkoutSession(levelId)
    }
    
    // Background that fills the entire screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top section with close button and progress info
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Close button
                    IconButton(
                        onClick = {
                            // Show confirmation dialog before exiting
                            // For now, just navigate back
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Exercise name in center
                    val currentExercise = exercises.getOrNull(currentExerciseIndex)
                    currentExercise?.let {
                        Text(
                            text = it.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center
                        )
                    }
                    
                    // Exercise progress
                    Text(
                        text = "${currentExerciseIndex + 1}/$totalExercises",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                
                // Main circular progress indicator - smaller size
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer circle - background
                    CircularProgressIndicator(
                        progress = 1f,
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 12.dp
                    )
                    
                    // Progress circle - foreground
                    CircularProgressIndicator(
                        progress = animatedProgress,
                        modifier = Modifier.fillMaxSize(),
                        color = when (workoutState) {
                            WorkoutState.READY -> Color(0xFF4CAF50) // Green
                            WorkoutState.EXERCISE -> Color(0xFF2196F3) // Blue
                            WorkoutState.REST -> Color(0xFFFFA000) // Amber
                        },
                        strokeWidth = 12.dp
                    )
                    
                    // Center content
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Status text
                        Text(
                            text = when (workoutState) {
                                WorkoutState.READY -> "CHUẨN BỊ"
                                WorkoutState.EXERCISE -> "TẬP LUYỆN"
                                WorkoutState.REST -> "NGHỈ NGƠI"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        
                        // Timer
                        Text(
                            text = timeRemaining.toString(),
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        
                        // Exercise number if in exercise state
                        if (workoutState == WorkoutState.EXERCISE) {
                            Text(
                                text = "Bài #${currentExerciseIndex + 1}",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Exercise info section
                val currentExercise = exercises.getOrNull(currentExerciseIndex)
                currentExercise?.let { exercise ->
                    ExerciseInfoSection(exercise = exercise, workoutState = workoutState)
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Control buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pause/Resume button
                    FloatingActionButton(
                        onClick = { viewModel.togglePause() },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Filled.PlayArrow,
                            contentDescription = if (isPaused) "Resume" else "Pause"
                        )
                    }
                    
                    // Skip or Check button
                    FloatingActionButton(
                        onClick = { 
                            if (currentExerciseIndex == totalExercises - 1 && workoutState == WorkoutState.EXERCISE) {
                                viewModel.completeWorkout()
                            } else {
                                viewModel.skipToNext()
                            }
                        },
                        containerColor = if (currentExerciseIndex == totalExercises - 1 && workoutState == WorkoutState.EXERCISE)
                            Color(0xFF4CAF50) // Green for Check button
                        else 
                            MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = if (currentExerciseIndex == totalExercises - 1 && workoutState == WorkoutState.EXERCISE)
                            Color.White
                        else
                            MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Icon(
                            imageVector = if (currentExerciseIndex == totalExercises - 1 && workoutState == WorkoutState.EXERCISE) 
                                        Icons.Default.Check
                                    else 
                                        Icons.Default.ArrowForward,
                            contentDescription = if (currentExerciseIndex == totalExercises - 1 && workoutState == WorkoutState.EXERCISE) 
                                              "Complete" 
                                          else 
                                              "Skip"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseInfoSection(
    exercise: Exercise,
    workoutState: WorkoutState
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Only show exercise image during exercise or rest
            if (workoutState != WorkoutState.READY) {
                // Exercise image/gif - Increased size to be approximately half the screen
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (exercise.image.isNotBlank()) {
                        // Check if image URL ends with .gif
                        if (exercise.image.endsWith(".gif", ignoreCase = true)) {
                            // Use SubcomposeAsyncImage for GIF support
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(exercise.image)
                                    .decoderFactory(GifDecoder.Factory())
                                    .crossfade(true)
                                    .build(),
                                contentDescription = exercise.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize(),
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            )
                        } else {
                            // Regular image
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(context)
                                        .data(exercise.image)
                                        .crossfade(true)
                                        .build()
                                ),
                                contentDescription = exercise.name,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    } else {
                        // Fallback image
                        Image(
                            painter = rememberAsyncImagePainter(model = R.drawable.ic_fitness),
                            contentDescription = exercise.name,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Exercise metrics - Enhanced card design
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Duration or Reps
                        MetricItem(
                            title = if (exercise.duration > 0) "Thời gian" else "Số lần",
                            value = if (exercise.duration > 0) "${exercise.duration}s" else "x${exercise.reps}"
                        )
                        
                        // Rest time
                        if (exercise.restTime > 0) {
                            MetricItem(
                                title = "Nghỉ",
                                value = "${exercise.restTime}s"
                            )
                        }
                        
                        // Calories
                        if (exercise.caloriesBurn > 0) {
                            MetricItem(
                                title = "Calories",
                                value = "${exercise.caloriesBurn}"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(title: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
} 