package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammoadidaphat.presentation.ui.auth.LoginScreen
import com.example.nammoadidaphat.presentation.ui.auth.RegisterScreen
import com.example.nammoadidaphat.presentation.ui.auth.ForgotPasswordScreen
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("home") { HomeScreen(navController) }
    }
}
