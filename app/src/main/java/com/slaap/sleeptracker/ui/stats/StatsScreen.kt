package com.slaap.sleeptracker.ui.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
                    onClick = { viewModel.previousMonth() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = stats?.currentMonthLabel ?: "Loading...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                
                IconButton(
                    onClick = { viewModel.nextMonth() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
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
                    
                    MonthlyBarChart(
                        data = s.dailyHours,
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
fun MonthlyBarChart(data: List<Float>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB15EFF), // Purple
            Color(0xFF5E8AFF)  // Blue
        )
    )
    // Coerce at least 10 hours so the y-axis is standardized
    val maxVal = data.maxOrNull()?.coerceAtLeast(10f) ?: 10f
    
    // Create sensible y-axis labels
    val step = if (maxVal > 15f) 5f else 2f
    val yLabels = generateSequence(0f) { it + step }.takeWhile { it <= maxVal + step }.toList()
    val yMax = yLabels.lastOrNull() ?: 10f
    
    Row(modifier = modifier) {
        val yAxisWidth = 40.dp
        val xAxisHeight = 30.dp
        
        // Fixed Y-Axis Left Side
        Canvas(modifier = Modifier.width(yAxisWidth).fillMaxHeight()) {
            val chartHeight = size.height - xAxisHeight.toPx()
            val textPaint = Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = 30f
                textAlign = Paint.Align.RIGHT
            }
            
            yLabels.forEach { y ->
                val yPos = chartHeight - (y / yMax) * chartHeight
                drawContext.canvas.nativeCanvas.drawText(
                    y.toInt().toString(),
                    size.width - 15f,
                    yPos + 10f,
                    textPaint
                )
            }
        }

        // Scrollable Bar Area Right Side
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .horizontalScroll(scrollState)
        ) {
            val barWidthDp = 16.dp
            val spacingDp = 10.dp
            val totalWidthDp = (barWidthDp + spacingDp) * data.size + spacingDp
            
            Canvas(modifier = Modifier.width(totalWidthDp).fillMaxHeight()) {
                val chartHeight = size.height - xAxisHeight.toPx()
                val barWidth = barWidthDp.toPx()
                val spacing = spacingDp.toPx()

                // Draw subtle horizontal grid lines across the entire scroll area
                yLabels.forEach { y ->
                    val yPos = chartHeight - (y / yMax) * chartHeight
                    drawLine(
                        color = Color.White.copy(alpha = 0.05f),
                        start = Offset(0f, yPos),
                        end = Offset(size.width, yPos),
                        strokeWidth = 2f
                    )
                }

                // Draw Bars and X-Axis labels
                val xTextPaint = Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = Paint.Align.CENTER
                }

                data.forEachIndexed { index, value ->
                    val day = index + 1
                    val barHeight = (value / yMax) * chartHeight
                    val startOffset = spacing + index * (barWidth + spacing)
                    val topOffset = chartHeight - barHeight

                    if (barHeight > 0) {
                        drawRoundRect(
                            brush = gradient,
                            topLeft = Offset(startOffset, topOffset),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                    
                    // Draw x-axis label for EVERY day since we now scroll!
                    drawContext.canvas.nativeCanvas.drawText(
                        day.toString(),
                        startOffset + barWidth / 2f,
                        chartHeight + 35f,
                        xTextPaint
                    )
                }
            }
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
