package com.example.designsystem.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.CatalogCarousel
import com.example.designsystem.components.CatalogFloatingAppBar
import com.example.designsystem.components.CatalogPullToRefresh

/**
 * Design System Internal UI Catalog Screen
 *
 * This screen serves as the visual catalog and single source of truth for
 * all Expressive components abstracted by the design system.
 */
@Composable
fun CatalogScreen(modifier: Modifier = Modifier) {
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
    }
}
