package com.example.nammoadidaphat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WorkoutItem(workout: workouts) {
    // Row to layout the workout item horizontally
    Row(
        modifier = Modifier
            .fillMaxWidth() // Make the row fill the width of its parent
            .background(Color(0xFFF5F5F5)) // Set background color
            .padding(8.dp) // Set padding around the content
    ) {
        // Column for stacking text vertically
        Column(
            modifier = Modifier.align(Alignment.CenterVertically) // Align the column vertically in the center
        ) {
            // Workout name with bold font and larger size
            Text(
                text = workout.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            // Workout duration with gray color and smaller size
            Text(
                text = workout.duration,
                color = Color.Gray,
                fontSize = 14.sp
            )
            // Workout description with gray color and smaller size
            Text(
                text = workout.description,
                color = Color.Gray,
                fontSize = 14.sp
            )
            // Reps and Sets information with gray color and smaller size
            Text(
                text = "Reps: ${workout.reps} | Sets: ${workout.sets}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}
