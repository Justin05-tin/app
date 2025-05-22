package com.example.nammoadidaphat.presentation.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.Arrangement
import com.example.nammoadidaphat.R

@Composable
fun HomeScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Bụng") }
    val tabs = listOf("Bụng", "Cánh Tay", "Ngực", "Chân")

    // Updated workouts list for all body parts
    val workouts = when (selectedTab) {
        "Cánh Tay" -> listOf(
            Workout(R.drawable.ic_arm, "Cánh Tay Người bắt đầu", "20 phút • 16 Bài tập", 1),
            Workout(R.drawable.ic_arm, "Cánh Tay Trung bình", "29 phút • 21 Bài tập", 2),
            Workout(R.drawable.ic_arm, "Cánh Tay Nâng cao", "36 phút • 21 Bài tập", 3)
        )
        "Ngực" -> listOf(
            Workout(R.drawable.ic_chest, "Ngực Người bắt đầu", "20 phút • 16 Bài tập", 1),
            Workout(R.drawable.ic_chest, "Ngực Trung bình", "29 phút • 21 Bài tập", 2),
            Workout(R.drawable.ic_chest, "Ngực Nâng cao", "36 phút • 21 Bài tập", 3)
        )
        "Chân" -> listOf(
            Workout(R.drawable.ic_leg, "Chân Người bắt đầu", "20 phút • 16 Bài tập", 1),
            Workout(R.drawable.ic_leg, "Chân Trung bình", "29 phút • 21 Bài tập", 2),
            Workout(R.drawable.ic_leg, "Chân Nâng cao", "36 phút • 21 Bài tập", 3)
        )
        else -> listOf(
            Workout(R.drawable.ic_abs, "Bụng Người bắt đầu", "20 phút • 16 Bài tập", 1),
            Workout(R.drawable.ic_abs, "Bụng Trung bình", "29 phút • 21 Bài tập", 2),
            Workout(R.drawable.ic_abs, "Bụng Nâng cao", "36 phút • 21 Bài tập", 3)
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Title
        Text(
            text = "TẬP LUYỆN ",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            tabs.forEach { tab ->
                Text(
                    text = tab,
                    color = if (tab == selectedTab) Color.Blue else Color.Gray,
                    fontWeight = if (tab == selectedTab) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.clickable { selectedTab = tab }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Workout List (LazyColumn with items)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(workouts) { workout ->
                WorkoutItem(workout)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tags using Row instead of FlowRow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf("Giữ dáng", ">15 phút", "Xây dựng cơ", "Giãn cơ", "7-15 phút", "Trung bình").forEach { tag ->
                Chip(text = tag)
            }
        }
    }
}

@Composable
fun Chip(text: String) {
    Box(
        modifier = Modifier
            .background(Color(0xFFE0E0E0), shape = RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}

data class Workout(
    val imageRes: Int,
    val title: String,
    val duration: String,
    val difficulty: Int
)
