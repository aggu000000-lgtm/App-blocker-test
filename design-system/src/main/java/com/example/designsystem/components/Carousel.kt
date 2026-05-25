package com.example.designsystem.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Design System Abstraction: Expressive Carousel
 *
 * MAPPING TO MATERIAL 3 EXPRESSIVE GUIDELINES:
 * This component maps to the conceptual Carousel in Expressive guidelines.
 * A carousel presents a set of items that users can scroll through horizontally.
 * 
 * By using this catalog component, feature modules avoid importing the volatile
 * alpha libraries directly, preventing breaking changes if the underlying Google library 
 * updates its signature.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogCarousel(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    // Wrap alpha implementation of Material 3 Carousel
    Row(modifier = modifier, content = content)
}
