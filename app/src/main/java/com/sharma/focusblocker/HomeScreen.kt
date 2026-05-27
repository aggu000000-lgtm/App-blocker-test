package com.sharma.focusblocker

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.sharma.focusblocker.components.RecoveryCarousel
import com.sharma.focusblocker.service.FocusAccessibilityService
import com.sharma.focusblocker.util.PermissionUtils

import com.example.designsystem.components.CatalogBrandedTopAppBar
import com.example.designsystem.components.CatalogScaffold
import com.example.designsystem.components.CatalogAppLogo
import com.example.designsystem.components.CatalogText
import com.example.designsystem.theme.LocalAppIdentity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToManageSchedules: () -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val appIdentity = LocalAppIdentity.current
    
    var isAccessibilityEnabled by remember { 
        mutableStateOf(PermissionUtils.isAccessibilityServiceEnabled(context, FocusAccessibilityService::class.java)) 
    }

    var showDisclosureDialog by remember { mutableStateOf(false) }
    var showRecoveryCarousel by remember { mutableStateOf(false) }
    var hasLaunchedSettings by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isAccessibilityEnabled = PermissionUtils.isAccessibilityServiceEnabled(context, FocusAccessibilityService::class.java)
                if (hasLaunchedSettings) {
                    hasLaunchedSettings = false
                    if (!isAccessibilityEnabled && PermissionUtils.isRestrictedSettingsAffected(context)) {
                        showRecoveryCarousel = true
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (showDisclosureDialog) {
        AlertDialog(
            onDismissRequest = { showDisclosureDialog = false },
            title = { Text("Accessibility Permission Required") },
            text = {
                Text(
                    "${appIdentity.appName} needs Accessibility Service permission to function.\n\n" +
                    "Purpose: This app uses the Accessibility API to monitor the apps you open and block them if they are on your restricted list during a focus session.\n\n" +
                    "Data Privacy: No personal or sensitive data is collected, stored, or transmitted by this service."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDisclosureDialog = false
                        hasLaunchedSettings = true
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                    }
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDisclosureDialog = false }
                ) {
                    Text("Deny")
                }
            }
        )
    }

    CatalogScaffold(
        topBar = {
            CatalogBrandedTopAppBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (showRecoveryCarousel) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    RecoveryCarousel(onDismiss = { showRecoveryCarousel = false })
                }
            }

            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Accessibility Permission Status: ${if (isAccessibilityEnabled) "Granted" else "Not Granted"}")
                    Spacer(modifier = Modifier.height(8.dp))
                    if (!isAccessibilityEnabled) {
                        Button(onClick = {
                            showDisclosureDialog = true
                        }) {
                            Text("Grant Accessibility Permission")
                        }
                    } else {
                        Button(onClick = {
                            isAccessibilityEnabled = PermissionUtils.isAccessibilityServiceEnabled(context, FocusAccessibilityService::class.java)
                        }) {
                            Text("Refresh Status")
                        }
                    }
                }
            }

            Button(onClick = onNavigateToManageSchedules, modifier = Modifier.padding(top = 16.dp)) {
                Text("Manage Schedules")
            }
        }
    }
}
