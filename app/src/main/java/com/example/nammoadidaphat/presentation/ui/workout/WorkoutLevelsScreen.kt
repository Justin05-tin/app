package com.example.nammoadidaphat.presentation.ui.workout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.Level
import com.example.nammoadidaphat.domain.model.WorkoutType
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutLevelsViewModel

@Composable
fun WorkoutLevelsScreen(
    navController: NavController,
    viewModel: WorkoutLevelsViewModel,
    workoutTypeId: String
) {
    // Load data for the specified workout type
    LaunchedEffect(key1 = workoutTypeId) {
        viewModel.loadWorkoutType(workoutTypeId)
    }
    
    // Observe state from view model
    val workoutType by viewModel.workoutType.collectAsState()
    val levels by viewModel.levels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                    text = workoutType?.name ?: "Tập luyện",
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
            
            // Show levels when loaded
            if (!isLoading && error == null && levels.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(levels) { level ->
                        LevelCard(
                            level = level,
                            workoutType = workoutType
                        )
                    }
                }
            }
            
            // Show empty state
            if (!isLoading && error == null && levels.isEmpty() && workoutType != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Không tìm thấy các cấp độ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Vui lòng thử lại sau",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun LevelCard(
    level: Level,
    workoutType: WorkoutType?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Handle level click */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level image - use parent workout type's image
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = if (workoutType?.imageUrl?.isNotBlank() == true) workoutType.imageUrl else R.drawable.ic_fitness
                    ),
                    contentDescription = level.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Level info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = level.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rating stars based on difficulty level
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val rating = when(level.difficulty.lowercase()) {
                        "beginner" -> 1
                        "easy" -> 2
                        "intermediate" -> 3
                        "advanced" -> 4
                        "expert" -> 5
                        else -> 1
                    }
                    
                    repeat(rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    repeat(5 - rating) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
            
            // Duration and calories
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Duration
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    Text(
                        text = "${level.durationMinutes} m",
                        fontSize = 14.sp,
                        color = Color(0xFF0EA5E9),
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Calories
                Text(
                    text = "${level.caloriesBurn} kcal",
                    fontSize = 14.sp,
                    color = Color(0xFFF97316),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 