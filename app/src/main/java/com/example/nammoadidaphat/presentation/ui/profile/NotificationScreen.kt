package com.example.nammoadidaphat.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // General Notification Setting
            NotificationSettingItem(
                title = "General Notification",
                isEnabled = uiState.generalNotification,
                onToggle = { viewModel.toggleGeneralNotification() }
            )
            
            // Sound Setting
            NotificationSettingItem(
                title = "Sound",
                isEnabled = uiState.sound,
                onToggle = { viewModel.toggleSound() }
            )
            
            // Vibrate Setting
            NotificationSettingItem(
                title = "Vibrate",
                isEnabled = uiState.vibrate,
                onToggle = { viewModel.toggleVibrate() }
            )
            
            // App Updates Setting
            NotificationSettingItem(
                title = "App Updates",
                isEnabled = uiState.appUpdates,
                onToggle = { viewModel.toggleAppUpdates() }
            )
            
            // New Service Available Setting
            NotificationSettingItem(
                title = "New Service Available",
                isEnabled = uiState.newServiceAvailable,
                onToggle = { viewModel.toggleNewServiceAvailable() }
            )
            
            // New Tips Available Setting
            NotificationSettingItem(
                title = "New tips available",
                isEnabled = uiState.newTipsAvailable,
                onToggle = { viewModel.toggleNewTipsAvailable() }
            )
        }
    }
}

@Composable
fun NotificationSettingItem(
    title: String,
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        )
    }
} 