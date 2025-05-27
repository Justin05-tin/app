package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel

@Composable
fun NameScreen(
    viewModel: UserOnboardingViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    // Get the display name safely without try-catch
    val displayName by viewModel.displayName.collectAsState()
    
    // Use the value to initialize our state
    var name by remember { mutableStateOf(displayName) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What's Your Name?",
            fontSize = 24.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Please enter your full name",
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        TextField(
            value = name,
            onValueChange = { 
                name = it
                viewModel.updateDisplayName(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your full name") },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Continue")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back")
        }
    }
} 