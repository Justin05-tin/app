package com.example.nammoadidaphat.presentation.ui.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.facebook.CallbackManager
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccessfulRegistration: () -> Unit = {
        navController.navigate("login") {
            popUpTo("register") { inclusive = true }
        }
    }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Google Sign-In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isLoading = true
            errorMessage = ""
            
            scope.launch {
                viewModel.handleGoogleSignInResult(result.data)
                    .onSuccess {
                        isLoading = false
                        Toast.makeText(context, "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                        onSuccessfulRegistration()
                    }
                    .onFailure { exception ->
                        isLoading = false
                        errorMessage = exception.message ?: "Google sign-in failed"
                    }
            }
        }
    }
    
    // Facebook Sign-In Handler
    LaunchedEffect(Unit) {
        // Register Facebook SDK in the Activity
        // This is necessary for Facebook SDK to work
        // In a real app, you would do this in your MainActivity
    }
    
    fun handleFacebookSignIn() {
        isLoading = true
        errorMessage = ""
        
        viewModel.getFacebookSignInIntent() // This triggers Facebook login flow
        
        // Actual result handling will be done via Facebook SDK callbacks
        // which will eventually call our ViewModel's handleFacebookSignInResult method
        
        scope.launch {
            try {
                // This is a simplified example - in a real app, 
                // you need to properly handle the activity result
                val result = viewModel.handleFacebookSignInResult(null)
                result.onSuccess {
                    isLoading = false
                    Toast.makeText(context, "Facebook sign-in successful!", Toast.LENGTH_SHORT).show()
                    onSuccessfulRegistration()
                }.onFailure { exception ->
                    isLoading = false
                    errorMessage = exception.message ?: "Facebook sign-in failed"
                }
            } catch (e: Exception) {
                isLoading = false
                errorMessage = e.message ?: "Facebook sign-in failed"
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image with overlay
        Image(
            painter = painterResource(id = R.drawable.register_background),
            contentDescription = "Workout background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66000000))  // Semi-transparent black overlay
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(96.dp))
            
            // Title
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "BODY GOALS",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = "WORKOUT",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            
            // Registration Form
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 32.dp)
            ) {
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = "Email", tint = Color.White)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        backgroundColor = Color.White.copy(alpha = 0.1f)
                    )
                )

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color.White)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        backgroundColor = Color.White.copy(alpha = 0.1f)
                    )
                )
                
                // Confirmation Password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirmation password", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Confirmation Password", tint = Color.White)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                        backgroundColor = Color.White.copy(alpha = 0.1f)
                    )
                )

                // Error message
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Sign Up Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Please fill all fields"
                        } else if (password != confirmPassword) {
                            errorMessage = "Passwords don't match"
                        } else {
                            isLoading = true
                            errorMessage = ""
                            
                            scope.launch {
                                viewModel.signUp(
                                    email = email,
                                    password = password,
                                    fullName = email.substringBefore("@"),
                                    age = null,
                                    gender = "",
                                    height = null,
                                    weight = null,
                                    fitnessLevel = "",
                                    goals = ""
                                )
                                .onSuccess {
                                    isLoading = false
                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                    onSuccessfulRegistration()
                                }
                                .onFailure { exception ->
                                    isLoading = false
                                    errorMessage = exception.message ?: "Registration failed"
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFFFFEB3B) // Yellow color
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Sign Up",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // "Or" divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Or",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.weight(1f)
                    )
                }
                
                // Social login buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Facebook sign in button
                    Button(
                        onClick = { handleFacebookSignIn() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF1877F2),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_facebook),
                                contentDescription = "Facebook",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Facebook",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // Google sign in button
                    Button(
                        onClick = {
                            googleSignInLauncher.launch(viewModel.getGoogleSignInIntent())
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color.White,
                            contentColor = Color.DarkGray
                        ),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isLoading
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_google),
                                contentDescription = "Google",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Google",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                // Already have account text and login link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        "If you have an account? ",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    
                    ClickableText(
                        text = AnnotatedString("Sign In here"),
                        style = TextStyle(
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        onClick = {
                            navController.navigate("login")
                        }
                    )
                }
            }
            
            // Page indicator dots at the bottom
            Row(
                modifier = Modifier.padding(top = 32.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            }
        }
    }
}
