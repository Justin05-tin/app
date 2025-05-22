package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nammoadidaphat.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavigation(mainNavController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    BottomNavRoot(authViewModel = authViewModel, mainNavController = mainNavController)
}
