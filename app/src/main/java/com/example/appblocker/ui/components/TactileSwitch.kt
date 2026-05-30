package com.example.appblocker.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.appblocker.ui.theme.BrandColors

@Composable
fun TactileSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberHaptics()
    
    val motion = com.example.appblocker.ui.theme.LocalMotion.current
    
    val trackColor = if (checked) BrandColors.accentStart else MaterialTheme.colorScheme.surfaceVariant
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 24f else 0f,
        animationSpec = motion.interactiveSpring,
        label = "switchThumb"
    )

    Box(
        modifier = modifier
            .size(width = 52.dp, height = 28.dp)
            .clip(CircleShape)
            .background(trackColor)
            .clickable {
                val newValue = !checked
                if (newValue) haptics.heavyTick() else haptics.lightTick()
                onCheckedChange(newValue)
            }
            .padding(2.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .offset(x = thumbOffset.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
