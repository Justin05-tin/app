package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import kotlin.math.abs

@Composable
fun WeightScreen(viewModel: UserOnboardingViewModel, onContinue: () -> Unit, onBack: () -> Unit) {
    val weight by viewModel.weight.collectAsState()
    val scrollState = rememberScrollState()
    var selectedWeight by remember { mutableIntStateOf((weight?.toInt() ?: 65)) }
    
    val minWeight = 30
    val maxWeight = 150
    
    viewModel.updateWeight(selectedWeight.toFloat())
    
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "What is Your Weight?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Weight in kg. Don't worry, you can always change it later.",
                fontSize = 14.sp,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = {
                        if (selectedWeight < maxWeight) {
                            selectedWeight++
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Increase weight",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(36.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                when {
                                    dragAmount < 0 && selectedWeight < maxWeight -> selectedWeight++
                                    dragAmount > 0 && selectedWeight > minWeight -> selectedWeight--
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        (-4..4).forEach { offset ->
                            val value = selectedWeight + offset
                            if (value in minWeight..maxWeight) {
                                val alpha = when (abs(offset)) {
                                    0 -> 1f
                                    1 -> 0.8f
                                    2 -> 0.5f
                                    3 -> 0.3f
                                    else -> 0.2f
                                }
                                val size = when (abs(offset)) {
                                    0 -> 36.sp
                                    1 -> 26.sp
                                    2 -> 22.sp
                                    3 -> 20.sp
                                    else -> 18.sp
                                }
                                val weight = when (abs(offset)) {
                                    0 -> FontWeight.Bold
                                    1 -> FontWeight.Medium
                                    else -> FontWeight.Normal
                                }
                                val color = if (offset == 0) Color.White else Color.Gray.copy(alpha = alpha)

                                val itemModifier = if (offset == 0)
                                    Modifier
                                        .padding(vertical = 8.dp)
                                        .background(Color(0xFF8B5CF6), RoundedCornerShape(8.dp))
                                        .fillMaxWidth(0.6f)
                                        .height(56.dp)
                                else
                                    Modifier.padding(vertical = 4.dp)

                                Box(
                                    modifier = itemModifier,
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$value",
                                        fontSize = size,
                                        fontWeight = weight,
                                        color = color,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        if (selectedWeight > minWeight) {
                            selectedWeight--
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Decrease weight",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }


            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Back button
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray.copy(alpha = 0.3f),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) { Text(text = "Back", fontSize = 16.sp, fontWeight = FontWeight.Medium) }
                
                Button(
                    onClick = onContinue,
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(28.dp)
                ) { Text(text = "Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WeightScreenPreview() {
    val mockViewModel = object {
        fun updateWeight(weight: Float) {}
        val weight = object {
            @Composable
            fun collectAsState() = remember { mutableIntStateOf(65) }
        }
    }
    
    WeightScreen(
        viewModel = mockViewModel as UserOnboardingViewModel,
        onContinue = {},
        onBack = {}
    )
}
