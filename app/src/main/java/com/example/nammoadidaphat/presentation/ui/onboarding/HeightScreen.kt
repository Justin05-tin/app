package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import com.example.nammoadidaphat.ui.theme.AppYellow

@Composable
fun HeightScreen(
    viewModel: UserOnboardingViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val height by viewModel.height.collectAsState()
    val scrollState = rememberScrollState()
    
    // Default height if not set yet, and range limits
    var currentHeight by remember { mutableStateOf(height ?: 165) }
    val minHeight = 140
    val maxHeight = 220
    
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
                text = "What is Your Height?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "This information helps us create the right workout program for your body type.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Height display
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                // Height display
                Text(
                    text = "$currentHeight",
                    fontSize = 80.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // CM label
                Text(
                    text = "cm",
                    fontSize = 24.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            
            // Swipeable height control
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 16.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val horizontalDragChange = dragAmount.x
                            
                            if (horizontalDragChange > 0) {
                                // Swipe right - increase height
                                val newHeight = currentHeight + 1
                                if (newHeight <= maxHeight) {
                                    currentHeight = newHeight
                                }
                            } else if (horizontalDragChange < 0) {
                                // Swipe left - decrease height
                                val newHeight = currentHeight - 1
                                if (newHeight >= minHeight) {
                                    currentHeight = newHeight
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Height swipe control
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left arrow indicator
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Decrease height",
                        tint = AppYellow,
                        modifier = Modifier.clickable {
                            if (currentHeight > minHeight) {
                                currentHeight--
                            }
                        }
                    )
                    
                    // Swipe instruction
                    Text(
                        text = "< Swipe to adjust >",
                        color = AppYellow,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Right arrow indicator
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Increase height",
                        tint = AppYellow,
                        modifier = Modifier.clickable {
                            if (currentHeight < maxHeight) {
                                currentHeight++
                            }
                        }
                    )
                }
            }
            
            // Height meter visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                HeightMeterVisual(
                    currentHeight = currentHeight,
                    highlightColor = AppYellow
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Height set button
            Button(
                onClick = {
                    viewModel.updateHeight(currentHeight)
                    onContinue()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppYellow,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
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
fun HeightMeterVisual(
    currentHeight: Int,
    highlightColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Vertical ruler
        Canvas(
            modifier = Modifier
                .width(60.dp)
                .height(160.dp)
        ) {
            // Draw vertical line
            drawLine(
                color = Color.White.copy(alpha = 0.6f),
                start = Offset(size.width / 2, 0f),
                end = Offset(size.width / 2, size.height),
                strokeWidth = 4f,
                cap = StrokeCap.Round
            )
            
            // Draw tick marks
            val tickCount = 5
            val spacing = size.height / (tickCount - 1)
            
            for (i in 0 until tickCount) {
                val y = i * spacing
                val tickWidth = if (i % 2 == 0) 20f else 10f
                
                drawLine(
                    color = Color.White.copy(alpha = 0.6f),
                    start = Offset(size.width / 2 - tickWidth / 2, y),
                    end = Offset(size.width / 2 + tickWidth / 2, y),
                    strokeWidth = 2f
                )
            }
            
            // Highlight position
            val heightRange = 40 // Range from 140cm to 220cm
            val normalizedHeight = (currentHeight - 140).coerceIn(0, heightRange)
            val highlightPosition = (normalizedHeight.toFloat() / heightRange) * size.height
            val clampedPosition = size.height - highlightPosition
            
            drawCircle(
                color = highlightColor,
                radius = 12f,
                center = Offset(size.width / 2, clampedPosition),
                style = Stroke(width = 4f)
            )
            
            drawCircle(
                color = highlightColor,
                radius = 6f,
                center = Offset(size.width / 2, clampedPosition)
            )
        }
        
        // Height labels
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            val heights = listOf(180, 170, 160, 150)
            
            heights.forEach { height ->
                Text(
                    text = "$height",
                    fontSize = 16.sp,
                    color = if (currentHeight in height-5..height+4) Color.White else Color.White.copy(alpha = 0.7f),
                    fontWeight = if (currentHeight == height) FontWeight.Bold else FontWeight.Normal
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
} 