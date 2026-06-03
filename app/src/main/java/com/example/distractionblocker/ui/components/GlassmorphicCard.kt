package com.example.distractionblocker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.distractionblocker.theme.GlassBase
import com.example.distractionblocker.theme.GlassBorderDark
import com.example.distractionblocker.theme.GlassBorderLight

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassShape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(glassShape)
            // Frosty overlay background
            .background(GlassBase)
            // Thin refractive light-bleed border
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(GlassBorderLight, GlassBorderDark)
                ),
                shape = glassShape
            ),
        content = content
    )
}
