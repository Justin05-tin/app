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

@Composable
fun AgeScreen(viewModel: UserOnboardingViewModel, onContinue: () -> Unit, onBack: () -> Unit) {
    val age by viewModel.age.collectAsState()
    val scrollState = rememberScrollState()
    var selectedAge by remember { mutableIntStateOf(age ?: 30) }

    // Set reasonable limits for age
    val minAge = 18
    val maxAge = 80

    // Update the age in the viewModel when it changes
    viewModel.updateAge(selectedAge)

    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(scrollState),
                horizontalAlignment = Alignment.Start
        ) {
            Text(
                    text = "How Old Are You?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text =
                            "Age in years. This will help us to personalize an exercise program plan that suits you.",
                    fontSize = 14.sp,
                    color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Age selector
            Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Up arrow to increase age
                IconButton(
                        onClick = {
                            if (selectedAge < maxAge) {
                                selectedAge++
                            }
                        }
                ) {
                    Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Increase age",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(36.dp)
                    )
                }

                // Age number vertical selector with drag support
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .height(300.dp) // Increased height for better spacing
                                        .pointerInput(Unit) {
                                            detectVerticalDragGestures { _, dragAmount ->
                                                when {
                                                    dragAmount < 0 && selectedAge < maxAge ->
                                                            selectedAge++
                                                    dragAmount > 0 && selectedAge > minAge ->
                                                            selectedAge--
                                                }
                                            }
                                        },
                        contentAlignment = Alignment.Center
                ) {
                    // Previous numbers with consistent spacing
                    Text(
                            text = "${selectedAge - 4}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray.copy(alpha = 0.2f),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
                    )

                    Text(
                            text = "${selectedAge - 3}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 60.dp)
                    )

                    Text(
                            text = "${selectedAge - 2}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 100.dp)
                    )

                    Text(
                            text = "${selectedAge - 1}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray.copy(alpha = 0.8f),
                            modifier = Modifier.align(Alignment.TopCenter).padding(top = 140.dp)
                    )

                    // Selected age
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .padding(horizontal = 80.dp)
                                            .height(56.dp) // Fixed height for the selected box
                                            .background(Color(0xFF8B5CF6), RoundedCornerShape(8.dp))
                                            .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                    ) {
                        Text(
                                text = "$selectedAge",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = TextAlign.Center
                        )
                    }

                    // Next numbers with consistent spacing
                    Text(
                            text = "${selectedAge + 1}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray.copy(alpha = 0.8f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 140.dp)
                    )

                    Text(
                            text = "${selectedAge + 2}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)
                    )

                    Text(
                            text = "${selectedAge + 3}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp)
                    )

                    Text(
                            text = "${selectedAge + 4}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Gray.copy(alpha = 0.2f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp)
                    )
                }

                // Down arrow to decrease age
                IconButton(
                        onClick = {
                            if (selectedAge > minAge) {
                                selectedAge--
                            }
                        }
                ) {
                    Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Decrease age",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(36.dp)
                    )
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
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = Color.LightGray.copy(alpha = 0.3f),
                                        contentColor = Color.Black
                                ),
                        shape = RoundedCornerShape(28.dp)
                ) { Text(text = "Back", fontSize = 16.sp, fontWeight = FontWeight.Medium) }

                // Continue button
                Button(
                        onClick = onContinue,
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF8B5CF6),
                                        contentColor = Color.White
                                ),
                        shape = RoundedCornerShape(28.dp)
                ) { Text(text = "Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

// Preview function for AgeScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AgeScreenPreview() {
    // Mock ViewModel and callbacks for preview
    val mockViewModel = object {
        fun updateAge(age: Int) {}
        val age = object {
            @Composable
            fun collectAsState() = remember { mutableIntStateOf(30) }
        }
    }
    
    AgeScreen(
        viewModel = mockViewModel as UserOnboardingViewModel,
        onContinue = {},
        onBack = {}
    )
}
