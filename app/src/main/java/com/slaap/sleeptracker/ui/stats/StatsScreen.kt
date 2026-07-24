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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.border
import com.slaap.sleeptracker.R
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
                Column {
                    Text(
                        text = "Sleep Insights",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Understand your sleep. Improve your days.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFB15EFF).copy(alpha = 0.3f), CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = Color(0xFFB15EFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color(0xFFB15EFF).copy(alpha = 0.3f), CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color(0xFFB15EFF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Tabs
            // Month Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.previousMonth() },
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = Color(0xFFB15EFF)
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
                        .size(48.dp)
                        .shadow(elevation = 4.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Month",
                        tint = Color(0xFFB15EFF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            stats?.let { s ->
                // Distribution Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 4.dp, shape = RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Sleep Distribution (Monthly)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Most Common: ${s.mostCommonString}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFB15EFF)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFB15EFF).copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_trending_up),
                                contentDescription = "Trending",
                                tint = Color(0xFFB15EFF),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    MonthlyBarChart(
                        data = s.dailyHours,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    Text(
                        text = "Key Statistics",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .width(28.dp)
                            .height(2.dp)
                            .background(Color(0xFFB15EFF), RoundedCornerShape(1.dp))
                    )
                }

                // Key Statistics Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avg Sleep
                    StatSquare(
                        title = "Average Sleep",
                        value = "${String.format("%.1f", s.avgSleepMinutes / 60f)} hrs",
                        iconPainter = painterResource(id = R.drawable.ic_moon),
                        modifier = Modifier.weight(1f)
                    )
                    // Goal Adherence
                    GoalAdherenceSquare(
                        percent = s.goalAdherencePercent,
                        subtitle = "of nights",
                        iconPainter = painterResource(id = R.drawable.ic_target),
                        modifier = Modifier.weight(1f)
                    )
                    // Consistency
                    StatSquare(
                        title = "Most Consistent",
                        value = s.consistencyString,
                        iconPainter = androidx.compose.ui.graphics.vector.rememberVectorPainter(Icons.Default.Star),
                        modifier = Modifier.weight(1f)
                    )
                }
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No sleep data available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
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
fun StatSquare(title: String, value: String, iconPainter: androidx.compose.ui.graphics.painter.Painter, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFB15EFF).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = title,
                tint = Color(0xFFB15EFF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFFB15EFF)
        )
    }
}

@Composable
fun GoalAdherenceSquare(percent: Int, subtitle: String, iconPainter: androidx.compose.ui.graphics.painter.Painter, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFB15EFF).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = iconPainter,
                contentDescription = "Goal",
                tint = Color(0xFFB15EFF),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Goal Adherence",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(12.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(50.dp)) {
            CircularProgressIndicator(
                progress = 1f,
                color = Color(0xFFB15EFF).copy(alpha = 0.2f),
                strokeWidth = 4.dp
            )
            CircularProgressIndicator(
                progress = percent / 100f,
                color = Color(0xFFB15EFF),
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Text(
                text = "$percent%",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
