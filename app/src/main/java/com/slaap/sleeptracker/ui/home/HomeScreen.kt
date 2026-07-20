package com.slaap.sleeptracker.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.slaap.sleeptracker.ui.components.SessionCard
import com.slaap.sleeptracker.ui.components.SleepButton
import android.app.TimePickerDialog
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime
import android.content.Intent
import android.provider.AlarmClock

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val activeSession by viewModel.activeSession.collectAsState(initial = null)
    val lastSession by viewModel.lastSession.collectAsState(initial = null)

    val isActive = activeSession != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Display Last Session if exists and no active session
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isActive) {
                Text(
                    text = "Sleeping...",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Text(
                    text = "Last Night",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                lastSession?.let { session ->
                    SessionCard(session = session)
                } ?: run {
                    Text(
                        text = "No recorded sleep",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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

        val context = LocalContext.current
        androidx.compose.material3.TextButton(
            onClick = {
                val currentTime = LocalTime.now()
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        viewModel.scheduleAlarm(hour, minute)
                        
                        // Also set the system alarm silently
                        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                            putExtra(AlarmClock.EXTRA_MESSAGE, "SLAAP Wake Up")
                            putExtra(AlarmClock.EXTRA_HOUR, hour)
                            putExtra(AlarmClock.EXTRA_MINUTES, minute)
                            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
                        }
                        context.startActivity(intent)
                    },
                    currentTime.hour,
                    currentTime.minute,
                    false
                ).show()
            },
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(
                "When to wake",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
