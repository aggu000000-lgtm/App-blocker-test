package com.example.designsystem.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.CatalogCarousel
import com.example.designsystem.components.CatalogFloatingAppBar
import com.example.designsystem.components.CatalogPullToRefresh
import com.example.designsystem.components.CatalogTimePickerDialog

/**
 * Design System Internal UI Catalog Screen
 *
 * This screen serves as the visual catalog and single source of truth for
 * all Expressive components abstracted by the design system.
 */
@Composable
fun CatalogScreen(modifier: Modifier = Modifier) {
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Design System Catalog: Expressive Components", modifier = Modifier.padding(bottom = 16.dp))
        
        CatalogFloatingAppBar {
            Text("Floating App Bar Action", modifier = Modifier.padding(8.dp))
        }
        
        CatalogPullToRefresh(isRefreshing = false, onRefresh = { }) {
            Text("Pull to Refresh Content", modifier = Modifier.padding(8.dp))
        }
        
        CatalogCarousel {
            Text("Carousel Item 1", modifier = Modifier.padding(8.dp))
            Text("Carousel Item 2", modifier = Modifier.padding(8.dp))
        }

        Button(onClick = { showTimePicker = true }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Show Catalog Time Picker")
        }

        if (showTimePicker) {
            CatalogTimePickerDialog(
                initialHour = 9,
                initialMinute = 0,
                onConfirm = { _, _ -> showTimePicker = false },
                onDismiss = { showTimePicker = false }
            )
        }
    }
}
