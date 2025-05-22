package com.example.nammoadidaphat.presentation.navigation

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammoadidaphat.presentation.ui.auth.ForgotPasswordScreen
import com.example.nammoadidaphat.presentation.ui.auth.LoginScreen
import com.example.nammoadidaphat.presentation.ui.auth.RegisterScreen
import com.example.nammoadidaphat.presentation.ui.home.HomeScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.OnboardingScreen
import com.example.nammoadidaphat.presentation.viewmodel.OnboardingViewModel
import com.example.nammoadidaphat.ui.theme.PetPackLoginTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

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
        
        // Sử dụng biến trong Activity để theo dõi trạng thái hiển thị màn hình onboarding
        var isFirstLaunch = true
        
        setContent {
            PetPackLoginTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                    val hasSeenOnboarding by onboardingViewModel.hasSeenOnboarding.collectAsState()
                    
                    // Luôn hiển thị màn hình onboarding khi lần đầu mở ứng dụng
                    val startDestination = if (isFirstLaunch) {
                        isFirstLaunch = false
                        "onboarding"
                    } else if (hasSeenOnboarding) {
                        "login"
                    } else {
                        "onboarding"
                    }

                    NavHost(
                        navController = navController, 
                        startDestination = startDestination
                    ) {
                        composable("onboarding") {
                            OnboardingScreen(navController = navController)
                        }
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
