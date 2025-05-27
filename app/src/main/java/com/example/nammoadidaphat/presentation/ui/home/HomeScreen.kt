package com.example.nammoadidaphat.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()
    val currentUser = authViewModel.currentUser.collectAsState().value
    
    // State for selected featured workout tab
    var selectedFeaturedTab by remember { mutableStateOf("Beginner") }
    val featuredTabs = listOf("Beginner", "Intermediate", "Advanced")
    
    // State for selected workout level
    var selectedWorkoutLevel by remember { mutableStateOf("Beginner") }
    val workoutLevels = listOf("Beginner", "Intermediate", "Advanced")
    
    // Featured workouts based on selected tab
    val featuredWorkouts = when (selectedFeaturedTab) {
        "Intermediate" -> listOf(
            FeaturedWorkout("Full Body Stretching", "10 minutes", "Intermediate", R.drawable.ic_fitness),
            FeaturedWorkout("Yoga Poses", "15 minutes", "Intermediate", R.drawable.ic_fitness)
        )
        "Advanced" -> listOf(
            FeaturedWorkout("HIIT Workout", "20 minutes", "Advanced", R.drawable.ic_fitness),
            FeaturedWorkout("CrossFit Challenge", "25 minutes", "Advanced", R.drawable.ic_fitness)
        )
        else -> listOf(
            FeaturedWorkout("Easy Stretching", "8 minutes", "Beginner", R.drawable.ic_fitness),
            FeaturedWorkout("Body Weight Exercise", "12 minutes", "Beginner", R.drawable.ic_fitness)
        )
    }
    
    // Workout exercises for selected level
    val workoutExercises = when (selectedWorkoutLevel) {
        "Intermediate" -> listOf(
            WorkoutExercise("Squat Movement Exercise", "12 minutes", "Intermediate", R.drawable.ic_leg),
            WorkoutExercise("Push-up Challenge", "10 minutes", "Intermediate", R.drawable.ic_chest),
            WorkoutExercise("Core Strength", "15 minutes", "Intermediate", R.drawable.ic_abs),
            WorkoutExercise("Full Body Stretching", "6 minutes", "Intermediate", R.drawable.ic_fitness),
            WorkoutExercise("Yoga Women Exercise", "8 minutes", "Intermediate", R.drawable.ic_yoga),
            WorkoutExercise("Yoga Movement Exercise", "10 minutes", "Intermediate", R.drawable.ic_yoga),
            WorkoutExercise("Abdominal Exercise", "6 minutes", "Intermediate", R.drawable.ic_abs)
        )
        "Advanced" -> listOf(
            WorkoutExercise("Advanced Squats", "15 minutes", "Advanced", R.drawable.ic_leg),
            WorkoutExercise("Power Push-ups", "12 minutes", "Advanced", R.drawable.ic_chest),
            WorkoutExercise("Abs Crusher", "18 minutes", "Advanced", R.drawable.ic_abs),
            WorkoutExercise("Advanced Yoga", "10 minutes", "Advanced", R.drawable.ic_yoga),
            WorkoutExercise("Intense HIIT", "25 minutes", "Advanced", R.drawable.ic_fitness)
        )
        else -> listOf(
            WorkoutExercise("Basic Squats", "10 minutes", "Beginner", R.drawable.ic_leg),
            WorkoutExercise("Knee Push-ups", "8 minutes", "Beginner", R.drawable.ic_chest),
            WorkoutExercise("Simple Abs", "10 minutes", "Beginner", R.drawable.ic_abs),
            WorkoutExercise("Beginner Yoga", "8 minutes", "Beginner", R.drawable.ic_yoga),
            WorkoutExercise("Light Cardio", "15 minutes", "Beginner", R.drawable.ic_fitness)
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Single scrollable LazyColumn for the entire screen
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Header item
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Header with app logo, app name and notification/favorites icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo and App name
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7B50E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = stringResource(R.string.app_name),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    
                    // Icons for notification and favorites
                    Row {
                        IconButton(onClick = { /* Handle notification click */ }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        IconButton(onClick = { /* Handle favorites click */ }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favorites",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Greeting with user name
                Text(
                    text = "Morning, ${currentUser?.displayName?.split(" ")?.firstOrNull() ?: "User"} 👋",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            // Featured Workout section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Featured Workout",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    TextButton(onClick = { /* Handle See All click */ }) {
                        Text(
                            text = "See All",
                            fontSize = 16.sp,
                            color = Color(0xFF7B50E8)
                        )
                    }
                }
                
                // Featured workout tabs
                ScrollableTabRow(
                    selectedTabIndex = featuredTabs.indexOf(selectedFeaturedTab),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    edgePadding = 0.dp,
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFF7B50E8),
                    indicator = { tabPositions ->
                        // Tab indicator
                        TabRowDefaults.Indicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[featuredTabs.indexOf(selectedFeaturedTab)]),
                            height = 2.dp,
                            color = Color(0xFF7B50E8)
                        )
                    },
                    divider = {}
                ) {
                    featuredTabs.forEach { tab ->
                        Tab(
                            selected = selectedFeaturedTab == tab,
                            onClick = { selectedFeaturedTab = tab },
                            text = {
                                Text(
                                    text = tab,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedFeaturedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedFeaturedTab == tab) Color(0xFF7B50E8) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
            
            // Featured workout horizontal list
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(end = 8.dp),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(featuredWorkouts) { workout ->
                        FeaturedWorkoutCard(workout)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Workout Levels section header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Workout Levels",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    TextButton(onClick = { /* Handle See All click */ }) {
                        Text(
                            text = "See All",
                            fontSize = 16.sp,
                            color = Color(0xFF7B50E8)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Workout levels selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    workoutLevels.forEach { level ->
                        WorkoutLevelButton(
                            level = level, 
                            isSelected = level == selectedWorkoutLevel,
                            onClick = { selectedWorkoutLevel = level }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            // Workout exercises list
            items(workoutExercises) { exercise ->
                ExerciseCard(
                    title = exercise.title,
                    time = exercise.time,
                    level = exercise.level,
                    imageRes = exercise.imageRes
                )
            }
        }
    }
}

@Composable
fun FeaturedWorkoutCard(workout: FeaturedWorkout) {
    Card(
        modifier = Modifier
            .width(240.dp)  // Made smaller to prevent text wrapping
            .height(180.dp)
            .clickable { /* Handle workout click */ },
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
                painter = painterResource(id = workout.imageRes),
                contentDescription = workout.title,
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
            
            // Workout details
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = workout.title,
                    fontSize = 18.sp,  // Smaller font size
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1  // Prevent text wrapping
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${workout.time}  |  ",
                        fontSize = 14.sp,  // Smaller font size
                        color = Color.White
                    )
                    
                    Text(
                        text = workout.level,
                        fontSize = 14.sp,  // Smaller font size
                        color = Color.White
                    )
                }
            }
            
            // Favorite icon
            IconButton(
                onClick = { /* Handle favorite */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)  // Smaller padding
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)  // Smaller icon
                )
            }
        }
    }
}

@Composable
fun WorkoutLevelButton(level: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),  // Smaller height
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF7B50E8) else Color.White,
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = if (isSelected) 0.dp else 2.dp
        )
    ) {
        Text(
            text = level,
            fontSize = 14.sp,  // Smaller font size
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ExerciseCard(title: String, time: String, level: String, imageRes: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)  // Smaller height
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
                painter = painterResource(id = imageRes),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            
            // Gradient overlay
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
            
            // Exercise details
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,  // Smaller font size
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1  // Prevent text wrapping
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$time  |  ",
                        fontSize = 14.sp,  // Smaller font size
                        color = Color.White
                    )
                    
                    Text(
                        text = level,
                        fontSize = 14.sp,  // Smaller font size
                        color = Color.White
                    )
                }
            }
            
            // Favorite icon
            IconButton(
                onClick = { /* Handle favorite */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)  // Smaller padding
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)  // Smaller icon
                )
            }
        }
    }
}

data class FeaturedWorkout(
    val title: String,
    val time: String,
    val level: String,
    val imageRes: Int
)

data class WorkoutExercise(
    val title: String,
    val time: String,
    val level: String,
    val imageRes: Int
)
