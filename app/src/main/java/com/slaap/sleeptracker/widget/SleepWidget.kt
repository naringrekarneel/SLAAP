package com.slaap.sleeptracker.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.slaap.sleeptracker.repository.SleepRepository
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class SleepWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = getRepository(context)
        val activeSession = repository.getActiveSession()
        val isActive = activeSession != null

        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(Color(0xFF0F1014)) // Dark background
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val buttonText = if (isActive) "Stop Sleep" else "Start Sleep"
                    
                    Button(
                        text = buttonText,
                        onClick = actionRunCallback<ToggleSleepAction>(),
                        modifier = GlanceModifier.fillMaxSize()
                    )
                }
            }
        }
    }

    private fun getRepository(context: Context): SleepRepository {
        val hiltEntryPoint = EntryPoints.get(context.applicationContext, WidgetEntryPoint::class.java)
        return hiltEntryPoint.sleepRepository()
    }
}

class SleepWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = SleepWidget()
}

class ToggleSleepAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val hiltEntryPoint = EntryPoints.get(context.applicationContext, WidgetEntryPoint::class.java)
        val repository = hiltEntryPoint.sleepRepository()
        
        repository.toggleSleepSession()
        
        // Refresh widget
        SleepWidget().update(context, glanceId)
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun sleepRepository(): SleepRepository
}
