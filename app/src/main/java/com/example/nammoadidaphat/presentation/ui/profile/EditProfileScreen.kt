package com.example.nammoadidaphat.presentation.ui.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.nammoadidaphat.R
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    // State for image picker
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Upload the image
            viewModel.uploadProfileImage(it) { success ->
                if (success) {
                    Timber.d("Profile image updated successfully")
                } else {
                    Timber.e("Failed to update profile image")
                }
            }
        }
    }
    
    // Effect to load user profile
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
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
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Loading indicator
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (uiState.error != null && uiState.user == null) {
                // Error state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = { viewModel.loadUserProfile() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Retry")
                    }
                }
            } else {
                // Form content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image with edit option
                    Box(
                        modifier = Modifier.padding(vertical = 16.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        // Profile image
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (uiState.isUploadingImage) {
                                // Show loading indicator while uploading
                                CircularProgressIndicator(
                                    modifier = Modifier.size(40.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                // Display profile image with comprehensive error handling
                                val profileImage = uiState.user?.avatar ?: ""
                                
                                // Kiểm tra kỹ lưỡng xem avatar có hợp lệ không
                                val isValidUrl = profileImage.isNotBlank() &&
                                    (profileImage.startsWith("http://") || 
                                     profileImage.startsWith("https://") || 
                                     profileImage.startsWith("content://") || 
                                     profileImage.startsWith("file://"))
                                
                                if (isValidUrl) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(profileImage)
                                                .crossfade(true)
                                                .placeholder(R.drawable.profile_placeholder)
                                                .error(R.drawable.profile_placeholder)
                                                .build()
                                        ),
                                        contentDescription = "Profile Picture",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Hiển thị ảnh mặc định không dùng try-catch
                                    // Sử dụng flag để kiểm soát việc hiển thị
                                    val useDefaultResource = true
                                    
                                    if (useDefaultResource) {
                                        Image(
                                            painter = painterResource(id = R.drawable.profile_placeholder),
                                            contentDescription = "Default Profile Picture",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    } else {
                                        // Fallback avatar đơn giản
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = "Default Avatar",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(50.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Edit button overlaid on the image
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .offset(x = 0.dp, y = (-5).dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            border = BorderStroke(1.dp, Color.White)
                        ) {
                            IconButton(
                                onClick = { imagePickerLauncher.launch("image/*") },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Change Photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Form Fields
                    OutlinedTextField(
                        value = viewModel.displayName,
                        onValueChange = { viewModel.displayName = it },
                        label = { Text("Full Name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Name"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Gender Dropdown
                    ExposedDropdownMenuBox(
                        expanded = viewModel.isGenderMenuExpanded,
                        onExpandedChange = { viewModel.isGenderMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = viewModel.gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Gender"
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isGenderMenuExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = viewModel.isGenderMenuExpanded,
                            onDismissRequest = { viewModel.isGenderMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Male") },
                                onClick = {
                                    viewModel.gender = "Male"
                                    viewModel.isGenderMenuExpanded = false
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Female") },
                                onClick = {
                                    viewModel.gender = "Female"
                                    viewModel.isGenderMenuExpanded = false
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Other") },
                                onClick = {
                                    viewModel.gender = "Other"
                                    viewModel.isGenderMenuExpanded = false
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Age
                    OutlinedTextField(
                        value = viewModel.age,
                        onValueChange = { viewModel.age = it },
                        label = { Text("Age") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Age"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Height
                    OutlinedTextField(
                        value = viewModel.height,
                        onValueChange = { viewModel.height = it },
                        label = { Text("Height (cm)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Height"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Weight
                    OutlinedTextField(
                        value = viewModel.weight,
                        onValueChange = { viewModel.weight = it },
                        label = { Text("Weight (kg)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Weight"
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Fitness Level Dropdown
                    ExposedDropdownMenuBox(
                        expanded = viewModel.isFitnessLevelMenuExpanded,
                        onExpandedChange = { viewModel.isFitnessLevelMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = viewModel.fitnessLevel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fitness Level") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Fitness Level"
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = viewModel.isFitnessLevelMenuExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        
                        ExposedDropdownMenu(
                            expanded = viewModel.isFitnessLevelMenuExpanded,
                            onDismissRequest = { viewModel.isFitnessLevelMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Beginner") },
                                onClick = {
                                    viewModel.fitnessLevel = "Beginner"
                                    viewModel.isFitnessLevelMenuExpanded = false
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Intermediate") },
                                onClick = {
                                    viewModel.fitnessLevel = "Intermediate"
                                    viewModel.isFitnessLevelMenuExpanded = false
                                }
                            )
                            
                            DropdownMenuItem(
                                text = { Text("Advanced") },
                                onClick = {
                                    viewModel.fitnessLevel = "Advanced"
                                    viewModel.isFitnessLevelMenuExpanded = false
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Save Button
                    Button(
                        onClick = {
                            viewModel.saveUserProfile { success ->
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !uiState.isSaving,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Save Changes",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = uiState.error!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
} 