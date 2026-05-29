package com.example.appblocker.ui.settings

import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import com.example.appblocker.ui.components.OrganicToggle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import com.example.appblocker.ui.components.ScreenHeader
import com.example.appblocker.ui.components.SectionGroup
import com.example.appblocker.ui.components.SettingItem
import com.example.appblocker.ui.theme.spacing
import com.example.appblocker.ui.theme.motion
import com.example.appblocker.ui.foundation.rememberHaptics

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val strictMode = remember { mutableStateOf(false) }
    val notifications = remember { mutableStateOf(true) }
    val biometric = remember { mutableStateOf(false) }

    val context = LocalContext.current
    val reduceMotion = remember(context) {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }

    val visibleIndex = remember { mutableStateOf(-1) }
    val haptics = rememberHaptics()
    LaunchedEffect(Unit) {
        if (reduceMotion) {
            visibleIndex.value = 100
        } else {
            for (i in 0..4) {
                delay(50)
                visibleIndex.value = i
            }
        }
    }

    @Composable
    fun StaggeredItem(index: Int, content: @Composable () -> Unit) {
        AnimatedVisibility(
            visible = visibleIndex.value >= index,
            enter = if (reduceMotion) {
                fadeIn(animationSpec = MaterialTheme.motion.interactiveSpring)
            } else {
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = MaterialTheme.motion.interactiveSpringIntOffset
                ) + fadeIn(animationSpec = MaterialTheme.motion.interactiveSpring)
            }
        ) {
            content()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
    ) {
        item {
            StaggeredItem(index = 0) {
                ScreenHeader(title = "Settings")
            }
        }

        item {
            StaggeredItem(index = 1) {
                SectionGroup(title = "General") {
                    SettingItem(
                        title = "Strict Mode",
                        description = "Prevent uninstalling or disabling the blocker",
                        onClick = {
                            val newValue = !strictMode.value
                            if (newValue) haptics.toggleOn() else haptics.toggleOff()
                            strictMode.value = newValue
                        },
                        action = {
                            OrganicToggle(checked = strictMode.value, onCheckedChange = { strictMode.value = it })
                        }
                    )
                    SettingItem(
                        title = "Notifications",
                        description = "Alerts when apps are blocked or sessions end",
                        onClick = {
                            val newValue = !notifications.value
                            if (newValue) haptics.toggleOn() else haptics.toggleOff()
                            notifications.value = newValue
                        },
                        action = {
                            OrganicToggle(checked = notifications.value, onCheckedChange = { notifications.value = it })
                        }
                    )
                }
            }
        }

        item {
            StaggeredItem(index = 1) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }

        item {
            StaggeredItem(index = 2) {
                SectionGroup(title = "Security") {
                    SettingItem(
                        title = "Biometric Lock",
                        description = "Require fingerprint to change settings",
                        onClick = {
                            val newValue = !biometric.value
                            if (newValue) haptics.toggleOn() else haptics.toggleOff()
                            biometric.value = newValue
                        },
                        action = {
                            OrganicToggle(checked = biometric.value, onCheckedChange = { biometric.value = it })
                        }
                    )
                    SettingItem(
                        title = "Accessibility Service",
                        description = "Required for app blocking",
                        onClick = {},
                        action = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    SettingItem(
                        title = "Device Admin",
                        description = "Required for strict mode",
                        onClick = {},
                        action = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        item {
            StaggeredItem(index = 2) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
            }
        }

        item {
            StaggeredItem(index = 3) {
                SectionGroup(title = "About") {
                    SettingItem(
                        title = "Version",
                        onClick = {},
                        action = {
                            Text(
                                text = "1.0.0",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    SettingItem(
                        title = "Build",
                        onClick = {},
                        action = {
                            Text(
                                text = "100000",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        item {
            StaggeredItem(index = 3) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
            }
        }
    }
}
