package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import com.example.nammoadidaphat.ui.theme.AppYellow
import kotlinx.coroutines.launch

@Composable
fun FitnessLevelScreen(
    viewModel: UserOnboardingViewModel,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val fitnessLevel by viewModel.fitnessLevel.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val scrollState = rememberScrollState()
    
    val coroutineScope = rememberCoroutineScope()
    
    // Show error if present
    LaunchedEffect(error) {
        if (error != null) {
            // In a real app, you'd show a snackbar or dialog here
            viewModel.clearError()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image with overlay
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = "Background image",
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
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            
            Text(
                text = "Physical Activity Level",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "This helps us determine the intensity of your workouts and tailor your fitness journey.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Fitness level options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FitnessLevelOption(
                    text = "Beginner",
                    isSelected = fitnessLevel == "beginner",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateFitnessLevel("beginner") }
                )
                
                FitnessLevelOption(
                    text = "Intermediate",
                    isSelected = fitnessLevel == "intermediate",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateFitnessLevel("intermediate") }
                )
                
                FitnessLevelOption(
                    text = "Advanced",
                    isSelected = fitnessLevel == "advanced",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateFitnessLevel("advanced") }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    coroutineScope.launch {
                        val result = viewModel.saveUserOnboardingData()
                        if (result.isSuccess) {
                            onFinish()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = fitnessLevel.isNotEmpty() && !isLoading
            ) {
                Text(
                    text = if (isLoading) "Saving..." else "Finish",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Increase bottom spacing to avoid overlap with PageIndicator
            Spacer(modifier = Modifier.height(70.dp))
        }
    }
}

@Composable
fun FitnessLevelOption(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) selectedColor else Color(0xFF2F2F2F).copy(alpha = 0.7f)
    val textColor = if (isSelected) Color.Black else Color.White
    
    val cornerShape = RoundedCornerShape(28.dp)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(backgroundColor, cornerShape)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) Color.White else Color.Transparent,
                shape = cornerShape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}