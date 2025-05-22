package com.example.nammoadidaphat.presentation.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    val scope = rememberCoroutineScope()
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        // Header with title and logout button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TẬP LUYỆN",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            // Nút đăng xuất
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        // Thực hiện đăng xuất trong scope
                        scope.launch {
                            // Gọi phương thức signOut từ AuthViewModel
                            authViewModel.signOut()
                            
                            // Sau khi đăng xuất, chuyển hướng về màn hình đăng nhập
                            navController.navigate("login") {
                                // Xóa tất cả các màn hình ra khỏi stack điều hướng
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Đăng xuất",
                    tint = Color.Black,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

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
