package com.example.distractionblocker

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.distractionblocker.theme.DistractionBlockerTheme
import com.example.distractionblocker.theme.NeonCyan
import com.example.distractionblocker.theme.TextSecondary
import com.example.distractionblocker.ui.components.AuroraBackground
import com.example.distractionblocker.ui.components.BounceButton
import com.example.distractionblocker.ui.components.GlassmorphicCard
import kotlinx.coroutines.delay
import java.util.Locale

class BlockerActivity : ComponentActivity() {

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "blocked_package"
        
        // Random focus quotes to encourage the user
        private val QUOTES = listOf(
            "Focus is a superpower. Protect it.",
            "You chose this focus session. Stay strong.",
            "Your future self will thank you for focusing.",
            "Distractions are temporary. Your goals are permanent.",
            "Put down the screen, pick up your life.",
            "Deep work is rare, valuable, and rewarding."
        )
    }

    private lateinit var blockedAppsManager: BlockedAppsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        blockedAppsManager = BlockedAppsManager(applicationContext)

        // Select a quote based on current time index to vary it across launches
        val quote = QUOTES[(System.currentTimeMillis() % QUOTES.size).toInt()]
        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: "Blocked App"

        setContent {
            DistractionBlockerTheme {
                // Intercept hardware Back press and force user to exit to Launcher
                BackHandler {
                    exitToLauncher()
                }

                BlockerScreen(
                    quote = quote,
                    blockedPackage = blockedPackage,
                    blockedAppsManager = blockedAppsManager,
                    onExitClick = { exitToLauncher() },
                    onSessionExpired = {
                        // Finish the blocker once the session ends
                        finish()
                    }
                )
            }
        }
    }

    private fun exitToLauncher() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
    }
}

@Composable
fun BlockerScreen(
    quote: String,
    blockedPackage: String,
    blockedAppsManager: BlockedAppsManager,
    onExitClick: () -> Unit,
    onSessionExpired: () -> Unit
) {
    var timeRemainingMillis by remember { 
        mutableStateOf(blockedAppsManager.getSessionEndTime() - System.currentTimeMillis()) 
    }

    // Launch ticker to update remaining time every second
    LaunchedEffect(key1 = true) {
        while (true) {
            val remaining = blockedAppsManager.getSessionEndTime() - System.currentTimeMillis()
            timeRemainingMillis = remaining

            if (remaining <= 0L || !blockedAppsManager.isSessionActive()) {
                onSessionExpired()
                break
            }
            delay(1000L)
        }
    }

    AuroraBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassmorphicCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Shield Lock Icon with Cyan Glow (Custom Canvas Drawing)
                    Box(
                        modifier = Modifier.padding(bottom = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(52.dp)) {
                            val strokeWidth = 3.dp.toPx()
                            val shackleRadius = 13.dp.toPx()
                            
                            // Draw shackle (arc on top)
                            drawArc(
                                color = NeonCyan,
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(size.width / 2 - shackleRadius, size.height * 0.15f),
                                size = Size(shackleRadius * 2, shackleRadius * 2),
                                style = Stroke(width = strokeWidth)
                            )
                            
                            // Draw lock body
                            drawRoundRect(
                                color = NeonCyan,
                                topLeft = Offset(size.width * 0.2f, size.height * 0.4f),
                                size = Size(size.width * 0.6f, size.height * 0.45f),
                                cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
                                style = Stroke(width = strokeWidth)
                            )
                            
                            // Draw keyhole circle
                            drawCircle(
                                color = NeonCyan,
                                radius = 4.dp.toPx(),
                                center = Offset(size.width / 2, size.height * 0.58f)
                            )
                            
                            // Draw keyhole bar
                            drawLine(
                                color = NeonCyan,
                                start = Offset(size.width / 2, size.height * 0.6f),
                                end = Offset(size.width / 2, size.height * 0.7f),
                                strokeWidth = strokeWidth
                            )
                        }
                    }

                    // Main Locked Title
                    Text(
                        text = "Focus Lock Active",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Quote Card Display
                    Text(
                        text = "\"$quote\"",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 24.dp)
                    )

                    // Countdown Timer Display
                    Text(
                        text = formatTime(timeRemainingMillis),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NeonCyan,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    // Exit to Launcher button (Bounce Animation)
                    BounceButton(
                        onClick = onExitClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Back to Launcher",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

/**
 * Format remaining milliseconds into visual HH:MM:SS or MM:SS format
 */
private fun formatTime(millis: Long): String {
    if (millis <= 0) return "00:00"
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
}
