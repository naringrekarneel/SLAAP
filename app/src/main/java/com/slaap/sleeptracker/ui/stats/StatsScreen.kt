package com.slaap.sleeptracker.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slaap.sleeptracker.util.TimeUtils
import androidx.compose.ui.graphics.nativeCanvas
import android.graphics.Paint

@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val stats by viewModel.stats.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sleep Insights",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Tabs
            // Month Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { /* TODO: Previous Month */ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous Month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "July 2026", // Mockup date
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = { /* TODO: Next Month */ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next Month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            stats?.let { s ->
                // Distribution Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Your Sleep Distribution (Monthly)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Most Common: ${s.mostCommonString}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    HistogramChart(
                        data = s.distributionBuckets,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Key Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Key Statistics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avg Sleep
                    StatSquare(
                        title = "Average Sleep",
                        value = "${String.format("%.1f", s.avgSleepMinutes / 60f)} hrs",
                        modifier = Modifier.weight(1f)
                    )
                    // Goal Adherence
                    GoalAdherenceSquare(
                        percent = s.goalAdherencePercent,
                        subtitle = "${(s.goalAdherencePercent * s.totalSessions) / 100}/${s.totalSessions} nights",
                        modifier = Modifier.weight(1f)
                    )
                    // Consistency
                    StatSquare(
                        title = "Most Consistent",
                        value = s.consistencyString,
                        modifier = Modifier.weight(1f)
                    )
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sleep data available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = { /* TODO: Open manual log */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 96.dp, end = 24.dp),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Log Sleep")
                Text("Log Sleep", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun HistogramChart(data: List<Int>, modifier: Modifier = Modifier) {
    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB15EFF), // Purple
            Color(0xFF5E8AFF)  // Blue
        )
    )
    val maxVal = data.maxOrNull()?.coerceAtLeast(1) ?: 1
    // Generate y-axis labels based on max value (e.g., 0 to maxVal)
    val yLabels = (0..maxVal).toList()
    val xLabels = listOf("4", "5", "6", "7", "8", "9", "10+")

    Canvas(modifier = modifier) {
        val yAxisWidth = 40.dp.toPx()
        val xAxisHeight = 30.dp.toPx()
        
        val chartWidth = size.width - yAxisWidth
        val chartHeight = size.height - xAxisHeight
        
        val barWidth = chartWidth / (data.size * 1.5f)
        val spaceWidth = (chartWidth - (barWidth * data.size)) / data.size

        // Draw Y Axis Labels and Lines
        val textPaint = Paint().apply {
            color = android.graphics.Color.GRAY
            textSize = 30f
            textAlign = Paint.Align.RIGHT
        }
        
        yLabels.forEach { y ->
            val yPos = chartHeight - (y.toFloat() / maxVal) * chartHeight
            drawContext.canvas.nativeCanvas.drawText(
                y.toString(),
                yAxisWidth - 20f,
                yPos + 10f,
                textPaint
            )
            // Subtle grid line
            drawLine(
                color = Color.White.copy(alpha = 0.05f),
                start = Offset(yAxisWidth, yPos),
                end = Offset(size.width, yPos),
                strokeWidth = 2f
            )
        }

        // Draw Bars
        data.forEachIndexed { index, value ->
            val barHeight = (value.toFloat() / maxVal) * chartHeight
            val startOffset = yAxisWidth + index * (barWidth + spaceWidth) + spaceWidth / 2f
            val topOffset = chartHeight - barHeight

            if (barHeight > 0) {
                // Glow effect
                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(startOffset - 4f, topOffset - 4f),
                    size = Size(barWidth + 8f, barHeight + 8f),
                    cornerRadius = CornerRadius(16f, 16f),
                    alpha = 0.2f
                )
                
                // Actual bar
                drawRoundRect(
                    brush = gradient,
                    topLeft = Offset(startOffset, topOffset),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )
            }

            // X Axis Label
            textPaint.textAlign = Paint.Align.CENTER
            drawContext.canvas.nativeCanvas.drawText(
                xLabels[index],
                startOffset + barWidth / 2f,
                size.height - 10f,
                textPaint
            )
        }
    }
}

@Composable
fun StatSquare(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
            .aspectRatio(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun GoalAdherenceSquare(percent: Int, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
            .aspectRatio(1f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Goal Adherence",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
            CircularProgressIndicator(
                progress = 1f,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                strokeWidth = 6.dp
            )
            CircularProgressIndicator(
                progress = percent / 100f,
                color = Color(0xFF5E8AFF), // Blue
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
