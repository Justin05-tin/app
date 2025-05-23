package com.example.nammoadidaphat.presentation.navigation

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nammoadidaphat.data.repository.AuthRepositoryImpl
import com.example.nammoadidaphat.presentation.ui.auth.ForgotPasswordScreen
import com.example.nammoadidaphat.presentation.ui.auth.LoginScreen
import com.example.nammoadidaphat.presentation.ui.auth.RegisterScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.OnboardingScreen
import com.example.nammoadidaphat.presentation.ui.splash.SplashScreen
import com.example.nammoadidaphat.presentation.viewmodel.AuthState
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.OnboardingViewModel
import com.example.nammoadidaphat.ui.theme.PetPackLoginTheme
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    @Inject
    lateinit var authRepository: AuthRepositoryImpl
    
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private lateinit var authViewModel: AuthViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    
    // Firebase Auth instance for direct access if needed
    private lateinit var firebaseAuth: FirebaseAuth
    
    // Store a reference to the current NavController for navigation from activity scope
    private var currentNavHostController: NavController? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Generate and display Facebook Key Hash for debugging
        generateFacebookKeyHash()
        
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()
        
        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)
        
        // Get ViewModel the proper way
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        
        // Set up Google Sign-In
        setupGoogleSignIn()
        
        // Register for Google Sign-In activity result
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                // Set loading state to true in the UI
                authViewModel.handleGoogleSignInIntent(result.data)
                
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    Log.d(TAG, "Google sign in successful: ${account.email}")
                    
                    // Forward the intent to the view model for Firebase authentication
                    lifecycleScope.launch {
                        val authResult = authViewModel.handleGoogleSignInResult(result.data)
                        if (authResult.isSuccess) {
                            val user = authResult.getOrNull()
                            
                            // Check if user needs onboarding
                            if (authViewModel.needsOnboarding(user)) {
                                // New user or incomplete profile - navigate to onboarding
                                Log.d(TAG, "Navigating to user_onboarding: incomplete profile")
                                
                                // Use the current top-level NavController to navigate
                                val currentDestination = currentNavHostController?.currentDestination?.route
                                currentNavHostController?.navigate("user_onboarding") {
                                    // Pop up to the current destination or login if not available
                                    popUpTo(currentDestination ?: "login") { inclusive = true }
                                }
                            } else {
                                // Returning user with complete profile - navigate to main
                                Log.d(TAG, "Navigating to main: complete profile")
                                
                                // Use the current top-level NavController to navigate
                                val currentDestination = currentNavHostController?.currentDestination?.route
                                currentNavHostController?.navigate("main") {
                                    // Pop up to the current destination or login if not available
                                    popUpTo(currentDestination ?: "login") { inclusive = true }
                                }
                            }
                            
                            Toast.makeText(this@MainActivity, "Sign in successful", Toast.LENGTH_SHORT).show()
                        } else {
                            val exception = authResult.exceptionOrNull()
                            Toast.makeText(this@MainActivity, 
                                "Sign in failed: ${exception?.message ?: "Unknown error"}",
                                Toast.LENGTH_SHORT).show()
                            Log.e(TAG, "Google sign in failed", exception)
                        }
                    }
                } catch (e: ApiException) {
                    // Google Sign In failed
                    Log.e(TAG, "Google sign in failed", e)
                    Toast.makeText(this@MainActivity, 
                        "Google sign in failed: ${e.message}", 
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.d(TAG, "Google sign in was cancelled")
            }
        }
        
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
                    // Store a reference to the NavController
                    currentNavHostController = navController
                    
                    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
                    val composeAuthViewModel: AuthViewModel = hiltViewModel()

                    // When user signs in, mark onboarding as completed
                    val authState by composeAuthViewModel.authState.collectAsState()
                    LaunchedEffect(authState) {
                        if (authState is AuthState.Authenticated) {
                            onboardingViewModel.saveOnboardingCompleted()
                        }
                    }
                    
                    // Always start with splash screen which will handle all the navigation logic
                    NavHost(
                        navController = navController, 
                        startDestination = "splash"
                    ) {
                        composable("splash") {
                            SplashScreen(
                                navController = navController,
                                onboardingViewModel = onboardingViewModel,
                                authViewModel = composeAuthViewModel
                            )
                        }
                        composable("onboarding") {
                            OnboardingScreen(
                                navController = navController,
                                viewModel = onboardingViewModel
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                viewModel = composeAuthViewModel,
                                onGoogleSignInClicked = { 
                                    startGoogleSignIn()
                                },
                                onFacebookSignInClicked = {
                                    // Initialize Facebook login
                                    authRepository.initiateLoginWithFacebook(this@MainActivity)
                                }
                            )
                        }
                        composable("register") {
                            RegisterScreen(
                                navController = navController,
                                viewModel = composeAuthViewModel,
                                onGoogleSignInClicked = {
                                    startGoogleSignIn()
                                },
                                onFacebookSignInClicked = {
                                    authRepository.initiateLoginWithFacebook(this@MainActivity)
                                },
                                onSuccessfulRegistration = {
                                    navController.navigate("user_onboarding") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("user_onboarding") {
                            val innerNavController = rememberNavController()
                            UserOnboardingNavGraph(
                                navController = innerNavController,
                                onFinished = {
                                    navController.navigate("main") {
                                        popUpTo("user_onboarding") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("main") {
                            BottomNavRoot(authViewModel = composeAuthViewModel, mainNavController = navController)
                        }
                        composable("forgot_password") {
                            ForgotPasswordScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
    
    private fun setupGoogleSignIn() {
        // Configure Google Sign In with the correct server client ID
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1026435770130-gha0scpj0328af1nnc5cl6ehmq3l40su.apps.googleusercontent.com")
            .requestEmail()
            .build()
            
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    
    private fun startGoogleSignIn() {
        // First, clear the previous signed in account and cached credentials
        googleSignInClient.signOut().addOnCompleteListener {
            // Now sign in with the clean state
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
    }
    
    private fun generateFacebookKeyHash() {
        try {
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KeyHash", "Facebook Key Hash: $keyHash")
                
                // Also display in console for easy copy
                println("=================================")
                println("FACEBOOK KEY HASH: $keyHash")
                println("=================================")
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "Error getting key hash", e)
        }
        
        try {
            // Generate release key hash if you have a custom keystore
            val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            if (info.signingInfo != null) {
                val signatures = info.signingInfo.apkContentsSigners
                signatures.forEach { signature ->
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    Log.d("KeyHash", "Additional Facebook Key Hash: $keyHash")
                    println("ADDITIONAL KEY HASH: $keyHash")
                }
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "Error getting additional key hash", e)
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Handle Facebook login result
        authRepository.onActivityResult(requestCode, resultCode, data)
    }
}
