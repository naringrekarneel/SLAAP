package com.slaap.sleeptracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import android.Manifest
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.slaap.sleeptracker.ui.history.HistoryScreen
import com.slaap.sleeptracker.ui.home.HomeScreen
import com.slaap.sleeptracker.ui.settings.SettingsScreen
import com.slaap.sleeptracker.ui.stats.StatsScreen
import com.slaap.sleeptracker.ui.theme.SLAAPTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material.icons.filled.Refresh

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        setContent {
            SLAAPTheme {
                MainScreen()
            }
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector?, val iconRes: Int? = null) {
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object History : Screen("history", "History", null, R.drawable.ic_nav_history)
    object Stats : Screen("stats", "Stats", null, R.drawable.ic_nav_stats)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
}

val items = listOf(
    Screen.Home,
    Screen.History,
    Screen.Stats,
    Screen.Settings
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color(0xFF1A1625))
                    .border(1.dp, Color(0xFF2E2A3B), RoundedCornerShape(32.dp))
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { screen ->
                    val isSelected = currentRoute == screen.route
                    val color = if (isSelected) Color(0xFFB15EFF) else Color(0xFF8B8D98)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            .padding(8.dp)
                    ) {
                        if (screen.icon != null) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                tint = color,
                                modifier = Modifier.size(28.dp)
                            )
                        } else if (screen.iconRes != null) {
                            Icon(
                                painter = painterResource(id = screen.iconRes),
                                contentDescription = screen.title,
                                tint = color,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // The purple indicator line
                        Box(
                            modifier = Modifier
                                .width(20.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(if (isSelected) color else Color.Transparent)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF0F1014)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.History.route) { HistoryScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }
    }
}
