package com.slaap.sleeptracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.slaap.sleeptracker.data.SleepSession
import com.slaap.sleeptracker.util.TimeUtils

@Composable
fun SessionCard(
    session: SleepSession,
    modifier: Modifier = Modifier
) {
    val startTime = TimeUtils.formatTime(session.startTime)
    val endTime = session.endTime?.let { TimeUtils.formatTime(it) } ?: "Now"
    val duration = if (session.endTime != null) TimeUtils.formatDuration(session.durationMinutes) else "In Progress"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF14121A)) // Dark background
            .border(1.dp, Color(0xFF2E2A3B), RoundedCornerShape(16.dp)) // Subtle purple/gray border
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Left Icon (Clock in circle)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF221A33)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        drawCircle(
                            color = Color(0xFFB15EFF),
                            style = Stroke(width = 4f)
                        )
                        drawLine(
                            color = Color(0xFFB15EFF),
                            start = center,
                            end = Offset(center.x, center.y - size.height / 3),
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = Color(0xFFB15EFF),
                            start = center,
                            end = Offset(center.x + size.width / 4, center.y),
                            strokeWidth = 4f
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(20.dp))

                // Timeline & Times
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Timeline dots & dashed line
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Canvas(modifier = Modifier.size(4.dp)) {
                            drawCircle(color = Color(0xFF5E8AFF))
                        }
                        Canvas(
                            modifier = Modifier
                                .height(32.dp)
                                .width(2.dp)
                                .padding(vertical = 4.dp)
                        ) {
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.5f),
                                start = Offset(1f, 0f),
                                end = Offset(1f, size.height),
                                strokeWidth = 2f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                            )
                        }
                        Canvas(modifier = Modifier.size(4.dp)) {
                            drawCircle(color = Color(0xFF5E8AFF))
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Times text
                    Column {
                        Text(
                            text = startTime,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = endTime,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            
            // Duration
            Text(
                text = duration,
                color = Color(0xFFB15EFF),
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
