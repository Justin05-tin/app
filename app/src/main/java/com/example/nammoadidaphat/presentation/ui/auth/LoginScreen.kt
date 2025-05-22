package com.example.nammoadidaphat.presentation.ui.auth

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 0.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val waveHeight = size.height / 4
                val waveWidth = size.width / 6

                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, size.height - waveHeight)
                    cubicTo(
                        waveWidth, size.height,
                        size.width - waveWidth, size.height,
                        size.width, size.height - waveHeight
                    )
                    lineTo(size.width, 0f)
                    close()
                }

                drawPath(path = path, color = Color.Black)
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Already Have An\nAccount?",
                    color = Color.White,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = painterResource(id = R.drawable.gym),
                    contentDescription = "Gym Icon",
                    modifier = Modifier.size(228.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login Button
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    loginWithEmailPassword(
                        context = context,
                        email = email,
                        password = password,
                        onSuccess = {
                            Toast.makeText(context, "Login success!", Toast.LENGTH_SHORT).show()

                            // Navigate to HomeScreen after successful login
                            navController.navigate("home") {
                                // Pop the login screen off the back stack to prevent going back to login
                                popUpTo("login") { inclusive = true }
                            }
                        },
                        onFailure = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black)
        ) {
            Text("LOGIN", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))


        TextButton(onClick = {
            navController.navigate("forgot_password")
        }) {
            Text(
                text = "FORGOT PASSWORD?",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }



        Spacer(modifier = Modifier.height(16.dp))
        Text("OR", fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.facebook),
                contentDescription = "Facebook Login",
                modifier = Modifier.size(36.dp)
            )

        }

        Spacer(modifier = Modifier.height(24.dp))

        ClickableText(
            text = AnnotatedString("New User? Register Now"),
            onClick = {
                navController.navigate("register") // Navigate to register screen
            },
            style = TextStyle(
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val waveHeight = size.height * 0.1f
                val numberOfWaves = 1
                val waveLength = size.width / numberOfWaves

                val path = Path().apply {
                    moveTo(0f, waveHeight)

                    for (i in 0 until numberOfWaves) {
                        val startX = i * waveLength
                        val endX = startX + waveLength
                        val controlX1 = startX + waveLength / 4
                        val controlY1 = 0f
                        val controlX2 = startX + 3 * waveLength / 4
                        val controlY2 = size.height

                        cubicTo(
                            controlX1, controlY1,
                            controlX2, controlY2,
                            endX, waveHeight
                        )
                    }

                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }

                drawPath(path = path, color = Color.Black)
            }
        }
    }
}

fun loginWithEmailPassword(
    context: Context,
    email: String,
    password: String,
    onSuccess: () -> Unit,
    onFailure: (String) -> Unit
) {
    val auth = Firebase.auth

    // Kiểm tra email và mật khẩu có trống không
    if (email.isBlank() || password.isBlank()) {
        onFailure("Email và mật khẩu không được để trống.")
        return
    }

    // Thực hiện đăng nhập bằng email và mật khẩu
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Đăng nhập thành công
                onSuccess()
            } else {
                // Xử lý lỗi khi đăng nhập thất bại
                val exceptionMessage = task.exception?.message ?: "Đăng nhập thất bại do lỗi không xác định"

                // Kiểm tra các lỗi chi tiết từ Firebase
                when {
                    exceptionMessage.contains("The email address is badly formatted") -> {
                        onFailure("Địa chỉ email không hợp lệ.")
                    }
                    exceptionMessage.contains("There is no user record corresponding to this identifier") -> {
                        onFailure("Email không tồn tại.")
                    }
                    exceptionMessage.contains("The password is invalid") -> {
                        onFailure("Mật khẩu không đúng.")
                    }
                    else -> {
                        onFailure(exceptionMessage)
                    }
                }
            }
        }
}