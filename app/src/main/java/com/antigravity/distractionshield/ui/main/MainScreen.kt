package com.antigravity.distractionshield.ui.main

import android.app.Application
import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.navigation3.runtime.NavKey
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antigravity.distractionshield.theme.*
import com.antigravity.distractionshield.ui.components.AuroraBackground
import com.antigravity.distractionshield.ui.components.BounceButton
import com.antigravity.distractionshield.ui.components.GlassmorphicCard
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onItemClick: (NavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application
    val viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(app) }
    val state by viewModel.uiState.collectAsState(initial = MainScreenState())

    AuroraBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {
                // 1. Header Section
                Text(
                    text = "Distraction Shield",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    modifier = Modifier.padding(top = 28.dp, bottom = 4.dp)
                )
                Text(
                    text = "SwiftUI-Grade Deep Focus",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

                // 2. Permission / Status Bar
                if (!state.isAccessibilityEnabled) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "System Service Inactive",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Text(
                                text = "The blocker requires accessibility services to intercept distracting apps. Tap below to enable.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(bottom = 14.dp)
                            )
                            BounceButton(
                                onClick = { viewModel.showAccessibilityDisclosure() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Enable in System Settings",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                } else {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(NeonCyan)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Active Shield Protection Connected",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonCyan
                            )
                        }
                    }
                }

                // 3. Focus Session Control Section
                if (!state.isSessionActive) {
                    // Session Configuration Card
                    var selectedDurationIndex by remember { mutableStateOf(2) } // Default 30 min
                    val durations = listOf(
                        5 * 60 * 1000L to "5m",
                        15 * 60 * 1000L to "15m",
                        30 * 60 * 1000L to "30m",
                        60 * 60 * 1000L to "1h",
                        120 * 60 * 1000L to "2h"
                    )

                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = "Select Lock Timer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Horizontally aligned duration chips
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                durations.forEachIndexed { index, duration ->
                                    val isSelected = selectedDurationIndex == index
                                    val chipBg by animateColorAsState(
                                        targetValue = if (isSelected) NeonCyan else TextSecondary.copy(alpha = 0.08f),
                                        label = "chip_bg"
                                    )
                                    val chipTextColor by animateColorAsState(
                                        targetValue = if (isSelected) Color.White else TextPrimary,
                                        label = "chip_text"
                                    )

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 4.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(chipBg)
                                            .clickable { selectedDurationIndex = index }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = duration.second,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = chipTextColor
                                        )
                                    }
                                }
                            }

                            // Start Focus Button
                            BounceButton(
                                onClick = {
                                    val duration = durations[selectedDurationIndex].first
                                    viewModel.startFocusSession(duration)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Initiate Deep Focus",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                } else {
                    // Active Focus Session Display Card
                    var timeRemainingMillis by remember { mutableStateOf(state.sessionEndTime - System.currentTimeMillis()) }

                    LaunchedEffect(state.sessionEndTime) {
                        while (true) {
                            timeRemainingMillis = state.sessionEndTime - System.currentTimeMillis()
                            if (timeRemainingMillis <= 0) break
                            delay(1000L)
                        }
                    }

                    // Progress bar calculation
                    val totalDuration = state.sessionTotalDuration.toFloat()
                    val progress = if (totalDuration > 0) {
                        (timeRemainingMillis / totalDuration).coerceIn(0f, 1f)
                    } else 0f

                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Focus Session Active",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Countdown clock
                            Text(
                                text = formatTime(timeRemainingMillis),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NeonCyan,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            // Clean SwiftUI-like thin Progress Bar
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(CircleShape),
                                color = NeonCyan,
                                trackColor = TextSecondary.copy(alpha = 0.15f)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Plus 15 mins extend button
                            BounceButton(
                                onClick = { viewModel.extendFocusSession(15 * 60 * 1000L) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Extend Session (+15m)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // 4. Apps List Section Title and Search Bar
                Text(
                    text = "Restricted Shield Apps",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Glassmorphic Search Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GlassBase)
                        .border(1.dp, GlassBorderDark, RoundedCornerShape(12.dp))
                ) {
                    TextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onSearchQueryChanged(it) },
                        placeholder = { Text("Search apps to block...", color = TextSecondary) },
                        leadingIcon = {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val strokeWidth = 2.dp.toPx()
                                val radius = 5.dp.toPx()
                                val center = Offset(size.width * 0.45f, size.height * 0.45f)
                                drawCircle(
                                    color = TextSecondary,
                                    radius = radius,
                                    center = center,
                                    style = Stroke(width = strokeWidth)
                                )
                                drawLine(
                                    color = TextSecondary,
                                    start = Offset(center.x + radius * 0.707f, center.y + radius * 0.707f),
                                    end = Offset(size.width * 0.75f, size.height * 0.75f),
                                    strokeWidth = strokeWidth
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // 5. Scrollable App Items
                if (state.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = NeonCyan)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(state.filteredApps, key = { it.packageName }) { app ->
                            AppItemRow(
                                app = app,
                                isSessionActive = state.isSessionActive,
                                onToggle = { viewModel.toggleAppBlock(app.packageName) }
                            )
                        }
                    }
                }
            }

            // 6. Focus Session Expired Modal Overlay (Liquid Glass Dialog)
            if (state.showExtensionPrompt) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable(enabled = false) {}, // Intercept clicks
                    contentAlignment = Alignment.Center
                ) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Focus Target Reached!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                            Text(
                                text = "Your designated focus timer has expired. Would you like to protect your state and extend focus?",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            // Quick extend +15m
                            BounceButton(
                                onClick = { viewModel.extendFocusSession(15 * 60 * 1000L) },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = "Extend Focus (+15m)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            // Dismiss / release blockage
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, GlassBorderLight, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.endFocusSession() }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "End Deep Focus",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }

            // 7. Accessibility Prominent Disclosure Modal Overlay (Liquid Glass Dialog)
            if (state.showAccessibilityDisclosure) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(enabled = false) {}, // Intercept clicks
                    contentAlignment = Alignment.Center
                ) {
                    GlassmorphicCard(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .wrapContentHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Accessibility Permission",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            
                            Text(
                                text = "Distraction Shield requires the Accessibility Service permission to block apps during active focus sessions. Here is how we protect your privacy:",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 19.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                             // Glassmorphic Bullet points
                             Column(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .padding(bottom = 24.dp)
                                     .clip(RoundedCornerShape(10.dp))
                                     .background(TextSecondary.copy(alpha = 0.05f))
                                     .padding(14.dp),
                                 verticalArrangement = Arrangement.spacedBy(10.dp)
                             ) {
                                 Row(verticalAlignment = Alignment.Top) {
                                     Text(text = "🎯 ", fontSize = 14.sp)
                                     Text(
                                         text = "Purpose: We only monitor which application is currently in the foreground (window state changes) so we can block distracting apps when your lock timer is active.",
                                         fontSize = 12.sp,
                                         color = TextPrimary
                                     )
                                 }
                                 Row(verticalAlignment = Alignment.Top) {
                                     Text(text = "🛡️ ", fontSize = 14.sp)
                                     Text(
                                         text = "Privacy First: We do NOT track, read, collect, or store any personal data or screen content. Everything is evaluated locally on your device.",
                                         fontSize = 12.sp,
                                         color = TextPrimary
                                     )
                                 }
                                 Row(verticalAlignment = Alignment.Top) {
                                     Text(text = "📡 ", fontSize = 14.sp)
                                     Text(
                                         text = "Zero Networks: No data is sent over the internet or shared with third parties. The app runs completely offline.",
                                         fontSize = 12.sp,
                                         color = TextPrimary
                                     )
                                 }
                             }

                            // Action buttons
                            BounceButton(
                                onClick = {
                                    viewModel.dismissAccessibilityDisclosure()
                                    viewModel.openAccessibilitySettings(context)
                                },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = "Agree & Proceed",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, GlassBorderLight, RoundedCornerShape(14.dp))
                                    .clickable { viewModel.dismissAccessibilityDisclosure() }
                                    .padding(vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Cancel",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppItemRow(
    app: AppInfo,
    isSessionActive: Boolean,
    onToggle: () -> Unit
) {
    val pm = LocalContext.current.packageManager

    // Asynchronously load app icon into memory to keep performance extremely slick
    val appIcon = remember(app.packageName) {
        try {
            val drawable = pm.getApplicationIcon(app.packageName)
            drawable.toBitmap(120, 120).asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Display icon if successfully parsed
            if (appIcon != null) {
                Image(
                    bitmap = appIcon,
                    contentDescription = app.name,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TextSecondary.copy(alpha = 0.15f))
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = app.packageName,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }

            // Custom Neon Slide-Toggle (disabled during active focus session to prevent adjustments)
            NeonToggle(
                checked = app.isBlocked,
                enabled = !isSessionActive,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
fun NeonToggle(
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    // Dynamic animations for thumb slide offsets and track color shifts
    val offsetAnimation by animateFloatAsState(
        targetValue = if (checked) 20f else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "toggle_slide"
    )
    val colorAnimation by animateColorAsState(
        targetValue = if (checked) NeonCyan else TextSecondary.copy(alpha = 0.2f),
        label = "toggle_color"
    )

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 24.dp)
            .clip(CircleShape)
            .background(colorAnimation)
            .border(1.dp, if (checked) NeonCyan.copy(alpha = 0.2f) else GlassBorderDark, CircleShape)
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(start = offsetAnimation.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

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
