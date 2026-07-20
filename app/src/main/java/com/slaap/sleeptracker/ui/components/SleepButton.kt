package com.slaap.sleeptracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.slaap.sleeptracker.ui.theme.AccentSleep
import com.slaap.sleeptracker.ui.theme.AccentWake
import com.slaap.sleeptracker.ui.theme.BackgroundDark

@Composable
fun SleepButton(
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isActive) {
        listOf(AccentWake, AccentWake.copy(alpha = 0.7f))
    } else {
        listOf(AccentSleep, AccentSleep.copy(alpha = 0.7f))
    }

    val text = if (isActive) "Stop Sleep" else "Start Sleep"

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientColors))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Custom indication can be added for micro-animations
                onClick = onClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(BackgroundDark)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                color = if (isActive) AccentWake else AccentSleep
            )
        }
    }
}
