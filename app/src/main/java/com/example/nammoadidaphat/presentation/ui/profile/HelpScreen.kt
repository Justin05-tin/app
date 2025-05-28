package com.example.nammoadidaphat.presentation.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    navController: NavController,
    viewModel: HelpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trợ giúp") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Câu hỏi thường gặp",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Hãy tìm câu trả lời cho các câu hỏi của bạn dưới đây:",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(24.dp))
            }
            
            items(uiState.faqItems) { faqItem ->
                FAQItem(faqItem)
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Cần thêm trợ giúp?",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "Liên hệ với chúng tôi qua email:",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Text(
                            text = "support@exs-fitness.com",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { /* Implement email intent */ },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Gửi Email Cho Chúng Tôi")
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Phiên bản ứng dụng: 1.0.0",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun FAQItem(faqItem: FAQItem) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Question header with expand/collapse icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = faqItem.question,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Thu gọn" else "Mở rộng",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Answer - only visible when expanded
            if (expanded) {
                Text(
                    text = faqItem.answer,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
        }
    }
} 