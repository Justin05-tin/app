package com.example.nammoadidaphat.presentation.ui.report

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import com.example.nammoadidaphat.ui.theme.PrimaryColor
import kotlin.math.pow
import kotlin.math.round

@Composable
fun ReportScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            // Report header
            Text(
                text = "Báo cáo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Daily stats section
            Text(
                text = "THỐNG KÊ HÀNG NGÀY",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Daily stats cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_fitness,
                    value = "0",
                    label = "BÀI TẬP",
                    iconTint = Color(0xFF4CAF50)
                )
                
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_fire,
                    value = "0",
                    label = "KCAL",
                    iconTint = Color(0xFFE91E63)
                )
                
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = R.drawable.ic_timer,
                    value = "0",
                    label = "GIÂY",
                    iconTint = Color(0xFF2196F3)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Body metrics section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Chỉ số cơ thể",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Weight display from user data
                    BodyMetricRow(
                        icon = R.drawable.ic_weight,
                        label = "Trọng Lượng",
                        value = formatWeight(currentUser?.weight),
                        iconTint = Color(0xFF2196F3)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Height display from user data
                    BodyMetricRow(
                        icon = R.drawable.ic_height,
                        label = "Chiều cao",
                        value = formatHeight(currentUser?.height),
                        iconTint = Color(0xFF2196F3)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Calculate and display BMI
                    val bmiData = calculateBMI(currentUser)
                    
                    BodyMetricRow(
                        icon = R.drawable.ic_bmi,
                        label = "BMI",
                        value = bmiData.displayText,
                        iconTint = Color(0xFF2196F3)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // BMI indicator with dynamic position based on BMI value
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(bmiData.indicatorPosition)
                                .height(8.dp)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF4CAF50), // Green for normal
                                            Color(0xFFFFEB3B)  // Yellow for warning
                                        )
                                    ),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Water intake section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lượng Nước",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Decrease button
                        FloatingActionButton(
                            onClick = { /* Handle decrease */ },
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Decrease",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        // Water counter
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "0/9",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3)
                            )
                            
                            Text(
                                text = "(0 ml)",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        // Increase button
                        FloatingActionButton(
                            onClick = { /* Handle increase */ },
                            containerColor = PrimaryColor,
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase"
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp)) // Extra space for bottom nav bar
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: Int,
    value: String,
    label: String,
    iconTint: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BodyMetricRow(
    icon: Int,
    label: String,
    value: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper function to format weight
private fun formatWeight(weight: Float?): String {
    return if (weight != null) {
        "$weight kg"
    } else {
        "-- kg"
    }
}

// Helper function to format height
private fun formatHeight(height: Int?): String {
    return if (height != null) {
        "$height cm"
    } else {
        "-- cm"
    }
}

// Data class to hold BMI calculation results
data class BMIData(
    val bmiValue: Float,
    val category: String,
    val displayText: String,
    val indicatorPosition: Float
)

// Function to calculate BMI from user data
private fun calculateBMI(user: User?): BMIData {
    val height = user?.height
    val weight = user?.weight
    
    if (height == null || weight == null || height <= 0 || weight <= 0) {
        return BMIData(
            bmiValue = 0f,
            category = "Không xác định",
            displayText = "-- (Không xác định)",
            indicatorPosition = 0.5f
        )
    }
    
    // BMI formula: weight (kg) / (height (m))²
    val heightInMeters = height / 100f
    val bmi = weight / (heightInMeters.pow(2))
    val roundedBmi = round(bmi * 10) / 10
    
    // Determine BMI category
    val (category, position) = when {
        bmi < 18.5 -> "Thiếu cân" to 0.2f
        bmi < 25 -> "Bình Thường" to 0.5f
        bmi < 30 -> "Thừa cân" to 0.7f
        else -> "Béo phì" to 0.9f
    }
    
    return BMIData(
        bmiValue = roundedBmi,
        category = category,
        displayText = "$roundedBmi ($category)",
        indicatorPosition = position
    )
} 