package com.example.nammoadidaphat.presentation.ui.auth

import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Define error handler for coroutines
    val errorHandler = CoroutineExceptionHandler { _, exception ->
        Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
    }

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessLevel by remember { mutableStateOf("") }
    var goals by remember { mutableStateOf("") }
    
    var successMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Dropdown options
    val genderOptions = listOf("Male", "Female", "Other")
    var genderExpanded by remember { mutableStateOf(false) }
    
    val fitnessLevelOptions = listOf("Beginner", "Intermediate", "Advanced", "Expert")
    var fitnessLevelExpanded by remember { mutableStateOf(false) }

    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Black,
                        Color(0xFF424242)
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Sign Up",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Full Name", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date of Birth field
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { dateOfBirth = it },
                placeholder = { Text("Date of Birth (DD/MM/YYYY)", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gender dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = gender,
                    onValueChange = { },
                    placeholder = { Text("Gender", color = Color.White) },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        backgroundColor = Color.Transparent,
                        textColor = Color.White
                    ),
                    trailingIcon = {
                        IconButton(onClick = { genderExpanded = true }) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = genderExpanded,
                    onDismissRequest = { genderExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    genderOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            gender = option
                            genderExpanded = false
                        }) {
                            Text(text = option)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Height field
            OutlinedTextField(
                value = height,
                onValueChange = { height = it },
                placeholder = { Text("Height (cm)", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight field
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                placeholder = { Text("Weight (kg)", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.List, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fitness Level dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = fitnessLevel,
                    onValueChange = { },
                    placeholder = { Text("Fitness Level", color = Color.White) },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        backgroundColor = Color.Transparent,
                        textColor = Color.White
                    ),
                    trailingIcon = {
                        IconButton(onClick = { fitnessLevelExpanded = true }) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown",
                                tint = Color.White
                            )
                        }
                    }
                )
                
                DropdownMenu(
                    expanded = fitnessLevelExpanded,
                    onDismissRequest = { fitnessLevelExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    fitnessLevelOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            fitnessLevel = option
                            fitnessLevelExpanded = false
                        }) {
                            Text(text = option)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Goals field
            OutlinedTextField(
                value = goals,
                onValueChange = { goals = it },
                placeholder = { Text("Fitness Goals", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    backgroundColor = Color.Transparent,
                    textColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Please fill in all required fields."
                        successMessage = ""
                    } else if (!isValidEmail(email)) {
                        errorMessage = "Invalid email address."
                        successMessage = ""
                    } else if (!isValidPassword(password)) {
                        errorMessage = "Password must be at least 8 characters long."
                        successMessage = ""
                    } else {
                        isLoading = true
                        errorMessage = ""
                        successMessage = ""
                        
                        scope.launch(errorHandler) {
                            try {
                                val heightValue = height.toIntOrNull()
                                val weightValue = weight.toFloatOrNull()
                                
                                viewModel.signUp(
                                    email = email,
                                    password = password,
                                    fullName = fullName,
                                    dateOfBirth = dateOfBirth,
                                    gender = gender,
                                    height = heightValue,
                                    weight = weightValue,
                                    fitnessLevel = fitnessLevel,
                                    goals = goals
                                ).onSuccess {
                                    isLoading = false
                                    successMessage = "Registration successful! You can now log in."
                                    
                                    // Navigate to login screen after 2 seconds
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        try {
                                            navController.navigate("login") {
                                                popUpTo("register") { inclusive = true }
                                            }
                                        } catch (e: Exception) {
                                            Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }, 2000)
                                }.onFailure { exception ->
                                    isLoading = false
                                    errorMessage = "Registration failed: ${exception.message}"
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                isLoading = false
                                val message = "Error during registration: ${e.message}"
                                errorMessage = message
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3)),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Sign Up", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Show success or error message
            if (successMessage.isNotEmpty()) {
                Text(text = successMessage, color = Color.Green, fontWeight = FontWeight.Bold)
            }
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation to login screen
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color.White)
                TextButton(onClick = {
                    try {
                        navController.navigate("login")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Navigation error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Log in", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
