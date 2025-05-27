package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen
import com.example.nammoadidaphat.presentation.ui.workout.ExerciseScreen
import com.example.nammoadidaphat.presentation.ui.report.ReportScreen
import com.example.nammoadidaphat.presentation.ui.profile.ProfileScreen
import com.example.nammoadidaphat.presentation.ui.profile.ProfileViewModel
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.ui.theme.ThemeViewModel
import com.example.nammoadidaphat.presentation.viewmodel.ExerciseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavRoot(
    authViewModel: AuthViewModel,
    mainNavController: NavController
) {
    val navController = rememberNavController()
    val items = BottomNavScreen.items
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
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
                    navController = mainNavController, 
                    authViewModel = authViewModel,
                    exerciseViewModel = exerciseViewModel
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