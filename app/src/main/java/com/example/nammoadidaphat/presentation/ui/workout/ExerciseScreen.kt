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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.nammoadidaphat.domain.model.Category
import com.example.nammoadidaphat.domain.model.WorkoutType
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.ExerciseViewModel

@Composable
fun ExerciseScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    exerciseViewModel: ExerciseViewModel
) {
    // Collect state from view model
    val categories by exerciseViewModel.categories.collectAsState()
    val workoutTypesByCategory by exerciseViewModel.workoutTypesByCategory.collectAsState()
    val isLoading by exerciseViewModel.isLoading.collectAsState()
    val error by exerciseViewModel.error.collectAsState()
    
    // Trigger data loading when the screen is first displayed
    LaunchedEffect(key1 = Unit) {
        exerciseViewModel.loadData()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
        
        // Show categories and workout types when loaded
        if (!isLoading && error == null) {
            // Single scrollable LazyColumn for the entire screen
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Add padding for bottom navigation
            ) {
                // Header
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Bài tập",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                // Display each category and its workout types
                categories.forEach { category ->
                    item {
                        CategoryHeader(title = "${category.name.uppercase()}")
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Get workout types for this category
                        val workoutTypes = workoutTypesByCategory[category.id] ?: emptyList()
                        
                        if (workoutTypes.isEmpty()) {
                            Text(
                                text = "Không có bài tập cho danh mục này (Category ID: ${category.id})",
                                color = Color.Gray,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(end = 8.dp)
                            ) {
                                items(workoutTypes) { workoutType ->
                                    WorkoutTypeCard(
                                        workoutType = workoutType,
                                        onClick = {
                                            navController.navigate("workout_levels/${workoutType.id}")
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun WorkoutTypeCard(
    workoutType: WorkoutType,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image using Coil for network images
            Image(
                painter = rememberAsyncImagePainter(
                    model = if (workoutType.imageUrl.isNotBlank()) workoutType.imageUrl else R.drawable.ic_fitness
                ),
                contentDescription = workoutType.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = 900f
                        )
                    )
            )
            
            // Workout type details at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = workoutType.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Description
                if (workoutType.description.isNotBlank()) {
                    Text(
                        text = workoutType.description,
                        fontSize = 14.sp,
                        color = Color.LightGray,
                        maxLines = 2
                    )
                }
                
                // Display additional information if available as badges
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Display difficulty if available
                    workoutType.difficulty?.let { difficulty ->
                        if (difficulty.isNotBlank()) {
                            Text(
                                text = difficulty,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF8B5CF6).copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    
                    // Display duration if available
                    workoutType.duration?.let { duration ->
                        if (duration.isNotBlank()) {
                            Text(
                                text = duration,
                                fontSize = 12.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF2DD4BF).copy(alpha = 0.7f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
            
            // Bookmark icon
            IconButton(
                onClick = { /* Handle bookmark */ },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Bookmark",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
} 