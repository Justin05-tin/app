package com.example.nammoadidaphat.presentation.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.nammoadidaphat.R
import timber.log.Timber
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import androidx.compose.ui.window.Dialog
import com.example.nammoadidaphat.domain.model.User

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // Get current user state from ViewModel
    val uiState by viewModel.uiState.collectAsState(initial = ProfileUiState(isLoading = true))
    val context = LocalContext.current
    
    // State for logout dialog
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Helper function to check if a resource exists
    fun isResourceAvailable(resId: Int): Boolean {
        return try {
            context.resources.getResourceName(resId)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.getCurrentUser()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (uiState.isLoading) {
            // Only show loading indicator when loading
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFF7B50E8)
            )
        } else if (uiState.error != null) {
            // Show error message if there's an error
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Something went wrong",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = uiState.error ?: "Failed to load profile",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.getCurrentUser() },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF7B50E8),
                        contentColor = Color.White
                    )
                ) {
                    Text("Try Again")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = {
                    viewModel.logout()
                    navController.navigate("login") {
                        popUpTo("main_nav_graph") { inclusive = true }
                    }
                }) {
                    Text("Log Out", color = Color(0xFF7B50E8))
                }
            }
        } else {
            // Only show the content when not loading and no errors
            val user = uiState.user ?: User() // Ensure we have a non-null user object
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header with App Icon and Profile text
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // App Icon (Purple circle with fitness icon)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7B50E8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Fitness",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // More options (three dots)
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More Options",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Profile Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Picture with Edit Button
                    Box(contentAlignment = Alignment.BottomEnd) {
                        // Profile Image - safely handle null/empty values
                        val profileImageUrl = user.avatarUrl
                        
                        if (profileImageUrl != null && profileImageUrl.isNotEmpty()) {
                            // Load image from URL using Coil without try-catch
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(profileImageUrl)
                                        .crossfade(true)
                                        .error(R.drawable.profile_placeholder)
                                        .fallback(R.drawable.profile_placeholder)
                                        .build()
                                ),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            DefaultProfileImage()
                        }
                        
                        // Edit Button (Purple circle with edit icon)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7B50E8))
                                .clickable { viewModel.editProfilePicture() }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // User Name - safely handle null value with fallback
                    Text(
                        text = user.fullName.takeIf { it.isNotBlank() } ?: "User",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // User Email - safely handle null value with fallback
                    Text(
                        text = user.email.takeIf { it.isNotBlank() } ?: "",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Premium Upgrade Banner - only show if user is not premium
                    // Safely handle null value with default false
                    if (user.isPremium != true) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.upgradeToProVersion() },
                            backgroundColor = Color(0xFF7B50E8),
                            shape = RoundedCornerShape(16.dp),
                            elevation = 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // PRO Badge
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700))
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "PRO",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(16.dp))
                                    
                                    Column {
                                        Text(
                                            text = "Upgrade to Premium",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        
                                        Text(
                                            text = "Enjoy workout access without ads and restrictions",
                                            fontSize = 14.sp,
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                                
                                // Chevron icon
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Go to Premium",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                    
                    // Menu Items
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Edit Profile
                        MenuRow(
                            icon = Icons.Default.Person,
                            title = "Edit Profile",
                            onClick = { viewModel.navigateToEditProfile(navController) }
                        )
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        
                        // Notifications
                        MenuRow(
                            icon = Icons.Default.Notifications,
                            title = "Notifications",
                            onClick = { viewModel.navigateToNotifications(navController) }
                        )
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        
                        // Security
                        MenuRow(
                            icon = Icons.Default.Lock,
                            title = "Security",
                            onClick = { viewModel.navigateToSecurity(navController) }
                        )
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        
                        // Help
                        MenuRow(
                            icon = Icons.Default.Info,
                            title = "Help",
                            onClick = { viewModel.navigateToHelp(navController) }
                        )
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        
                        // Dark Theme
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = "Dark Theme",
                                    tint = Color(0xFF333333),
                                    modifier = Modifier.size(24.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Text(
                                    text = "Dark Theme",
                                    fontSize = 16.sp,
                                    color = Color(0xFF333333)
                                )
                            }
                            
                            Switch(
                                checked = uiState.isDarkTheme,
                                onCheckedChange = { viewModel.toggleDarkTheme() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = Color(0xFF7B50E8),
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color.LightGray
                                )
                            )
                        }
                        
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        
                        // Logout
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLogoutDialog = true }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Logout",
                                tint = Color(0xFFE57373), // Red color
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "Logout",
                                fontSize = 16.sp,
                                color = Color(0xFFE57373) // Red color
                            )
                        }
                    }
                }
            }
        }
        
        // Logout confirmation dialog
        if (showLogoutDialog) {
            Dialog(
                onDismissRequest = { showLogoutDialog = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Logout",
                        color = Color(0xFFE57373),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Are you sure you want to log out?",
                        fontSize = 16.sp,
                        color = Color.DarkGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Logout button
                    Button(
                        onClick = {
                            viewModel.logout()
                            showLogoutDialog = false
                            // Navigate back to login screen
                            navController.navigate("login") {
                                popUpTo("main_nav_graph") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFF7B50E8),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = "Yes, Logout",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Cancel button
                    OutlinedButton(
                        onClick = { showLogoutDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7B50E8)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, Color(0xFF7B50E8).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFF333333),
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF333333)
        )
    }
}

@Composable
private fun DefaultProfileImage() {
    // Pre-compute whether the placeholder is available
    val context = LocalContext.current
    val isPlaceholderAvailable = remember {
        try {
            context.resources.getResourceName(R.drawable.profile_placeholder)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    if (isPlaceholderAvailable) {
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
    } else {
        FallbackProfileBox()
    }
}

@Composable
private fun FallbackProfileBox() {
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = Color.White,
            modifier = Modifier.size(60.dp)
        )
    }
} 