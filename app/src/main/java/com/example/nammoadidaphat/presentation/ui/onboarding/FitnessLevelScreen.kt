package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FitnessLevelScreen(
    viewModel: UserOnboardingViewModel,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    val fitnessLevel by viewModel.fitnessLevel.collectAsState()
    val scrollState = rememberScrollState()
    
    val selectedLevel = remember { mutableStateOf(fitnessLevel.ifEmpty { "beginner" }) }
    
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "What is your fitness level?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Be honest, this helps us create your personalized plan",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Fitness level options
            FitnessLevelOption(
                title = "Beginner",
                description = "I'm new to fitness or haven't exercised in a while",
                isSelected = selectedLevel.value == "beginner",
                onClick = {
                    selectedLevel.value = "beginner"
                    viewModel.updateFitnessLevel("beginner")
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FitnessLevelOption(
                title = "Intermediate",
                description = "I exercise 1-3 times per week",
                isSelected = selectedLevel.value == "intermediate",
                onClick = {
                    selectedLevel.value = "intermediate"
                    viewModel.updateFitnessLevel("intermediate")
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FitnessLevelOption(
                title = "Advanced",
                description = "I exercise 4+ times per week consistently",
                isSelected = selectedLevel.value == "advanced",
                onClick = {
                    selectedLevel.value = "advanced"
                    viewModel.updateFitnessLevel("advanced")
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Navigation buttons in the same row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Back button
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.3f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Back",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                // Finish button
                Button(
                    onClick = {
                        // First save user data to Firestore
                        CoroutineScope(Dispatchers.IO).launch {
                            val result = viewModel.saveUserOnboardingData()
                            // Then complete onboarding on main thread
                            withContext(Dispatchers.Main) {
                                onComplete()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) {
                    Text(
                        text = "Finish",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FitnessLevelOption(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) Color(0xFF8B5CF6) else Color.LightGray
    val backgroundColor = if (isSelected) Color(0xFFEDE9FE) else Color.White
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF8B5CF6) else Color.Black
            )
            
            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

// Preview function for FitnessLevelScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FitnessLevelScreenPreview() {
    // Mock ViewModel and callbacks for preview
    val mockViewModel = object {
        fun updateFitnessLevel(level: String) {}
        val fitnessLevel = object {
            @Composable
            fun collectAsState() = remember { mutableStateOf("intermediate") }
        }
    }
    
    FitnessLevelScreen(
        viewModel = mockViewModel as UserOnboardingViewModel,
        onComplete = {},
        onBack = {}
    )
}