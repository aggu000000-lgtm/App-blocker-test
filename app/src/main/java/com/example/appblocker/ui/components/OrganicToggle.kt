package com.example.appblocker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.appblocker.ui.foundation.rememberHaptics
import androidx.compose.ui.semantics.Role

@Composable
fun OrganicToggle(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val haptics = rememberHaptics()
    val width = 52.dp
    val height = 32.dp
    val thumbSize = 24.dp
    val padding = 4.dp

    val trackColor by animateColorAsState(
        targetValue = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        label = "trackColor"
    )

    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.6f),
        label = "thumbOffset"
    )

    val maxOffset = width - thumbSize - padding * 2
    val offsetX = maxOffset * thumbOffset

    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(CircleShape)
            .background(trackColor)
            .clickable(role = Role.Switch) {
                if (onCheckedChange != null) {
                    val newValue = !checked
                    if (newValue) {
                        haptics.toggleOn()
                    } else {
                        haptics.toggleOff()
                    }
                    onCheckedChange(newValue)
                }
            }
            .padding(padding),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = offsetX)
                .size(thumbSize)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
