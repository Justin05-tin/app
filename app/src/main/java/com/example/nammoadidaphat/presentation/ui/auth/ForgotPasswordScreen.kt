package com.example.nammoadidaphat.presentation.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Forgot Password") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Header text
            Text(
                text = "Reset Your Password",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Enter your registered email address to receive a password reset link",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    email = it
                    // Clear status messages when typing
                    errorMessage = null
                    isSuccess = false
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email"
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { 
                        focusManager.clearFocus()
                        if (!isLoading && !isSuccess) {
                            resetPassword(email, context) { success, message ->
                                isLoading = false
                                isSuccess = success
                                errorMessage = if (!success) message else null
                            }
                            isLoading = true
                        }
                    }
                ),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                isError = errorMessage != null
            )
            
            // Error message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically()
            ) {
                errorMessage?.let {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            
            // Success message
            AnimatedVisibility(
                visible = isSuccess,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Password Reset Email Sent",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Please check your email inbox for the reset link",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Password button
            Button(
                onClick = {
                    if (!isLoading && !isSuccess) {
                        resetPassword(email, context) { success, message ->
                            isLoading = false
                            isSuccess = success
                            errorMessage = if (!success) message else null
                        }
                        isLoading = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && !isSuccess && email.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else if (isSuccess) {
                    Text(
                        text = "Return to Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Reset Password",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            if (isSuccess) {
                Button(
                    onClick = {
                        navController.navigate("login") {
                            popUpTo("forgot_password") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Return to Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

fun resetPassword(
    email: String,
    context: Context,
    onComplete: (success: Boolean, message: String?) -> Unit
) {
    if (email.isEmpty()) {
        onComplete(false, "Please enter your email address")
        return
    }

    val auth = FirebaseAuth.getInstance()

    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val signInMethods = task.result?.signInMethods
            if (!signInMethods.isNullOrEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { resetTask ->
                    if (resetTask.isSuccessful) {
                        onComplete(true, null)
                    } else {
                        onComplete(false, "Failed to send reset email: ${resetTask.exception?.message ?: "Unknown error"}")
                    }
                }
            } else {
                onComplete(false, "Email not registered in our system")
            }
        } else {
            onComplete(false, "Error checking email: ${task.exception?.message ?: "Unknown error"}")
        }
    }
}

data class User(
    val name: String = "",
    val email: String = "",
    val key: String = ""
)
