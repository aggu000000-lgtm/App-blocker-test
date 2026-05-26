package com.sharma.focusblocker

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sharma.focusblocker.data.BlockerPreferences
import com.sharma.focusblocker.service.FocusAccessibilityService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onNavigateToFeature: () -> Unit) {
    val context = LocalContext.current
    val prefs = remember { BlockerPreferences(context) }
    
    var isAccessibilityEnabled by remember { 
        mutableStateOf(isAccessibilityServiceEnabled(context, FocusAccessibilityService::class.java)) 
    }
    var restrictedPackages by remember { 
        mutableStateOf(prefs.getRestrictedPackages().toList()) 
    }
    var packageInput by remember { mutableStateOf("") }

    var showDisclosureDialog by remember { mutableStateOf(false) }

    if (showDisclosureDialog) {
        AlertDialog(
            onDismissRequest = { showDisclosureDialog = false },
            title = { Text("Accessibility Permission Required") },
            text = {
                Text(
                    "Focus Blocker needs Accessibility Service permission to function.\n\n" +
                    "Purpose: This app uses the Accessibility API to monitor the apps you open and block them if they are on your restricted list during a focus session.\n\n" +
                    "Data Privacy: No personal or sensitive data is collected, stored, or transmitted by this service."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDisclosureDialog = false
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Focus Blocker Foundation",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

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
                        isAccessibilityEnabled = isAccessibilityServiceEnabled(context, FocusAccessibilityService::class.java)
                    }) {
                        Text("Refresh Status")
                    }
                }
            }
        }

        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Restricted Packages", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = packageInput,
                    onValueChange = { packageInput = it },
                    label = { Text("Package Name (e.g. com.example.app)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    if (packageInput.isNotBlank()) {
                        prefs.addRestrictedPackage(packageInput.trim())
                        restrictedPackages = prefs.getRestrictedPackages().toList()
                        packageInput = ""
                    }
                }) {
                    Text("Add Package")
                }
            }
        }

        LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
            items(restrictedPackages) { pkg ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = pkg)
                    Button(onClick = {
                        prefs.removeRestrictedPackage(pkg)
                        restrictedPackages = prefs.getRestrictedPackages().toList()
                    }) {
                        Text("Remove")
                    }
                }
            }
        }

        Button(onClick = onNavigateToFeature, modifier = Modifier.padding(top = 16.dp)) {
            Text("Go to Feature")
        }
    }
}

fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
    val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    ) ?: return false
    
    val colonSplitter = TextUtils.SimpleStringSplitter(':')
    colonSplitter.setString(enabledServices)
    while (colonSplitter.hasNext()) {
        val componentName = colonSplitter.next()
        if (componentName.equals("${context.packageName}/${service.name}", ignoreCase = true)) {
            return true
        }
    }
    return false
}
