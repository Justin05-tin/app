package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import com.example.nammoadidaphat.ui.theme.AppYellow
import kotlin.math.roundToInt

@Composable
fun WeightScreen(
    viewModel: UserOnboardingViewModel,
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val weight by viewModel.weight.collectAsState()
    val scrollState = rememberScrollState()
    
    var isKg by remember { mutableStateOf(true) }
    val density = LocalDensity.current
    
    // Default weight values range
    val minWeight = if (isKg) 30f else 66f // 30kg or 66lb
    val maxWeight = if (isKg) 150f else 330f // 150kg or 330lb
    
    // Current weight in display units
    var displayWeight by remember { 
        mutableStateOf(weight?.let { 
            if (isKg) it else it * 2.20462f
        } ?: if (isKg) 75f else 165f) 
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
                text = "What Is Your Weight?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your weight helps us determine the right intensity for your workouts.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Unit toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(40.dp)
                        .background(AppYellow.copy(alpha = 0.2f), RoundedCornerShape(50))
                        .border(1.dp, AppYellow, RoundedCornerShape(50))
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    if (isKg) AppYellow else Color.Transparent,
                                    RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp)
                                )
                                .clickable { 
                                    isKg = true
                                    // Convert lb to kg
                                    if (!isKg) {
                                        displayWeight = displayWeight / 2.20462f
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "KG",
                                color = if (isKg) Color.Black else Color.White,
                                fontWeight = if (isKg) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .background(
                                    if (!isKg) AppYellow else Color.Transparent,
                                    RoundedCornerShape(topEnd = 50.dp, bottomEnd = 50.dp)
                                )
                                .clickable { 
                                    isKg = false 
                                    // Convert kg to lb
                                    if (isKg) {
                                        displayWeight = displayWeight * 2.20462f
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LB",
                                color = if (!isKg) Color.Black else Color.White,
                                fontWeight = if (!isKg) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
            
            // Weight display
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${displayWeight.roundToInt()}",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = if (isKg) "kg" else "lb",
                        fontSize = 20.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
            
            // Swipeable weight slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(16.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val horizontalDragChange = dragAmount.x
                            val step = if (isKg) 0.5f else 1f
                            
                            if (horizontalDragChange > 0) {
                                // Swipe right - increase weight
                                val newWeight = displayWeight + step
                                if (newWeight <= maxWeight) {
                                    displayWeight = newWeight
                                }
                            } else if (horizontalDragChange < 0) {
                                // Swipe left - decrease weight
                                val newWeight = displayWeight - step
                                if (newWeight >= minWeight) {
                                    displayWeight = newWeight
                                }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Weight ruler
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left arrow indicator
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Decrease weight",
                        tint = AppYellow,
                        modifier = Modifier.clickable {
                            val step = if (isKg) 0.5f else 1f
                            val newWeight = displayWeight - step
                            if (newWeight >= minWeight) {
                                displayWeight = newWeight
                            }
                        }
                    )
                    
                    // Weight ruler
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
                        contentDescription = "Increase weight",
                        tint = AppYellow,
                        modifier = Modifier.clickable {
                            val step = if (isKg) 0.5f else 1f
                            val newWeight = displayWeight + step
                            if (newWeight <= maxWeight) {
                                displayWeight = newWeight
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            Button(
                onClick = {
                    // Convert lb to kg if necessary
                    val weightInKg = if (isKg) displayWeight else displayWeight / 2.20462f
                    viewModel.updateWeight(weightInKg)
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