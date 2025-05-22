package com.example.nammoadidaphat.presentation.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var securityKey by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.backgr),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds // sửa lỗi & hiển thị toàn ảnh
        )




        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            OutlinedTextField(
                value = securityKey,
                onValueChange = { securityKey = it },
                label = { Text("Security Key", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password", color = Color.White) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    checkSecurityKey(email, securityKey, context, navController)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
            ) {
                Text("Reset Password", color = Color.White)
            }
        }
    }
}

fun checkSecurityKey(
    email: String,
    securityKey: String,
    context: Context,
    navController: NavController
) {
    val database = FirebaseDatabase.getInstance().reference

    database.child("users").orderByChild("email").equalTo(email).get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val user = snapshot.children.first().getValue(User::class.java)
                if (user?.key == securityKey) {
                    resetPassword(email, context, navController)
                } else {
                    Toast.makeText(context, "Security key is incorrect", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Email not registered", Toast.LENGTH_SHORT).show()
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
}

fun resetPassword(
    email: String,
    context: Context,
    navController: NavController
) {
    val auth = FirebaseAuth.getInstance()

    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val signInMethods = task.result?.signInMethods
            if (!signInMethods.isNullOrEmpty()) {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { resetTask ->
                    if (resetTask.isSuccessful) {
                        Toast.makeText(context, "Password reset email sent!", Toast.LENGTH_SHORT).show()
                        navController.navigate("login") {
                            popUpTo("forgot_password") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Failed to send reset email: ${resetTask.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Email not registered", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

data class User(
    val name: String = "",
    val email: String = "",
    val key: String = ""
)
