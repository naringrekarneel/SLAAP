package com.slaap.sleeptracker.ui.home

import android.app.TimePickerDialog
import android.content.Intent
import android.provider.AlarmClock
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import com.slaap.sleeptracker.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slaap.sleeptracker.ui.components.SleepButton
import com.slaap.sleeptracker.util.TimeUtils
import java.time.LocalTime
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val activeSession by viewModel.activeSession.collectAsState(initial = null)
    val lastSession by viewModel.lastSession.collectAsState(initial = null)

    val isActive = activeSession != null
    var showAlarmDialog by remember { mutableStateOf(false) }
    var selectedAlarmTime by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Good Night ",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_custom_moon),
                        contentDescription = null,
                        tint = Color(0xFFB15EFF),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Track your sleep. Improve your life.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E1A29)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star, // Placeholder for chart icon
                    contentDescription = "Insights",
                    tint = Color(0xFFB15EFF)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Last Night Card
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Last Night",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1625))
                    .padding(24.dp)
            ) {
                if (lastSession != null) {
                    val ls = lastSession!!
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(painterResource(id = R.drawable.ic_custom_moon), contentDescription = null, tint = Color(0xFFB15EFF), modifier = Modifier.size(20.dp))
                                Canvas(modifier = Modifier.height(40.dp).width(2.dp).padding(vertical = 4.dp)) {
                                    drawLine(
                                        color = Color(0xFF5E8AFF),
                                        start = Offset(1f, 0f),
                                        end = Offset(1f, size.height),
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                    )
                                }
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFF5E8AFF), modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(TimeUtils.formatTime(ls.startTime), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Fell asleep", color = Color.Gray, fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(20.dp))
                                Text(ls.endTime?.let { TimeUtils.formatTime(it) } ?: "Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Woke up", color = Color.Gray, fontSize = 12.sp)
                            }
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (ls.endTime != null) TimeUtils.formatDuration(ls.durationMinutes) else "In Progress",
                                color = Color(0xFFB15EFF),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Light
                            )
                            Text("Total Sleep", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                } else {
                    Text(
                        text = "No recorded sleep yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SleepButton(
            isActive = isActive,
            onClick = { viewModel.toggleSleep() },
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        val context = LocalContext.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(Color(0xFF16141D))
                .clickable {
                    val currentTime = LocalTime.now()
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            viewModel.scheduleAlarm(hour, minute)
                            selectedAlarmTime = Pair(hour, minute)
                            showAlarmDialog = true
                        },
                        currentTime.hour,
                        currentTime.minute,
                        false
                    ).show()
                }
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Notifications, contentDescription = "Alarm", tint = Color(0xFFB15EFF))
                Spacer(modifier = Modifier.width(16.dp))
                Text("When to wake", color = Color.White, fontSize = 16.sp)
            }
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Go", tint = Color(0xFFB15EFF))
        }

        if (showAlarmDialog && selectedAlarmTime != null) {
            AlertDialog(
                onDismissRequest = { showAlarmDialog = false },
                title = { Text("Set System Alarm?") },
                text = { Text("Would you like to also set your phone's native alarm for this time?") },
                confirmButton = {
                    TextButton(onClick = {
                        val (hour, minute) = selectedAlarmTime!!
                        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                            putExtra(AlarmClock.EXTRA_MESSAGE, "SLAAP Wake Up")
                            putExtra(AlarmClock.EXTRA_HOUR, hour)
                            putExtra(AlarmClock.EXTRA_MINUTES, minute)
                            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                        }
                        context.startActivity(intent)
                        showAlarmDialog = false
                    }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAlarmDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
