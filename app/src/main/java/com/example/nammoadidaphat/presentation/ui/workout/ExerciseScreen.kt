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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel

@Composable
fun ExerciseScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    // Data for different exercise categories
    val beginnerExercises = listOf(
        ExerciseItem("Mỗi sáng thức dậy", "Beginner", R.drawable.ic_fitness, 2),
        ExerciseItem("Kéo dãn cơ thể", "Beginner", R.drawable.ic_yoga, 2)
    )
    
    val calorieBurnExercises = listOf(
        ExerciseItem("Cardio Toàn Thân", "Intermediate", R.drawable.ic_fitness, 2),
        ExerciseItem("HIIT Đốt Calo", "Advanced", R.drawable.ic_fitness, 2)
    )
    
    val bodyPartExercises = listOf(
        ExerciseItem("Đôi Tay Khỏe", "Intermediate", R.drawable.ic_chest, 2),
        ExerciseItem("Đôi Chân Khỏe", "Intermediate", R.drawable.ic_leg, 2)
    )
    
    val movementExercises = listOf(
        ExerciseItem("Yoga Cơ Bản", "Beginner", R.drawable.ic_yoga, 2),
        ExerciseItem("Stretching", "Beginner", R.drawable.ic_yoga, 2)
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
            
            // For Beginners Section
            item {
                CategoryHeader(title = "DÀNH CHO NGƯỜI MỚI")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(beginnerExercises) { exercise ->
                        ExerciseCard(exercise)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Quick Calorie Burn Section
            item {
                CategoryHeader(title = "ĐỐT CALO NHANH")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(calorieBurnExercises) { exercise ->
                        ExerciseCard(exercise)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Exercise by Body Part Section
            item {
                CategoryHeader(title = "TẬP THEO BỘ PHẬN")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(bodyPartExercises) { exercise ->
                        ExerciseCard(exercise)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            // Movements Section
            item {
                CategoryHeader(title = "ĐỘNG TÁC")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 8.dp)
                ) {
                    items(movementExercises) { exercise ->
                        ExerciseCard(exercise)
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
fun ExerciseCard(exercise: ExerciseItem) {
    Card(
        modifier = Modifier
            .width(240.dp)
            .height(180.dp)
            .clickable { /* Handle exercise click */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background image
            Image(
                painter = painterResource(id = exercise.imageRes),
                contentDescription = exercise.title,
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
            
            // Exercise details at the bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = exercise.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Rating stars
                Row {
                    repeat(exercise.rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFF7B50E8),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    repeat(5 - exercise.rating) {
                        Icon(
                            imageVector = Icons.Outlined.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFF7B50E8),
                            modifier = Modifier.size(20.dp)
                        )
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

data class ExerciseItem(
    val title: String,
    val level: String,
    val imageRes: Int,
    val rating: Int // Rating from 1-5
) 