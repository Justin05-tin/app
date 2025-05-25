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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun GoalScreen(
    viewModel: UserOnboardingViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val goal by viewModel.goal.collectAsState()
    val scrollState = rememberScrollState()
    
    // Goals options
    val goals = listOf(
        "Get Fitter",
        "Gain Weight",
        "Lose Weight", 
        "Building Muscles",
        "Improving Endurance",
        "Others"
    )
    
    var selectedGoal by remember { mutableStateOf(goal.ifEmpty { goals[0] }) }
    
    // Update the goal in the viewModel
    if (selectedGoal.isNotEmpty()) {
        viewModel.updateGoal(selectedGoal)
    }
    
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "What is Your Goal?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "You can choose more than one. Don't worry, you can always change it later.",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Goal options as checkboxes
            goals.forEach { goalOption ->
                val isSelected = selectedGoal == goalOption
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF8B5CF6) else Color.LightGray,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                color = if (isSelected) Color(0xFF8B5CF6).copy(alpha = 0.1f) else Color.White,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedGoal = goalOption
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = goalOption,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color(0xFF8B5CF6) else Color.Black
                        )
                        
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(
                                        color = Color(0xFF8B5CF6),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
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
                
                // Continue button
                Button(
                    onClick = onContinue,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp),
                    enabled = selectedGoal.isNotEmpty()
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
}

@Composable
fun GoalOption(
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
            .height(80.dp)
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

// Preview function for GoalScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GoalScreenPreview() {
    // Mock ViewModel and callbacks for preview
    val mockViewModel = object {
        fun updateGoal(goal: String) {}
        val goal = object {
            @Composable
            fun collectAsState() = remember { mutableStateOf("lose_weight") }
        }
    }
    
    GoalScreen(
        viewModel = mockViewModel as UserOnboardingViewModel,
        onContinue = {},
        onBack = {}
    )
} 