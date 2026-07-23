package com.slaap.sleeptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SleepButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isActive) {
        listOf(Color(0xFFFF7A00), Color(0xFFFF004D)) // Warm Stop Glow
    } else {
        listOf(Color(0xFFB15EFF), Color(0xFF5E8AFF)) // Purple to Blue Start Glow
    }

    val text = if (isActive) "Stop Sleep" else "Start Sleep"
    val subtext = if (isActive) "Tap to wake up" else "Tap to begin"
    
    // Using default Icons since we don't have custom drawable resources
    val icon = if (isActive) Icons.Filled.Warning else Icons.Outlined.Star

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(280.dp) // Much larger
            .clip(CircleShape)
            .drawBehind {
                val strokeWidth = 8.dp.toPx()
                // The glowing border
                drawCircle(
                    brush = Brush.linearGradient(gradientColors),
                    radius = (size.minDimension - strokeWidth) / 2,
                    style = Stroke(width = strokeWidth)
                )
                // The inner faint glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            gradientColors[0].copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    radius = size.minDimension / 2
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = gradientColors[0],
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = gradientColors[0]
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
