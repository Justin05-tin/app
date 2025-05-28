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
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.data.repository.AuthRepositoryImpl
import com.example.nammoadidaphat.presentation.ui.auth.ForgotPasswordScreen
import com.example.nammoadidaphat.presentation.ui.auth.LoginScreen
import com.example.nammoadidaphat.presentation.ui.auth.RegisterScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.OnboardingScreen
import com.example.nammoadidaphat.presentation.ui.profile.EditProfileScreen
import com.example.nammoadidaphat.presentation.ui.splash.SplashScreen
import com.example.nammoadidaphat.presentation.viewmodel.AuthState
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.presentation.viewmodel.OnboardingViewModel
import com.example.nammoadidaphat.ui.theme.AppTheme
import com.facebook.FacebookSdk
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.security.MessageDigest
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.nammoadidaphat.presentation.ui.profile.ChangePasswordScreen
import com.example.nammoadidaphat.presentation.ui.profile.GoogleAuthenticatorScreen
import com.example.nammoadidaphat.presentation.ui.profile.HelpScreen
import com.example.nammoadidaphat.presentation.ui.profile.NotificationScreen
import com.example.nammoadidaphat.presentation.ui.profile.SecurityScreen
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
        private const val RC_GOOGLE_SIGN_IN = 9001
        
        // Cập nhật Web Client ID - kiểm tra và đảm bảo đây là ID chính xác từ Firebase Console
        // ID này phải khớp với cấu hình trong Firebase Console dưới dạng "OAuth 2.0 Client ID" hoặc "Web Client ID"
        private const val WEB_CLIENT_ID = "1026435770130-gha0scpj0328af1nnc5cl6ehmq3l40su.apps.googleusercontent.com"
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
            Log.d(TAG, "Google sign-in result received: ${result.resultCode}")
            
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null) {
                    Log.d(TAG, "Google sign-in intent data available")
                    
                    // Xử lý kết quả đăng nhập Google
                    handleGoogleSignInResult(data)
                } else {
                    Log.e(TAG, "Google sign-in result OK but data is null")
                    Toast.makeText(this, "Lỗi xác thực: Không có dữ liệu trả về từ Google", Toast.LENGTH_LONG).show()
                }
            } else {
                Log.e(TAG, "Google sign-in was cancelled or failed with result code: ${result.resultCode}")
                
                // Cung cấp thông tin chi tiết hơn về lỗi
                when (result.resultCode) {
                    RESULT_CANCELED -> {
                        Log.e(TAG, "Google sign-in was explicitly cancelled by the user")
                        Toast.makeText(this, "Đăng nhập đã bị hủy bởi người dùng", Toast.LENGTH_LONG).show()
                        
                        // Gửi thông tin lỗi chi tiết để phân tích
                        val error = result.data?.getStringExtra("error")
                        if (error != null) {
                            Log.e(TAG, "Error details: $error")
                        }
                    }
                    else -> {
                        Toast.makeText(this, "Đăng nhập thất bại với mã: ${result.resultCode}", Toast.LENGTH_LONG).show()
                        
                        // Kiểm tra Internet
                        if (!isNetworkAvailable()) {
                            Log.e(TAG, "Device is not connected to the internet")
                            Toast.makeText(this, "Vui lòng kiểm tra kết nối internet", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                
                // Thử đăng nhập bằng cách khác nếu cách đầu tiên thất bại
                Log.d(TAG, "Trying alternative sign-in approach after failure")
                startAlternativeGoogleSignIn()
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
            AppTheme {
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
                                authViewModel = composeAuthViewModel,
                                onboardingViewModel = onboardingViewModel
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
                        composable("edit_profile") {
                            EditProfileScreen(navController = navController)
                        }
                        
                        composable("notifications") {
                            NotificationScreen(navController = navController)
                        }
                        
                        composable("security") {
                            SecurityScreen(navController = navController)
                        }
                        
                        composable("change_password") {
                            ChangePasswordScreen(navController = navController)
                        }
                        
                        composable("google_authenticator") {
                            GoogleAuthenticatorScreen(navController = navController)
                        }
                        
                        composable("help") {
                            HelpScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
    
    private fun setupGoogleSignIn() {
        try {
            Log.d(TAG, "Setting up Google Sign In with Web Client ID: $WEB_CLIENT_ID")
            
            // Configure Google Sign In với các yêu cầu cụ thể
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .requestProfile() // Yêu cầu thông tin profile
                .build()
                
            googleSignInClient = GoogleSignIn.getClient(this, gso)
            Log.d(TAG, "Google Sign In client initialized successfully")
            
            // Log các thông số cấu hình hiện tại
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                Log.d(TAG, "Already signed in with Google as: ${account.email}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up Google Sign In", e)
        }
    }
    
    private fun startGoogleSignIn() {
        try {
            Log.d(TAG, "Starting Google Sign In process with direct approach")
            
            // Debug thông tin hiện tại
            debugGoogleSignInState()
            
            // Kiểm tra Google Play Services
            val apiAvailability = GoogleApiAvailability.getInstance()
            val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
            
            if (resultCode != ConnectionResult.SUCCESS) {
                if (apiAvailability.isUserResolvableError(resultCode)) {
                    Log.e(TAG, "Google Play Services needs update, error code: $resultCode")
                    apiAvailability.getErrorDialog(this, resultCode, 1001)?.show()
                    return
                } else {
                    Log.e(TAG, "Device doesn't support Google Play Services, error code: $resultCode")
                    Toast.makeText(this, "Thiết bị không hỗ trợ Google Play Services", Toast.LENGTH_LONG).show()
                    return
                }
            }
            
            // PHƯƠNG PHÁP 1: Khởi động đăng nhập Google trực tiếp, không đăng xuất trước
            try {
                val signInIntent = googleSignInClient.signInIntent
                Log.d(TAG, "Launching Google sign-in directly")
                googleSignInLauncher.launch(signInIntent)
                Toast.makeText(this, "Đang mở màn hình đăng nhập Google...", Toast.LENGTH_SHORT).show()
                return
            } catch (e: Exception) {
                Log.e(TAG, "Error with direct Google Sign In approach", e)
                // Nếu thất bại, tiếp tục thử phương pháp 2
            }
            
            // PHƯƠNG PHÁP 2: Đăng xuất trước, sau đó đăng nhập
            Log.d(TAG, "Trying sign-out then sign-in approach")
            googleSignInClient.signOut().addOnCompleteListener {
                try {
                    // Tạo lại GoogleSignInClient sau khi đăng xuất để đảm bảo trạng thái sạch
                    setupGoogleSignIn()
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                    Log.d(TAG, "Launched Google sign-in after sign out")
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching Google Sign In after sign out", e)
                    Toast.makeText(this, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Critical error in Google Sign In process", e)
            Toast.makeText(this, "Lỗi đăng nhập: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun debugGoogleSignInState() {
        try {
            Log.d(TAG, "====== GOOGLE SIGN IN DEBUG INFO ======")
            
            // Kiểm tra Web Client ID
            Log.d(TAG, "Web Client ID: $WEB_CLIENT_ID")
            
            // Kiểm tra tài khoản Google hiện tại
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                Log.d(TAG, "Current Google account: ${account.email}")
                Log.d(TAG, "ID token available: ${account.idToken != null}")
                Log.d(TAG, "Account granted scopes: ${account.grantedScopes?.joinToString()}")
                Log.d(TAG, "Account ID: ${account.id}")
                Log.d(TAG, "Account server auth code available: ${account.serverAuthCode != null}")
            } else {
                Log.d(TAG, "No Google account currently signed in")
            }
            
            // Kiểm tra Firebase project ID để xác nhận đúng dự án
            try {
                val appContext = applicationContext
                val applicationInfo = appContext.packageManager.getApplicationInfo(
                    appContext.packageName, PackageManager.GET_META_DATA)
                val firebaseAppId = applicationInfo.metaData?.getString("firebase_database_url")
                val googleServicesJson = applicationInfo.metaData?.getString("google_app_id")
                
                Log.d(TAG, "Firebase DB URL: $firebaseAppId")
                Log.d(TAG, "Google App ID: $googleServicesJson")
                Log.d(TAG, "Package Name: ${appContext.packageName}")
            } catch (e: Exception) {
                Log.e(TAG, "Error retrieving Firebase configuration", e)
            }
            
            // In ra SHA-1 fingerprint để kiểm tra với Firebase Console
            try {
                val info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                if (info.signingInfo != null) {
                    val signatures = info.signingInfo.apkContentsSigners
                    if (signatures.isNotEmpty()) {
                        val md = MessageDigest.getInstance("SHA-1")
                        md.update(signatures[0].toByteArray())
                        val sha1 = bytesToHex(md.digest())
                        Log.d(TAG, "SHA-1 certificate fingerprint: $sha1")
                        Log.d(TAG, "SHA-1 certificate fingerprint with colons: ${sha1.chunked(2).joinToString(":")}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get SHA-1 fingerprint", e)
            }
            
            // Kiểm tra FirebaseAuth
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                Log.d(TAG, "Firebase user signed in: ${currentUser.email}")
                Log.d(TAG, "Firebase user providers: ${currentUser.providerData.joinToString { it.providerId }}")
                Log.d(TAG, "Firebase user ID: ${currentUser.uid}")
            } else {
                Log.d(TAG, "No Firebase user signed in")
            }
            
            // Kiểm tra Google Play Services
            try {
                val googleApiAvailability = GoogleApiAvailability.getInstance()
                val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
                if (resultCode == ConnectionResult.SUCCESS) {
                    Log.d(TAG, "Google Play Services is available and up to date")
                } else {
                    Log.e(TAG, "Google Play Services is NOT available or needs update. Status code: $resultCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking Google Play Services", e)
            }
            
            Log.d(TAG, "====== END DEBUG INFO ======")
        } catch (e: Exception) {
            Log.e(TAG, "Error debugging Google Sign In state", e)
        }
    }
    
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = CharArray(bytes.size * 2)
        for (i in bytes.indices) {
            val v = bytes[i].toInt() and 0xff
            hexChars[i * 2] = "0123456789abcdef"[v shr 4]
            hexChars[i * 2 + 1] = "0123456789abcdef"[v and 0xf]
        }
        return String(hexChars)
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
    
    private fun handleGoogleSignInResult(data: Intent) {
        try {
            // Set loading state to true in the UI
            authViewModel.handleGoogleSignInIntent(data)
            
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                Log.d(TAG, "Google sign in successful: ${account?.email}")
                
                if (account != null && account.idToken != null) {
                    Log.d(TAG, "ID Token available: ${account.idToken?.take(10)}...")
                    
                    // Forward the intent to the view model for Firebase authentication
                    lifecycleScope.launch {
                        val authResult = authViewModel.handleGoogleSignInResult(data)
                        if (authResult.isSuccess) {
                            val user = authResult.getOrNull()
                            Log.d(TAG, "Firebase authentication successful: ${user?.email}")
                            
                            handleSuccessfulSignIn(user)
                        } else {
                            val exception = authResult.exceptionOrNull()
                            Log.e(TAG, "Firebase authentication failed", exception)
                            
                            // Hiển thị thông báo lỗi chi tiết
                            val errorMessage = when (exception) {
                                is FirebaseAuthInvalidCredentialsException -> "Thông tin đăng nhập không hợp lệ"
                                is FirebaseAuthInvalidUserException -> "Tài khoản không tồn tại hoặc đã bị vô hiệu hóa"
                                is FirebaseAuthUserCollisionException -> "Tài khoản email này đã được liên kết với tài khoản khác"
                                is IOException -> "Lỗi kết nối mạng, vui lòng kiểm tra kết nối internet"
                                else -> exception?.message ?: "Lỗi không xác định"
                            }
                            
                            Toast.makeText(this@MainActivity, 
                                "Đăng nhập thất bại: $errorMessage",
                                Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Log.e(TAG, "Google account or ID token is null")
                    Toast.makeText(this@MainActivity, "Đăng nhập Google thất bại: Không nhận được token", Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                // Google Sign In failed with API exception
                val statusCode = e.statusCode
                Log.e(TAG, "Google sign in API exception: ${statusCode}", e)
                
                val errorMessage = when (statusCode) {
                    12500 -> "Lỗi máy chủ Google Play (12500)"
                    12501 -> "Người dùng đã hủy đăng nhập (12501)"
                    7 -> "Lỗi kết nối mạng (7)"
                    16 -> "Lỗi API không khả dụng (16)"
                    8 -> "Lỗi kết nối bị gián đoạn (8)"
                    10 -> "Lỗi ứng dụng không được cài đặt (10)"
                    14 -> "Lỗi thời gian chờ kết nối (14)"
                    else -> "Mã lỗi: ${statusCode}"
                }
                
                Toast.makeText(this@MainActivity, 
                    "Đăng nhập Google thất bại: ${errorMessage}", 
                    Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            // General exception during Google Sign In
            Log.e(TAG, "Google sign in general error", e)
            Toast.makeText(this@MainActivity, 
                "Lỗi đăng nhập Google: ${e.message}", 
                Toast.LENGTH_LONG).show()
        }
    }
    
    private fun handleSuccessfulSignIn(user: com.example.nammoadidaphat.domain.model.User?) {
        // Kiểm tra user và xác định điều hướng tiếp theo
        if (user != null) {
            Log.d(TAG, "User successfully signed in: ${user.email}")
            
            // Kiểm tra xem cần onboarding hay không
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
            
            Toast.makeText(this@MainActivity, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
        } else {
            Log.e(TAG, "User is null after successful sign in")
            Toast.makeText(this@MainActivity, "Lỗi: Không lấy được thông tin người dùng", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun startAlternativeGoogleSignIn() {
        try {
            Log.d(TAG, "Starting alternative Google Sign In approach")
            
            // Tạo GSO hoàn toàn mới với các thiết lập khác
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(WEB_CLIENT_ID)
                .requestEmail()
                .build()
            
            // Tạo client mới 
            val tempClient = GoogleSignIn.getClient(this, gso)
            
            // Đăng xuất và sau đó đăng nhập lại
            tempClient.signOut().addOnCompleteListener {
                try {
                    // Lấy intent đăng nhập mới
                    val signInIntent = tempClient.signInIntent.apply {
                        // Thiết lập flags để đảm bảo dialog hiển thị đúng cách
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    
                    // Bắt đầu quá trình đăng nhập
                    googleSignInLauncher.launch(signInIntent)
                    Log.d(TAG, "Alternative Google sign-in approach launched")
                    Toast.makeText(this, "Đang thử phương pháp đăng nhập thay thế...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "Error in alternative Google sign-in approach", e)
                    Toast.makeText(this, "Lỗi đăng nhập: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing alternative sign-in", e)
            Toast.makeText(this, "Không thể khởi tạo đăng nhập: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    // Kiểm tra kết nối internet sử dụng API mới nhất
    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val network = cm.activeNetwork
            val capabilities = cm.getNetworkCapabilities(network)
            capabilities != null && 
                    (capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) || 
                     capabilities.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR))
        } else {
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo?.isConnectedOrConnecting == true
        }
    }
}
