package com.example.nammoadidaphat.presentation.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.AuthState
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay
import timber.log.Timber

@Composable
fun SplashScreen(
    navController: NavController,
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val hasSeenOnboarding by onboardingViewModel.hasSeenOnboarding.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    
    // Progress animation
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing)
    )
    
    // Logo animation
    val scale = remember { Animatable(0.5f) }
    
    // Animate logo and loading bar
    LaunchedEffect(Unit) {
        // Animate logo scaling up with bounce effect
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        )
        
        // Animate progress bar
        for (i in 1..10) {
            progress = i / 10f
            delay(150)
        }
        
        // Add a short delay to ensure animations complete
        delay(300)
        
        // Log navigation state for debugging
        Timber.d("Navigation decision: authState=${authState.javaClass.simpleName}, hasSeenOnboarding=$hasSeenOnboarding")
        
        // Navigate based on auth state and onboarding status
        when {
            authState is AuthState.Authenticated -> {
                Timber.d("User is authenticated, navigating to main screen")
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            hasSeenOnboarding -> {
                Timber.d("User has seen onboarding, navigating to login screen")
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            else -> {
                Timber.d("User has not seen onboarding, navigating to onboarding screen")
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }
    
    // Splash screen content
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.onboarding_background),
            contentDescription = "Splash background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Add an overlay for visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))
        )
        
        // Logo and Loading content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // App logo with animation
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(1000)) + 
                        slideInVertically(animationSpec = tween(1000)) { it / 2 }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .scale(scale.value)
                        .background(Color.Transparent)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App name
            Text(
                text = "EXS",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Loading indicator
            LinearProgressIndicator(
                progress = animatedProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color.White,
                trackColor = Color.Gray.copy(alpha = 0.3f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
} 