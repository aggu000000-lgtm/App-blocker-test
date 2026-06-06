package com.antigravity.distractionshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.antigravity.distractionshield.theme.DistractionBlockerTheme

class MainActivity : ComponentActivity() {
  private lateinit var blockedAppsManager: BlockedAppsManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    blockedAppsManager = BlockedAppsManager(applicationContext)

    enableEdgeToEdge()
    setContent {
      val themeMode = remember { mutableStateOf(blockedAppsManager.getThemeMode()) }
      val useDynamicColor = remember { mutableStateOf(blockedAppsManager.getUseDynamicColor()) }

      DisposableEffect(blockedAppsManager) {
        val listener = android.content.SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
          if (key == BlockedAppsManager.KEY_THEME_MODE) {
            themeMode.value = blockedAppsManager.getThemeMode()
          } else if (key == BlockedAppsManager.KEY_USE_DYNAMIC_COLOR) {
            useDynamicColor.value = blockedAppsManager.getUseDynamicColor()
          }
        }
        blockedAppsManager.registerListener(listener)
        onDispose {
          blockedAppsManager.unregisterListener(listener)
        }
      }

      DistractionBlockerTheme(
        themeMode = themeMode.value,
        useDynamicColor = useDynamicColor.value
      ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavigation()
        }
      }
    }
  }
}
