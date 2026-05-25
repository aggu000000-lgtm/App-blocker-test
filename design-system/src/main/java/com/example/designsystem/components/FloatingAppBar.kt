package com.example.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design System Abstraction: Expressive Floating App Bar
 *
 * MAPPING TO MATERIAL 3 EXPRESSIVE GUIDELINES:
 * This component maps to the conceptual Floating App Bar in Expressive guidelines.
 * It is a dynamic, detached bar that sits at the bottom or top of the screen.
 * 
 * By using this catalog component, feature modules avoid importing the volatile
 * alpha libraries directly, preventing breaking changes if the underlying Google library 
 * updates its signature.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogFloatingAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    tonalElevation: Dp = 3.dp,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        tonalElevation = tonalElevation,
        shadowElevation = tonalElevation,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(content = content)
    }
}
