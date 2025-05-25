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

@Composable
fun GenderScreen(viewModel: UserOnboardingViewModel, onContinue: () -> Unit) {
    val gender by viewModel.gender.collectAsState()
    val scrollState = rememberScrollState()
    val selectedGender = remember { mutableStateOf(gender.ifEmpty { "male" }) }
    
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Tell us About Yourself",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "To give you a better experience we need to know your gender",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Gender selection options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Male Option
                GenderOption(
                    gender = "male",
                    isSelected = selectedGender.value == "male",
                    onClick = {
                        selectedGender.value = "male"
                        viewModel.updateGender("male")
                    },
                    modifier = Modifier.weight(1f)
                )
                
                // Female Option
                GenderOption(
                    gender = "female",
                    isSelected = selectedGender.value == "female",
                    onClick = {
                        selectedGender.value = "female"
                        viewModel.updateGender("female")
                    },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Continue Button placed at the bottom
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8B5CF6),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun GenderOption(
    gender: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) Color(0xFF8B5CF6) else Color.LightGray
    val backgroundColor = if (isSelected) Color(0xFFEDE9FE) else Color.White
    
    Box(
        modifier = modifier
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // You can add an icon here if needed
            Spacer(modifier = Modifier.height(16.dp))
            
            // Gender Text
            Text(
                text = gender.replaceFirstChar { it.uppercase() },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color(0xFF8B5CF6) else Color.Black
            )
        }
    }
}

// Preview function for GenderScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GenderScreenPreview() {
    // Mock ViewModel and callbacks for preview
    val mockViewModel = object {
        fun updateGender(gender: String) {}
        val gender = object {
            @Composable
            fun collectAsState() = remember { mutableStateOf("male") }
        }
    }
    
    GenderScreen(
        viewModel = mockViewModel as UserOnboardingViewModel,
        onContinue = {}
    )
} 