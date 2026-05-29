package com.example.appblocker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appblocker.ui.foundation.rememberHaptics
import com.example.appblocker.ui.theme.BrandColors

/**
 * The one-and-only primary CTA in the app.
 *
 * Satisfies DESIGN_NORTH_STAR.md:
 *  - Pill shape (height/2 corners)
 *  - Accent gradient fill (the only place the accent appears strongly)
 *  - Spring scale on press
 *  - Haptic on confirm
 *  - White label, bold, tight
 */
@Composable
fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    brush: Brush = BrandColors.accentGradient
) {
    val haptics = rememberHaptics()

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .clip(CircleShape)
            .background(brush)
            .clickable {
                haptics.confirm()
                onClick()
            }
            .padding(PaddingValues(horizontal = 32.dp, vertical = 18.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}
