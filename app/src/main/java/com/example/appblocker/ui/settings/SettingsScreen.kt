package com.example.appblocker.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.appblocker.ui.components.ScreenHeader
import com.example.appblocker.ui.components.SectionGroup
import com.example.appblocker.ui.components.SettingItem
import com.example.appblocker.ui.theme.spacing

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val strictMode = remember { mutableStateOf(false) }
    val notifications = remember { mutableStateOf(true) }
    val biometric = remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.screenHorizontal)
    ) {
        ScreenHeader(title = "Settings")

        SectionGroup(title = "General") {
            SettingItem(
                title = "Strict Mode",
                description = "Prevent uninstalling or disabling the blocker",
                onClick = { strictMode.value = !strictMode.value },
                action = {
                    Switch(checked = strictMode.value, onCheckedChange = { strictMode.value = it })
                }
            )
            SettingItem(
                title = "Notifications",
                description = "Alerts when apps are blocked or sessions end",
                onClick = { notifications.value = !notifications.value },
                action = {
                    Switch(checked = notifications.value, onCheckedChange = { notifications.value = it })
                }
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

        SectionGroup(title = "Security") {
            SettingItem(
                title = "Biometric Lock",
                description = "Require fingerprint to change settings",
                onClick = { biometric.value = !biometric.value },
                action = {
                    Switch(checked = biometric.value, onCheckedChange = { biometric.value = it })
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

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

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

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
    }
}
