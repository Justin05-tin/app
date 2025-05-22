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
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen
import com.example.nammoadidaphat.presentation.ui.workout.WorkoutScreen
import com.example.nammoadidaphat.presentation.ui.report.ReportScreen
import com.example.nammoadidaphat.presentation.ui.profile.ProfileScreen
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavRoot() {
    val navController = rememberNavController()
    val items = BottomNavScreen.items
    val authViewModel: AuthViewModel = hiltViewModel()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
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
                HomeScreen(navController, authViewModel) 
            }
            composable(BottomNavScreen.Workout.route) { 
                WorkoutScreen() 
            }
            composable(BottomNavScreen.Report.route) { 
                ReportScreen() 
            }
            composable(BottomNavScreen.Profile.route) { 
                ProfileScreen() 
            }
        }
    }
} 