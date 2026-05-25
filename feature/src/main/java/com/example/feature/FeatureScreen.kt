package com.example.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.components.CatalogFloatingAppBar
import com.example.designsystem.components.CatalogPullToRefresh

@Composable
fun FeatureScreen() {
    Column {
        CatalogPullToRefresh(isRefreshing = false, onRefresh = {}) {
            // Feature content
        }
        
        CatalogFloatingAppBar {
            // Feature actions
        }
    }
}
