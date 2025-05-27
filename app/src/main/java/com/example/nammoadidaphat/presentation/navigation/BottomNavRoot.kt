package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavController
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen
import com.example.nammoadidaphat.presentation.ui.workout.ExerciseScreen
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutLevelDetailScreen
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutLevelsScreen
import com.example.nammoadidaphat.presentation.ui.report.ReportScreen
import com.example.nammoadidaphat.presentation.ui.profile.ProfileScreen
import com.example.nammoadidaphat.presentation.ui.profile.ProfileViewModel
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.ui.theme.ThemeViewModel
import com.example.nammoadidaphat.presentation.viewmodel.ExerciseViewModel
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutLevelDetailViewModel
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutLevelsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavRoot(
    authViewModel: AuthViewModel,
    mainNavController: NavController
) {
    val navController = rememberNavController()
    val items = BottomNavScreen.items
    
    // Get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Check if current screen is one of the main screens
    val isMainScreen = currentRoute == BottomNavScreen.Overview.route ||
                      currentRoute == BottomNavScreen.Workout.route ||
                      currentRoute == BottomNavScreen.Report.route ||
                      currentRoute == BottomNavScreen.Profile.route
    
    Scaffold(
        bottomBar = {
            // Only show bottom navigation if we're on a main screen
            if (isMainScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background
                ) {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(
                                imageVector = screen.icon, 
                                contentDescription = screen.title,
                                tint = if (currentRoute == screen.route) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            ) },
                            label = { Text(
                                text = screen.title,
                                color = if (currentRoute == screen.route) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            ) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.background
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavScreen.Overview.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Overview.route) { 
                HomeScreen(navController = mainNavController, authViewModel = authViewModel) 
            }
            
            composable(BottomNavScreen.Workout.route) { 
                val exerciseViewModel = hiltViewModel<ExerciseViewModel>()
                ExerciseScreen(
                    navController = navController, 
                    authViewModel = authViewModel,
                    exerciseViewModel = exerciseViewModel
                ) 
            }
            
            composable(
                route = "workout_levels/{workoutTypeId}",
                arguments = listOf(
                    navArgument("workoutTypeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val workoutTypeId = backStackEntry.arguments?.getString("workoutTypeId") ?: ""
                val workoutLevelsViewModel = hiltViewModel<WorkoutLevelsViewModel>()
                WorkoutLevelsScreen(
                    navController = navController,
                    viewModel = workoutLevelsViewModel,
                    workoutTypeId = workoutTypeId
                )
            }
            
            composable(
                route = "workout_level_detail/{levelId}",
                arguments = listOf(
                    navArgument("levelId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
                val workoutLevelDetailViewModel = hiltViewModel<WorkoutLevelDetailViewModel>()
                WorkoutLevelDetailScreen(
                    navController = navController,
                    viewModel = workoutLevelDetailViewModel,
                    levelId = levelId
                )
            }
            
            composable(BottomNavScreen.Report.route) { 
                ReportScreen() 
            }
            
            composable(BottomNavScreen.Profile.route) { 
                val profileViewModel = hiltViewModel<ProfileViewModel>()
                val themeViewModel = hiltViewModel<ThemeViewModel>()
                ProfileScreen(
                    navController = mainNavController,
                    viewModel = profileViewModel,
                    themeViewModel = themeViewModel
                ) 
            }
        }
    }
} 