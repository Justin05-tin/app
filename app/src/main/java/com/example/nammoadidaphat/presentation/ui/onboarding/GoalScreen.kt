package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import com.example.nammoadidaphat.ui.theme.AppYellow

@Composable
fun GoalScreen(
    viewModel: UserOnboardingViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val goal by viewModel.goal.collectAsState()
    val scrollState = rememberScrollState()
    
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
                text = "What is Your Goal?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Select your fitness goal to help us create a personalized workout plan for you.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Goal options
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GoalOption(
                    text = "Lose Weight",
                    isSelected = goal == "lose_weight",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGoal("lose_weight") }
                )
                
                GoalOption(
                    text = "Gain Weight",
                    isSelected = goal == "gain_weight",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGoal("gain_weight") }
                )
                
                GoalOption(
                    text = "Muscle Mass Gain",
                    isSelected = goal == "muscle_gain",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGoal("muscle_gain") }
                )
                
                GoalOption(
                    text = "Shape Body",
                    isSelected = goal == "shape_body",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGoal("shape_body") }
                )
                
                GoalOption(
                    text = "Others",
                    isSelected = goal == "others",
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGoal("others") }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = goal.isNotEmpty()
            ) {
                Text(
                    text = "Continue",
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
fun GoalOption(
    text: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) selectedColor else Color(0xFF2F2F2F).copy(alpha = 0.7f)
    val textColor = if (isSelected) Color.Black else Color.White
    val cornerShape = RoundedCornerShape(28.dp)
    
    Row(
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (isSelected) Color.Black else Color.White
            )
        }
    }
} 