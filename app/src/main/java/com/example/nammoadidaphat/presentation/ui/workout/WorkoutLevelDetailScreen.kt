package com.example.nammoadidaphat.presentation.ui.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.Exercise
import com.example.nammoadidaphat.domain.model.Level
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutLevelDetailViewModel
import androidx.compose.foundation.verticalScroll
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import coil.decode.GifDecoder
import androidx.compose.ui.platform.LocalContext

@Composable
fun WorkoutLevelDetailScreen(
    navController: NavController,
    viewModel: WorkoutLevelDetailViewModel,
    levelId: String
) {
    // Load data for the specified level
    LaunchedEffect(key1 = levelId) {
        viewModel.loadLevelWithExercises(levelId)
    }
    
    // Observe state from view model
    val level by viewModel.level.collectAsState()
    val exercises by viewModel.exercises.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Variable to track current exercise number to display in the center
    val currentExerciseNumber = remember { mutableStateOf(1) }
    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = level?.name ?: "Chi tiết bài tập",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show loading indicator
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF8B5CF6)
                )
            }
            
            // Show error if exists
            error?.let {
                Text(
                    text = "Error: $it",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            
            // Show level details and exercises when loaded
            if (!isLoading && error == null && level != null) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp) // Add bottom padding for the button
                    ) {
                        // Level summary
                        LevelSummary(level = level!!, exercises = exercises)
                        
                        // Divider
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = Color.LightGray.copy(alpha = 0.5f)
                        )
                        
                        // Exercises list
                        ExercisesList(
                            exercises = exercises,
                            onExerciseVisible = { exerciseNumber -> 
                                currentExerciseNumber.value = exerciseNumber 
                            }
                        )
                    }
                    
                    // Start button - positioned at the bottom
                    Button(
                        onClick = { 
                            // Navigate to workout session screen with the level ID
                            navController.navigate("workout_session/${levelId}")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 16.dp)
                            .height(56.dp)
                            .align(Alignment.BottomCenter), // Align at bottom center
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8B5CF6)
                        )
                    ) {
                        Text(
                            text = "Bắt Đầu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            

        }
    }
}

@Composable
fun LevelSummary(level: Level, exercises: List<Exercise>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val exerciseCount = exercises.size
        
        Text(
            text = "$exerciseCount động tác",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        // Calories
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(end = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Calories",
                tint = Color(0xFFF97316),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "${level.caloriesBurn} kcal",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF97316)
            )
        }
        
        // Duration
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Duration",
                tint = Color(0xFF0EA5E9),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = "${level.durationMinutes} m",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF0EA5E9)
            )
        }
    }
}

@Composable
fun ExercisesList(
    exercises: List<Exercise>,
    onExerciseVisible: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(exercises) { index, exercise ->
            val exerciseNumber = index + 1
            // Update the current visible exercise
            onExerciseVisible(exerciseNumber)
            ExerciseItem(exerciseNumber = exerciseNumber, exercise = exercise)
        }
    }
}

@Composable
fun ExerciseItem(exerciseNumber: Int, exercise: Exercise) {
    var showDetailDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Exercise number
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = exerciseNumber.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Exercise image
        Card(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            if (exercise.image.isNotBlank()) {
                // Check if image URL ends with .gif
                if (exercise.image.endsWith(".gif", ignoreCase = true)) {
                    // Use SubcomposeAsyncImage for GIF support
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(exercise.image)
                            .decoderFactory(GifDecoder.Factory())
                            .build(),
                        contentDescription = exercise.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Regular image
                    Image(
                        painter = rememberAsyncImagePainter(model = exercise.image),
                        contentDescription = exercise.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Fallback image
                Image(
                    painter = rememberAsyncImagePainter(model = R.drawable.ic_fitness),
                    contentDescription = exercise.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Exercise info
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = exercise.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Display either duration in seconds or repetition count
            val exerciseDetail = if (exercise.duration > 0) {
                "${exercise.duration} s"
            } else if (exercise.reps > 0) {
                "x${exercise.reps}"
            } else {
                "x10" // Default if no duration or reps specified
            }
            
            Text(
                text = exerciseDetail,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        
        // Info button
        IconButton(onClick = { showDetailDialog = true }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Thông tin chi tiết",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
    
    // Light divider between exercises
    HorizontalDivider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 72.dp),
        color = Color.LightGray.copy(alpha = 0.3f)
    )
    
    // Exercise detail dialog
    if (showDetailDialog) {
        ExerciseDetailDialog(
            exercise = exercise,
            onDismiss = { showDetailDialog = false }
        )
    }
}

@Composable
fun ExerciseDetailDialog(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = exercise.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Image
                if (exercise.image.isNotBlank()) {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        // Check if image URL ends with .gif
                        if (exercise.image.endsWith(".gif", ignoreCase = true)) {
                            // Use SubcomposeAsyncImage for GIF support
                            SubcomposeAsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(exercise.image)
                                    .decoderFactory(GifDecoder.Factory())
                                    .build(),
                                contentDescription = exercise.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Regular image
                            Image(
                                painter = rememberAsyncImagePainter(model = exercise.image),
                                contentDescription = exercise.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                
                // Description
                if (exercise.description.isNotBlank()) {
                    Text(
                        text = "Mô tả",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    Text(
                        text = exercise.description,
                        fontSize = 14.sp
                    )
                }
                
                // Equipment
                if (exercise.equipment.isNotBlank()) {
                    Text(
                        text = "Dụng cụ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    Text(
                        text = exercise.equipment,
                        fontSize = 14.sp
                    )
                }
                
                // Instructions
                if (exercise.instructions.isNotEmpty()) {
                    Text(
                        text = "Hướng dẫn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    exercise.instructions.forEachIndexed { index, instruction ->
                        Row(
                            modifier = Modifier.padding(bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "${index + 1}.",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 4.dp, top = 2.dp)
                            )
                            
                            Text(
                                text = instruction,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Tips
                if (exercise.tips.isNotEmpty()) {
                    Text(
                        text = "Mẹo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    
                    exercise.tips.forEachIndexed { _, tip ->
                        Row(
                            modifier = Modifier.padding(bottom = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 4.dp, top = 2.dp)
                            )
                            
                            Text(
                                text = tip,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                // Exercise metrics
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Duration
                    if (exercise.duration > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Thời gian",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Text(
                                text = "${exercise.duration}s",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Reps
                    if (exercise.reps > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Số lần",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Text(
                                text = "${exercise.reps}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Rest time
                    if (exercise.restTime > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nghỉ",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Text(
                                text = "${exercise.restTime}s",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Calories
                    if (exercise.caloriesBurn > 0) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Calories",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            
                            Text(
                                text = "${exercise.caloriesBurn}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Đóng")
            }
        }
    )
} 