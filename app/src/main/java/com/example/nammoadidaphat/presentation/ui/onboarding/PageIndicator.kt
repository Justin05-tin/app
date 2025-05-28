package com.example.nammoadidaphat.presentation.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.nammoadidaphat.ui.theme.AppYellow

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit = {}
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until pageCount) {
            val isSelected = i == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppYellow else Color.White.copy(alpha = 0.5f))
                    .clickable { onPageSelected(i) }
            )
        }
    }
} 