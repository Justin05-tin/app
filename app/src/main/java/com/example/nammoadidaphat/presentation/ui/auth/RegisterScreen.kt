package com.example.nammoadidaphat.presentation.ui.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel(),
    onSuccessfulRegistration: () -> Unit = {
        navController.navigate("login") {
            popUpTo("register") { inclusive = true }
        }
    },
    onGoogleSignInClicked: () -> Unit = {},
    onFacebookSignInClicked: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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
            Spacer(modifier = Modifier.height(64.dp))
            
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
                
                // Confirm Password field
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("Confirm Password", color = Color.White.copy(alpha = 0.6f)) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = "Confirm Password", tint = Color.White)
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
                
                // Register Button
                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Please fill in all fields"
                            return@Button
                        }
                        
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        
                        isLoading = true
                        errorMessage = ""
                        
                        scope.launch {
                            viewModel.signUp(email, password, "")
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
                            text = "Register",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                // Social Login Section
                Column(
                    modifier = Modifier.padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Or register with",
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Facebook button
                        Button(
                            onClick = { onFacebookSignInClicked() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color(0xFF1877F2), // Facebook blue
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
                        
                        // Google button
                        Button(
                            onClick = { onGoogleSignInClicked() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = Color.White,
                                contentColor = Color.Black
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
                }
                
                // Already have account text and login link
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 24.dp)
                ) {
                    Text(
                        "Already have an account? ",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    
                    ClickableText(
                        text = AnnotatedString("Login here"),
                        style = TextStyle(
                            color = Color(0xFFFFEB3B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        onClick = {
                            navController.navigate("login") {
                                popUpTo("register") { inclusive = true }
                            }
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
