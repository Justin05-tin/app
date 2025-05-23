package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun GenderScreen(
    viewModel: UserOnboardingViewModel,
    onContinue: () -> Unit
) {
    val gender by viewModel.gender.collectAsState()
    val maleSelected = gender == "male"
    val femaleSelected = gender == "female"
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
            // Back button (disabled for first screen)
            IconButton(
                onClick = { /* No back action on first screen */ },
                enabled = false
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Transparent
                )
            }
            
            Text(
                text = "What's Your Gender",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "This helps us create a personalized fitness program just for you.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                // Male Option
                GenderOption(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_male,
                    label = "Male",
                    isSelected = maleSelected,
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGender("male") }
                )
                
                // Female Option
                GenderOption(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_female,
                    label = "Female",
                    isSelected = femaleSelected,
                    selectedColor = AppYellow,
                    onClick = { viewModel.updateGender("female") }
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
                enabled = gender.isNotEmpty()
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
fun GenderOption(
    modifier: Modifier = Modifier,
    icon: Int,
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) selectedColor else Color.Transparent
    val labelColor = if (isSelected) Color.Black else Color.White
    
    Column(
        modifier = modifier
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(if (isSelected) backgroundColor else Color(0xFF2F2F2F))
                .border(
                    border = if (isSelected) BorderStroke(2.dp, Color.White) else BorderStroke(0.dp, Color.Transparent),
                    shape = CircleShape
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                modifier = Modifier.size(48.dp),
                tint = if (isSelected) Color.Black else Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = labelColor
        )
    }
} 