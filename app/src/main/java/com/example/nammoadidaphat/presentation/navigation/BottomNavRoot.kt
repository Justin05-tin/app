package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen
import com.example.nammoadidaphat.presentation.ui.profile.ProfileScreen
import com.example.nammoadidaphat.presentation.ui.report.ReportScreen
import com.example.nammoadidaphat.presentation.ui.workout.ExerciseScreen
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutLevelDetailScreen
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutLevelsScreen
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutSessionScreen
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.ExerciseViewModel
import com.example.nammoadidaphat.presentation.viewmodel.HomeViewModel
import com.example.nammoadidaphat.presentation.viewmodel.ReportViewModel
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutLevelDetailViewModel
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutLevelsViewModel
import com.example.nammoadidaphat.presentation.viewmodel.WorkoutSessionViewModel

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
                val authViewModel = hiltViewModel<AuthViewModel>()
                val homeViewModel = hiltViewModel<HomeViewModel>()
                
                HomeScreen(
                    navController = mainNavController,
                    authViewModel = authViewModel,
                    homeViewModel = homeViewModel
                ) 
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
            
            // Add route for workout session screen
            composable(
                route = "workout_session/{levelId}",
                arguments = listOf(
                    navArgument("levelId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val levelId = backStackEntry.arguments?.getString("levelId") ?: ""
                val workoutSessionViewModel = hiltViewModel<WorkoutSessionViewModel>()
                WorkoutSessionScreen(
                    navController = navController,
                    viewModel = workoutSessionViewModel,
                    levelId = levelId
                )
            }
            
            composable(BottomNavScreen.Report.route) { 
                val reportViewModel = hiltViewModel<ReportViewModel>()
                val authViewModel = hiltViewModel<AuthViewModel>()
                
                // Refresh data when navigating to this screen
                LaunchedEffect(Unit) {
                    reportViewModel.refreshData()
                }
                
                ReportScreen(
                    reportViewModel = reportViewModel,
                    authViewModel = authViewModel
                ) 
            }
            
            composable(BottomNavScreen.Profile.route) { 
                ProfileScreen(
                    navController = mainNavController,
                    viewModel = hiltViewModel()
                ) 
            }
        }
    }
} 