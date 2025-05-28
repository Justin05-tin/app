package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val title: String, val icon: ImageVector) {
    object Overview : BottomNavScreen("overview", "Tổng Quan", Icons.Filled.Home)
    object Workout : BottomNavScreen("workout", "Bài tập", Icons.Filled.Favorite)
    object Report : BottomNavScreen("report", "Báo cáo", Icons.Filled.List)
    object Profile : BottomNavScreen("profile", "Cá Nhân", Icons.Filled.Person)

    companion object {
        val items = listOf(Overview, Workout, Report, Profile)
    }
} 