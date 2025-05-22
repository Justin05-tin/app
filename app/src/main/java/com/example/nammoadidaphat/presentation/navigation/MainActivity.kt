package com.example.nammoadidaphat.presentation.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammoadidaphat.presentation.ui.auth.ForgotPasswordScreen
import com.example.nammoadidaphat.presentation.ui.auth.LoginScreen
import com.example.nammoadidaphat.presentation.ui.auth.RegisterScreen
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen
import com.example.nammoadidaphat.ui.theme.PetPackLoginTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetPackLoginTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("register") {
                            RegisterScreen(navController = navController)
                        }
                        composable("home") {
                            HomeScreen(navController = navController)
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
