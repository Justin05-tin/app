package com.example.nammoadidaphat.presentation.navigation

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammoadidaphat.presentation.ui.auth.ForgotPasswordScreen
import com.example.nammoadidaphat.presentation.ui.auth.LoginScreen
import com.example.nammoadidaphat.presentation.ui.auth.RegisterScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.OnboardingScreen
import com.example.nammoadidaphat.presentation.viewmodel.AuthState
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.OnboardingViewModel
import com.example.nammoadidaphat.ui.theme.PetPackLoginTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make the app edge-to-edge but keep status bar and navigation bar visible
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        
        // Make system bars transparent but visible
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.apply {
            isAppearanceLightStatusBars = false // Use dark icons on light status bar
            isAppearanceLightNavigationBars = false // Use dark icons on light navigation bar
        }
        
        setContent {
            PetPackLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    
                    val hasSeenOnboarding by onboardingViewModel.hasSeenOnboarding.collectAsState()
                    val authState by authViewModel.authState.collectAsState()
                    
                    // Khi người dùng đã đăng nhập, đánh dấu đã xem onboarding
                    LaunchedEffect(authState) {
                        if (authState is AuthState.Authenticated) {
                            onboardingViewModel.saveOnboardingCompleted()
                        }
                    }
                    
                    // Determine start destination based on authentication state and onboarding status first
                    val startDestination = when {
                        authState is AuthState.Authenticated -> "main"
                        hasSeenOnboarding -> "login"
                        else -> "onboarding"
                    }

                    NavHost(
                        navController = navController, 
                        startDestination = startDestination
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(
                                navController = navController,
                                viewModel = onboardingViewModel
                            )
                        }
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("register") {
                            RegisterScreen(navController = navController)
                        }
                        composable("main") {
                            BottomNavRoot(authViewModel = authViewModel, mainNavController = navController)
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
