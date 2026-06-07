package com.antigravity.distractionshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.antigravity.distractionshield.theme.DistractionBlockerTheme
import com.antigravity.distractionshield.presentation.main.MainActivityViewModel

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    setContent {
      val viewModel: MainActivityViewModel = viewModel { MainActivityViewModel() }
      val themeSettings by viewModel.themeSettings.collectAsStateWithLifecycle()

      DistractionBlockerTheme(
        themeMode = themeSettings.themeMode,
        useDynamicColor = themeSettings.useDynamicColor
      ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          MainNavigation()
        }
      }
    }
  }
}
