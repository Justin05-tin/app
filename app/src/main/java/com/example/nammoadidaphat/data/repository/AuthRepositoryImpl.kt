package com.example.nammoadidaphat.data.repository

import android.content.Context
import android.content.Intent
import android.app.Activity
import android.content.SharedPreferences
import androidx.activity.result.ActivityResultLauncher
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.domain.repository.AuthRepository
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CancellationException
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration.Companion.milliseconds

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : AuthRepository {

    // Implement the auth property from interface
    override val auth: FirebaseAuth
        get() = this.firebaseAuth

    private val usersCollection = firestore.collection("users")
    
    // Web Client ID cho Google Sign-In
    private val webClientId = "1026435770130-gha0scpj0328af1nnc5cl6ehmq3l40su.apps.googleusercontent.com"

    // SharedPreferences to cache user auth state
    private val sharedPrefs: SharedPreferences by lazy {
        context.getSharedPreferences("auth_cache", Context.MODE_PRIVATE)
    }
    
    // Gson for serializing/deserializing user object
    private val gson: Gson by lazy {
        Gson()
    }
    
    // Google sign in
    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
        
        Timber.d("Using Google Sign-In web client ID: $webClientId")
        
        GoogleSignIn.getClient(context, gso)
    }
    
    // Facebook sign in
    private val callbackManager: CallbackManager by lazy {
        CallbackManager.Factory.create()
    }

    // Save the user to local cache
    private fun saveUserToCache(user: User?) {
        if (user != null) {
            val userJson = gson.toJson(user)
            sharedPrefs.edit().putString("cached_user", userJson).apply()
            sharedPrefs.edit().putBoolean("is_authenticated", true).apply()
        } else {
            sharedPrefs.edit().remove("cached_user").apply()
            sharedPrefs.edit().putBoolean("is_authenticated", false).apply()
        }
    }

    // Get the user from local cache
    private fun getUserFromCache(): User? {
        val userJson = sharedPrefs.getString("cached_user", null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error parsing cached user")
            null
        }
    }

    // Check if user is authenticated based on cache
    private fun isAuthenticatedFromCache(): Boolean {
        return sharedPrefs.getBoolean("is_authenticated", false)
    }
    
    // Constant for default avatar URL
    private val DEFAULT_AVATAR_URL = "https://img.freepik.com/free-vector/blue-circle-with-white-user_78370-4707.jpg?semt=ais_hybrid&w=740"
    
    // Helper function to check and set default avatar if empty
    private suspend fun checkAndSetDefaultAvatar(userId: String, currentAvatar: String?): String {
        // If avatar is null or empty, set the default avatar
        if (currentAvatar.isNullOrBlank()) {
            Timber.d("Setting default avatar for user $userId")
            try {
                // Update the avatar in Firestore
                usersCollection.document(userId).update("avatar", DEFAULT_AVATAR_URL).await()
                return DEFAULT_AVATAR_URL
            } catch (e: Exception) {
                Timber.e(e, "Failed to update default avatar in Firestore: ${e.message}")
                // Return default avatar even if update fails
                return DEFAULT_AVATAR_URL
            }
        }
        return currentAvatar
    }

    override suspend fun signIn(email: String, password: String): Result<User> = try {
        // Auth sign in with timeout
        val result = withTimeout(10000.milliseconds) { // 10 second timeout
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        }
        
        val user = result.user
        if (user != null) {
            // Update last login timestamp
            val timestamp = Timestamp.now()
            
            // Update the user's document in Firestore with timeout
            try {
                withTimeout(5000.milliseconds) { // 5 second timeout
                    val userDoc = usersCollection.document(user.uid).get().await()
                    if (userDoc.exists()) {
                        usersCollection.document(user.uid).update("updatedAt", timestamp).await()
                    } else {
                        // Create a basic user document if it doesn't exist
                        val basicUserData = User(
                            id = user.uid,
                            email = email,
                            displayName = user.displayName ?: email.substringBefore('@'),
                            avatar = user.photoUrl?.toString() ?: "",
                            gender = "",
                            fitnessLevel = "beginner",  // Provide default values
                            goals = listOf("Get fit"),  // Provide default values
                            createdAt = timestamp,
                            updatedAt = timestamp,
                            authProvider = "password"
                        )
                        usersCollection.document(user.uid).set(basicUserData.toMap()).await()
                    }
                }
            } catch (e: Exception) {
                // Log but don't fail the login if Firestore update fails
                Timber.e(e, "Error updating Firestore after login: ${e.message}")
            }
            
            // Get user data from cache first, then update from Firestore asynchronously
            val cachedUser = getUserFromCache()
            if (cachedUser != null && cachedUser.id == user.uid) {
                // If we have cached user data, return it immediately
                Result.success(cachedUser)
            } else {
                // Otherwise get from Firestore with timeout
                try {
                    val userData = withTimeout(5000.milliseconds) {
                        getUserFromFirestore(user.uid)
                    }
                    
                    // Save to cache
                    saveUserToCache(userData)
                    
                    Result.success(userData)
                } catch (e: Exception) {
                    // If Firestore fails, create a minimal user from auth data
                    Timber.e(e, "Error fetching user data from Firestore: ${e.message}")
                    val basicUser = User.createMinimalUser(
                        userId = user.uid,
                        email = email,
                        authProvider = "password"
                    )
                    saveUserToCache(basicUser)
                    
                    // Return success with basic user rather than failing the login
                    Result.success(basicUser)
                }
            }
        } else {
            Result.failure(Exception("Authentication failed"))
        }
    } catch (e: Exception) {
        Timber.e(e, "SignIn error: ${e.message}")
        // Provide more specific error messages for common login issues
        val errorMessage = when {
            e.message?.contains("password is invalid") == true -> 
                "The password is incorrect. Please try again."
            e.message?.contains("no user record") == true -> 
                "No account found with this email. Please check your email or sign up."
            e.message?.contains("blocked all requests") == true || 
            e.message?.contains("network error") == true ->
                "Network connection issue. Please check your internet connection."
            e.message?.contains("timeout") == true ->
                "Request timed out. Please try again."
            else -> e.message ?: "Authentication failed"
        }
        
        Result.failure(Exception(errorMessage))
    }

    override suspend fun signUp(
        email: String,
        password: String,
        fullName: String
    ): Result<User> = try {
        // Create auth account
        Timber.d("Starting signUp with email: $email, fullName: $fullName")
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null) {
            val timestamp = Timestamp.now()
            
            Timber.d("User created in Firebase Auth, now creating Firestore record with ID: ${user.uid}")
            
            val userData = User(
                id = user.uid,
                email = email,
                displayName = if (fullName.isNotBlank()) fullName else email.substringBefore('@'),
                avatar = DEFAULT_AVATAR_URL, // Sử dụng avatar mặc định
                gender = "",
                fitnessLevel = "beginner",  // Provide default values
                goals = listOf("Get fit"),  // Provide default values
                createdAt = timestamp,
                updatedAt = timestamp,
                authProvider = "password"
            )
            
            // Log user data before saving to Firestore
            Timber.d("User data being saved to Firestore: $userData")
            Timber.d("Avatar URL being saved: ${userData.avatar}")
            
            // Save to Firestore
            try {
                usersCollection.document(user.uid).set(userData.toMap()).await()
                Timber.d("User data successfully saved to Firestore")
                
                // Verify the data was saved correctly by reading it back
                val savedUserDoc = usersCollection.document(user.uid).get().await()
                if (savedUserDoc.exists()) {
                    val savedAvatar = savedUserDoc.getString("avatar")
                    Timber.d("Verified saved user data - Avatar: $savedAvatar")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error saving user data to Firestore: ${e.message}")
            }
            
            Result.success(userData)
        } else {
            Timber.e("Failed to create user in Firebase Auth")
            Result.failure(Exception("Failed to create user"))
        }
    } catch (e: Exception) {
        Timber.e(e, "SignUp error: ${e.message}")
        Result.failure(e)
    }

    override suspend fun signOut() {
        // Clear cache first for immediate UI response
        saveUserToCache(null)
        
        firebaseAuth.signOut()
        
        // Sign out from Google
        try {
            googleSignInClient.signOut().await()
        } catch (e: Exception) {
            Timber.e(e, "Error signing out from Google")
        }
        
        // Sign out from Facebook
        try {
            LoginManager.getInstance().logOut()
        } catch (e: Exception) {
            Timber.e(e, "Error signing out from Facebook")
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> = try {
        firebaseAuth.sendPasswordResetEmail(email).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun updateUserProfile(user: User): Result<Unit> = try {
        val updates = user.toMap().toMutableMap()
        updates["updatedAt"] = Timestamp.now()
        
        usersCollection.document(user.id).update(updates).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    override suspend fun updateUserAvatar(userId: String, avatarUrl: String): Result<Unit> = try {
        val updates = mapOf(
            "avatar" to avatarUrl,
            "updatedAt" to Timestamp.now()
        )
        
        usersCollection.document(userId).update(updates).await()
        
        // Update cached user
        val cachedUser = getUserFromCache()
        if (cachedUser != null && cachedUser.id == userId) {
            val updatedUser = cachedUser.copy(avatar = avatarUrl, updatedAt = Timestamp.now())
            saveUserToCache(updatedUser)
        }
        
        Result.success(Unit)
    } catch (e: Exception) {
        Timber.e(e, "Failed to update user avatar")
        Result.failure(e)
    }
    
    override suspend fun getUserById(userId: String): Result<User> = try {
        val user = getUserFromFirestore(userId)
        Result.success(user)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        Timber.d("getCurrentUser flow started")
        
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            Timber.d("Auth state changed: user=${firebaseUser?.email}, uid=${firebaseUser?.uid}")
            
            if (firebaseUser == null) {
                Timber.d("No authenticated user, clearing cache")
                saveUserToCache(null)
                trySend(null)
            } else {
                Timber.d("Authenticated user found: ${firebaseUser.email}, fetching from Firestore")
                
                // Fetch user data from Firestore with proper error handling
                usersCollection.document(firebaseUser.uid).get()
                    .addOnSuccessListener { document ->
                        try {
                            val userData = if (document != null && document.exists()) {
                                Timber.d("User document found in Firestore")
                                // User exists in Firestore, parse the data
                                val user = User.fromMap(document.data ?: emptyMap())
                                // Ensure consistent data
                                user.copy(
                                    id = firebaseUser.uid,
                                    email = firebaseUser.email ?: user.email,
                                    authProvider = if (user.authProvider.isEmpty()) {
                                        firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                                    } else user.authProvider
                                )
                            } else {
                                Timber.d("User document not found, creating default user")
                                // User doesn't exist in Firestore, create basic user
                                val timestamp = Timestamp.now()
                                val defaultUser = User(
                                    id = firebaseUser.uid,
                                    email = firebaseUser.email ?: "",
                                    displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore('@') ?: "",
                                    avatar = firebaseUser.photoUrl?.toString() ?: "",
                                    gender = "Not specified",
                                    fitnessLevel = "beginner",
                                    goals = listOf("Get fit"),
                                    createdAt = timestamp,
                                    updatedAt = timestamp,
                                    authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                                )
                                
                                // Save to Firestore for future use
                                usersCollection.document(firebaseUser.uid)
                                    .set(defaultUser.toMap())
                                    .addOnFailureListener { e ->
                                        Timber.e(e, "Failed to create user document in Firestore")
                                    }
                                    
                                defaultUser
                            }
                            
                            Timber.d("Emitting user data: ${userData.email}, provider: ${userData.authProvider}")
                            saveUserToCache(userData)
                            trySend(userData)
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing user data")
                            // Create minimal user as fallback
                            val fallbackUser = User.createMinimalUser(
                                userId = firebaseUser.uid,
                                email = firebaseUser.email ?: "",
                                authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                            )
                            saveUserToCache(fallbackUser)
                            trySend(fallbackUser)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Timber.e(exception, "Failed to get user document")
                        // Create minimal user as fallback
                        val fallbackUser = User.createMinimalUser(
                            userId = firebaseUser.uid,
                            email = firebaseUser.email ?: "",
                            authProvider = firebaseUser.providerData.firstOrNull()?.providerId ?: "password"
                        )
                        saveUserToCache(fallbackUser)
                        trySend(fallbackUser)
                    }
            }
        }
        
        // Register the auth state listener
        firebaseAuth.addAuthStateListener(listener)
        
        // Clean up when the flow is closed
        awaitClose { 
            Timber.d("getCurrentUser flow closed")
            firebaseAuth.removeAuthStateListener(listener) 
        }
    }
    
    override fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }
    
    override suspend fun handleGoogleSignInResult(data: Intent?): Result<User> {
        if (data == null) {
            return Result.failure(Exception("Sign-in intent data is null"))
        }
        
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            
            if (account != null) {
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                val user = signInWithCredential(credential)
                
                // Create or update user profile
                val userData = createOrUpdateSocialUserProfile(
                    firebaseUser = user,
                    providerId = "google.com",
                    displayName = account.displayName ?: "",
                    email = account.email ?: "",
                    photoUrl = account.photoUrl?.toString() ?: ""
                )
                
                Result.success(userData)
            } else {
                Result.failure(Exception("Google sign in failed"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Google sign in error")
            Result.failure(e)
        }
    }
    
    override fun getFacebookSignInIntent(): Intent {
        // This is a dummy intent as Facebook uses its own mechanism
        return Intent()
    }
    
    // Function to initiate Facebook login - separate from the intent getter
    fun initiateLoginWithFacebook(activity: Activity) {
        LoginManager.getInstance().logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }
    
    override suspend fun handleFacebookSignInResult(data: Intent?): Result<User> {
        return suspendCoroutine { continuation ->
            try {
                LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        // This runs on the main thread, so we need to handle it carefully
                        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        
                        // Use a simple coroutine scope instead of lifecycleScope
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val firebaseUser = signInWithCredential(credential)
                                
                                // Get user data from the token
                                val userData = createOrUpdateSocialUserProfile(
                                    firebaseUser = firebaseUser,
                                    providerId = "facebook.com",
                                    displayName = firebaseUser.displayName ?: "",
                                    email = firebaseUser.email ?: "",
                                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                                )
                                
                                continuation.resume(Result.success(userData))
                            } catch (e: Exception) {
                                Timber.e(e, "Facebook Firebase auth error")
                                continuation.resume(Result.failure(e))
                            }
                        }
                    }
                    
                    override fun onCancel() {
                        Timber.d("Facebook login cancelled")
                        continuation.resume(Result.failure(Exception("Facebook login was cancelled")))
                    }
                    
                    override fun onError(error: FacebookException) {
                        Timber.e(error, "Facebook login error")
                        continuation.resume(Result.failure(error))
                    }
                })
                
                callbackManager.onActivityResult(
                    0, // These parameters will be replaced by the actual values
                    0, // from the activity result
                    data
                )
                
            } catch (e: Exception) {
                Timber.e(e, "Facebook login general error")
                continuation.resume(Result.failure(e))
            }
        }
    }

    // Helper to sign in with credential
    private suspend fun signInWithCredential(credential: AuthCredential): FirebaseUser {
        val result = firebaseAuth.signInWithCredential(credential).await()
        return result.user ?: throw Exception("Failed to sign in with credential")
    }
    
    // Helper to create or update user profile for social logins
    private suspend fun createOrUpdateSocialUserProfile(
        firebaseUser: FirebaseUser,
        providerId: String,
        displayName: String,
        email: String,
        photoUrl: String
    ): User {
        val timestamp = Timestamp.now()
        
        // Kiểm tra và sử dụng avatar mặc định nếu cần
        val safeAvatarUrl = if (photoUrl.isBlank()) DEFAULT_AVATAR_URL else photoUrl
        
        // Check if user exists
        val userDoc = usersCollection.document(firebaseUser.uid).get().await()
        
        return if (userDoc.exists()) {
            // Update existing user
            val updates = mapOf(
                "updatedAt" to timestamp,
                "email" to email,
                "avatar" to safeAvatarUrl // Sử dụng avatar đã kiểm tra
            )
            
            usersCollection.document(firebaseUser.uid).update(updates).await()
            
            // Get updated user data
            getUserFromFirestore(firebaseUser.uid)
        } else {
            // Create new user
            val userData = User(
                id = firebaseUser.uid,
                email = email,
                displayName = displayName,
                avatar = safeAvatarUrl, // Sử dụng avatar đã kiểm tra
                createdAt = timestamp,
                updatedAt = timestamp,
                authProvider = providerId
            )
            
            usersCollection.document(firebaseUser.uid).set(userData.toMap()).await()
            userData
        }
    }

    private suspend fun getUserFromFirestore(userId: String): User {
        return suspendCoroutine { continuation ->
            try {
                // Set a timeout using a Handler
                val timeoutHandler = android.os.Handler(android.os.Looper.getMainLooper())
                val timeoutRunnable = Runnable { 
                    // If timeout occurs, return minimal user
                    val currentUser = firebaseAuth.currentUser
                    // Use default avatar for timeout case
                    CoroutineScope(Dispatchers.IO).launch {
                        val avatar = checkAndSetDefaultAvatar(userId, currentUser?.photoUrl?.toString() ?: "")
                        val basicUser = User(
                            id = userId,
                            email = currentUser?.email ?: "",
                            displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "",
                            avatar = avatar,
                            gender = "Not specified",
                            fitnessLevel = "beginner", 
                            goals = listOf("Get fit"),
                            authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                        )
                        continuation.resume(basicUser)
                    }
                }
                
                // Set 5 second timeout
                timeoutHandler.postDelayed(timeoutRunnable, 5000)
                
                usersCollection.document(userId).get()
                    .addOnSuccessListener { document ->
                        // Cancel timeout
                        timeoutHandler.removeCallbacks(timeoutRunnable)
                        
                        try {
                            val currentUser = firebaseAuth.currentUser
                            if (document != null && document.exists()) {
                                // Get user data
                                CoroutineScope(Dispatchers.IO).launch {
                                    val userData = try {
                                        // Create a safe map with defaults first
                                        val safeMap = mutableMapOf<String, Any?>(
                                            "id" to userId,
                                            "email" to (currentUser?.email ?: ""),
                                            "displayName" to (currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: ""),
                                            "authProvider" to (currentUser?.providerData?.firstOrNull()?.providerId ?: "password"),
                                            "gender" to "Not specified",
                                            "fitnessLevel" to "beginner",
                                            "goals" to listOf("Get fit")
                                        )
                                        
                                        // Safely add all document data
                                        document.data?.forEach { (key, value) ->
                                            safeMap[key] = value
                                        }
                                        
                                        val user = User.fromMap(safeMap)
                                        
                                        // Check and set default avatar if needed
                                        val avatarUrl = checkAndSetDefaultAvatar(userId, user.avatar)
                                        
                                        // Create a safe copy with reasonable defaults for any missing fields
                                        User(
                                            id = user.id,
                                            email = user.email,
                                            displayName = user.displayName.ifEmpty { user.email.substringBefore('@') },
                                            avatar = avatarUrl,
                                            age = user.age,
                                            gender = user.gender.ifEmpty { "Not specified" },
                                            height = user.height,
                                            weight = user.weight,
                                            fitnessLevel = user.fitnessLevel.ifEmpty { "beginner" },
                                            goals = user.goals.ifEmpty { listOf("Get fit") },
                                            preferences = user.preferences,
                                            createdAt = user.createdAt,
                                            updatedAt = user.updatedAt,
                                            authProvider = user.authProvider
                                        )
                                    } catch (e: Exception) {
                                        Timber.e(e, "Error parsing user data in getUserFromFirestore: ${e.message}")
                                        // Return minimal user to prevent crashes
                                        val avatar = checkAndSetDefaultAvatar(userId, currentUser?.photoUrl?.toString() ?: "")
                                        User(
                                            id = userId,
                                            email = currentUser?.email ?: "",
                                            displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "",
                                            avatar = avatar,
                                            gender = "Not specified",
                                            fitnessLevel = "beginner",
                                            goals = listOf("Get fit"),
                                            authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                                        )
                                    }
                                    continuation.resume(userData)
                                }
                            } else {
                                // If user exists in Auth but not in Firestore, create a basic record
                                CoroutineScope(Dispatchers.IO).launch {
                                    val timestamp = Timestamp.now()
                                    
                                    // Check and set default avatar
                                    val avatar = checkAndSetDefaultAvatar(userId, currentUser?.photoUrl?.toString() ?: "")
                                    
                                    val userData = User(
                                        id = userId,
                                        email = currentUser?.email ?: "",
                                        displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "",
                                        avatar = avatar,
                                        gender = "Not specified",
                                        fitnessLevel = "beginner",
                                        goals = listOf("Get fit"),
                                        createdAt = timestamp,
                                        updatedAt = timestamp,
                                        authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                                    )
                                    
                                    // Create the user in Firestore
                                    usersCollection.document(userId).set(userData.toMap())
                                        .addOnSuccessListener {
                                            continuation.resume(userData)
                                        }
                                        .addOnFailureListener { error ->
                                            Timber.e(error, "Failed to create user in Firestore")
                                            // Return basic user data instead of failing
                                            continuation.resume(userData)
                                        }
                                }
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "Error processing user data in getUserFromFirestore: ${e.message}")
                            // Return basic user data instead of failing
                            CoroutineScope(Dispatchers.IO).launch {
                                val currentUser = firebaseAuth.currentUser
                                val avatar = checkAndSetDefaultAvatar(userId, currentUser?.photoUrl?.toString() ?: "")
                                val basicUser = User(
                                    id = userId,
                                    email = currentUser?.email ?: "",
                                    displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "",
                                    avatar = avatar,
                                    gender = "Not specified", 
                                    fitnessLevel = "beginner",
                                    goals = listOf("Get fit"),
                                    authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                                )
                                continuation.resume(basicUser)
                            }
                        }
                    }
                    .addOnFailureListener { exception ->
                        // Cancel timeout
                        timeoutHandler.removeCallbacks(timeoutRunnable)
                        
                        Timber.e(exception, "Failed to get user document: ${exception.message}")
                        // Return basic user data instead of failing
                        CoroutineScope(Dispatchers.IO).launch {
                            val currentUser = firebaseAuth.currentUser
                            val avatar = checkAndSetDefaultAvatar(userId, currentUser?.photoUrl?.toString() ?: "")
                            val basicUser = User(
                                id = userId,
                                email = currentUser?.email ?: "",
                                displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "",
                                avatar = avatar,
                                gender = "Not specified",
                                fitnessLevel = "beginner",
                                goals = listOf("Get fit"),
                                authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                            )
                            continuation.resume(basicUser)
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Unexpected error in getUserFromFirestore: ${e.message}")
                // Return basic user data instead of failing
                CoroutineScope(Dispatchers.IO).launch {
                    val currentUser = firebaseAuth.currentUser
                    val avatar = checkAndSetDefaultAvatar(userId, currentUser?.photoUrl?.toString() ?: "")
                    val basicUser = User(
                        id = userId,
                        email = currentUser?.email ?: "",
                        displayName = currentUser?.displayName ?: currentUser?.email?.substringBefore('@') ?: "",
                        avatar = avatar,
                        gender = "Not specified",
                        fitnessLevel = "beginner",
                        goals = listOf("Get fit"),
                        authProvider = currentUser?.providerData?.firstOrNull()?.providerId ?: "password"
                    )
                    continuation.resume(basicUser)
                }
            }
        }
    }
    
    // Handle activity result from Facebook login
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
} 