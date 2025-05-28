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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.WorkoutType
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.HomeViewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val currentUser = authViewModel.currentUser.collectAsState().value
    
    // Loading and error states
    val isLoading by homeViewModel.isLoading.collectAsState()
    val error by homeViewModel.error.collectAsState()
    
    // State for selected featured workout tab
    var selectedFeaturedTab by remember { mutableStateOf("Beginner") }
    val featuredTabs = listOf("Beginner", "Intermediate", "Advanced")
    
    // State for selected workout level
    var selectedWorkoutLevel by remember { mutableStateOf("Beginner") }
    val workoutLevels = listOf("Beginner", "Intermediate", "Advanced")
    
    // Observe featured workouts based on selected tab
    val featuredWorkouts = homeViewModel.getFeaturedWorkoutsByLevel(selectedFeaturedTab)
    
    // Observe workout exercises for selected level
    val workoutExercises = homeViewModel.getWorkoutsByLevel(selectedWorkoutLevel)
    
    // Add a refresh effect when the view model is initialized
    LaunchedEffect(Unit) {
        homeViewModel.loadWorkouts()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Show loading state
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        // Show error message if any
        error?.let { errorMessage ->
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        }
        
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
                if (featuredWorkouts.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No featured workouts found for $selectedFeaturedTab level",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(end = 8.dp),
                        modifier = Modifier.height(200.dp)
                    ) {
                        items(featuredWorkouts) { workout ->
                            FeaturedWorkoutCard(
                                workout = workout,
                                onClick = {
                                    // Navigate to workout details
                                    navController.navigate("workout_levels/${workout.id}")
                                }
                            )
                        }
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
            
            // Workout exercises list - only show when not loading
            if (!isLoading) {
                if (workoutExercises.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No workouts found for $selectedWorkoutLevel level",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    items(workoutExercises) { exercise ->
                        ExerciseCard(
                            workout = exercise,
                            onClick = {
                                // Navigate to workout details
                                navController.navigate("workout_levels/${exercise.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FeaturedWorkoutCard(workout: WorkoutType, onClick: () -> Unit) {
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
            // Background image - using Coil for network images
            if (workout.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = workout.imageUrl,
                    contentDescription = workout.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback to a placeholder
                Image(
                    painter = painterResource(id = R.drawable.ic_fitness),
                    contentDescription = workout.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
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
                    text = workout.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${workout.duration ?: "10 min"}  |  ",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    
                    Text(
                        text = workout.difficulty ?: "Beginner",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
            
            // Favorite icon
            IconButton(
                onClick = { /* Handle favorite */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun WorkoutLevelButton(level: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
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
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ExerciseCard(workout: WorkoutType, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
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
            // Background image - using Coil for network images
            if (workout.imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = workout.imageUrl,
                    contentDescription = workout.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Fallback to a placeholder based on the name
                val imageRes = when {
                    workout.name.contains("leg", ignoreCase = true) -> R.drawable.ic_leg
                    workout.name.contains("chest", ignoreCase = true) -> R.drawable.ic_chest
                    workout.name.contains("abs", ignoreCase = true) -> R.drawable.ic_abs
                    workout.name.contains("yoga", ignoreCase = true) -> R.drawable.ic_yoga
                    else -> R.drawable.ic_fitness
                }
                
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = workout.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
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
                    text = workout.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${workout.duration ?: "10 min"}  |  ",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                    
                    Text(
                        text = workout.difficulty ?: "Beginner",
                        fontSize = 14.sp,
                        color = Color.White
                    )
                }
            }
            
            // Favorite icon
            IconButton(
                onClick = { /* Handle favorite */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
