package com.example.nammoadidaphat.presentation.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.nammoadidaphat.R
import timber.log.Timber
import com.example.nammoadidaphat.domain.model.User
import com.example.nammoadidaphat.presentation.ui.theme.ThemeViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel()
) {
    // Get current user state from ViewModel
    val uiState by viewModel.uiState.collectAsState(initial = ProfileUiState(isLoading = true))
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)
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
        try {
            // Add small delay to ensure authentication state is fully settled
            delay(100)
            viewModel.getCurrentUser()
        } catch (e: Exception) {
            Timber.e(e, "Error in ProfileScreen LaunchedEffect: ${e.message}")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            // Only show loading indicator when loading
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
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
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = uiState.error ?: "Failed to load profile",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = { 
                        try {
                            viewModel.getCurrentUser() 
                        } catch (e: Exception) {
                            Timber.e(e, "Error retrying getCurrentUser: ${e.message}")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Try Again")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                TextButton(onClick = {
                    viewModel.signOut(navController)
                    navController.navigate("login") {
                        popUpTo("main_nav_graph") { inclusive = true }
                    }
                }) {
                    Text("Log Out", color = MaterialTheme.colorScheme.primary)
                }
            }
        } else {
            // Only show the content when not loading and no errors
            // Create a safe default user object with proper defaults
            val user = uiState.user ?: User(
                id = "default",
                email = "user@example.com",
                displayName = "User",
                avatar = "", // Empty string for avatar
                gender = "Not specified",
                fitnessLevel = "beginner",
                goals = listOf("Get fit"),
                authProvider = "password"
            )
            
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
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
                        // Profile Image - Cách xử lý an toàn và mạnh mẽ hơn 
                        val profileImageUrl = user.avatar ?: ""
                        
                        // Kiểm tra kỹ lưỡng hơn cho URL avatar hợp lệ
                        val isValidUrl = profileImageUrl.isNotBlank() && 
                            (profileImageUrl.startsWith("http://") || 
                             profileImageUrl.startsWith("https://") || 
                             profileImageUrl.startsWith("content://") || 
                             profileImageUrl.startsWith("file://"))
                        
                        if (isValidUrl) {
                            // Tải ảnh từ URL sử dụng Coil với xử lý lỗi toàn diện
                            Image(
                                painter = rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .data(profileImageUrl)
                                        .crossfade(true)
                                        .error(R.drawable.profile_placeholder)
                                        .fallback(R.drawable.profile_placeholder)
                                        .placeholder(R.drawable.profile_placeholder)
                                        .build()
                                ),
                                contentDescription = "Profile Picture",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            // Luôn hiển thị ảnh mặc định cho URL không hợp lệ/rỗng
                            DefaultProfileImage()
                        }
                        
                        // Edit Button (Purple circle with edit icon)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF7B50E8))
                                .clickable { 
                                    try {
                                        viewModel.navigateToEditProfile(navController) 
                                    } catch (e: Exception) {
                                        Timber.e(e, "Error navigating to edit profile: ${e.message}")
                                    }
                                }
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
                    val displayName = remember(user) { user.displayName.takeIf { it.isNotBlank() } ?: "User" }
                    Text(
                        text = displayName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // User Email - safely handle null value with fallback
                    val email = remember(user) { user.email.takeIf { it.isNotBlank() } ?: "" }
                    Text(
                        text = email,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Premium Upgrade Banner - only show if user is not premium
                    // Safely handle null value with default false
                    if (true) { // Always show premium banner
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.upgradeToProVersion() },
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF7B50E8)
                            ),
                            shape = RoundedCornerShape(16.dp)
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
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Go to Premium",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Settings options
                    SettingsItem(
                        icon = Icons.Default.Person,
                        title = "Edit Profile",
                        onClick = { viewModel.navigateToEditProfile(navController) }
                    )

                    SettingsItem(
                        icon = Icons.Default.Notifications,
                        title = "Notification",
                        onClick = { viewModel.navigateToNotifications(navController) }
                    )

                    SettingsItem(
                        icon = Icons.Default.Lock,
                        title = "Security",
                        onClick = { viewModel.navigateToSecurity(navController) }
                    )

                    SettingsItem(
                        icon = Icons.AutoMirrored.Filled.Help,
                        title = "Help",
                        onClick = { viewModel.navigateToHelp(navController) }
                    )
                    
                    // Dark Theme Toggle
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
                                imageVector = Icons.Default.DarkMode,
                                contentDescription = "Dark Theme",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Text(
                                text = "Dark Theme",
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = { themeViewModel.toggleDarkTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                                uncheckedThumbColor = MaterialTheme.colorScheme.surfaceVariant,
                                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            )
                        )
                    }
                    
                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
                    
                    // Logout Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
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
        
        // Logout confirmation dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { 
                    Text(
                        text = "Logout",
                        color = Color(0xFFE57373),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to log out?",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.signOut(navController)
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
                            containerColor = Color(0xFF7B50E8),
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
                },
                dismissButton = {
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
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
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
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun DefaultProfileImage() {
    // Sử dụng cách an toàn hơn không dùng try-catch
    // Sử dụng một biến cờ để kiểm soát việc hiển thị fallback
    val useDefaultImage = true
    
    if (useDefaultImage) {
        Image(
            painter = painterResource(id = R.drawable.profile_placeholder),
            contentDescription = "Default Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
    } else {
        // Fallback chỉ sử dụng trong trường hợp hiếm gặp
        FallbackProfileBox()
    }
}

@Composable
private fun FallbackProfileBox() {
    // This is now a backup fallback in case the resource is missing for some reason
    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .background(
                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF7B50E8),
                        Color(0xFF9C6AFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Default Profile",
            tint = Color.White,
            modifier = Modifier.size(60.dp)
        )
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
    
    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
} 