package com.example.nammoadidaphat.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleAuthenticatorScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Google Authenticator") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Information text
            Text(
                text = "Google Authenticator provides two-factor authentication for your account",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Setup Button
            Button(
                onClick = { /* Would be implemented in real app */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Setup Google Authenticator",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Learn more button
            OutlinedButton(
                onClick = { /* Would open docs */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Learn More",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 