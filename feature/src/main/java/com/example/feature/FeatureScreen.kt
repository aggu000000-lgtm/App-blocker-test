package com.example.feature

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.designsystem.components.CatalogFloatingAppBar
import com.example.designsystem.components.CatalogPullToRefresh

@Composable
fun FeatureScreen(viewModel: FeatureViewModel = hiltViewModel()) {
    Column {
        CatalogPullToRefresh(isRefreshing = false, onRefresh = {}) {
            // Use viewModel to verify it's active
            println(viewModel.message)
        }
        
        CatalogFloatingAppBar {
            // Feature actions
        }
    }
}
