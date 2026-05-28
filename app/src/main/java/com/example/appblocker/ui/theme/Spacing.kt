package com.example.appblocker.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppSpacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp,
    val extraExtraLarge: Dp = 80.dp,
    val screenHorizontal: Dp = 16.dp,
    val itemHorizontal: Dp = 20.dp,
    val itemVertical: Dp = 12.dp,
    val itemVerticalLarge: Dp = 16.dp,
    val itemGap: Dp = 12.dp,
    val textGap: Dp = 8.dp
)

val LocalSpacing = androidx.compose.runtime.staticCompositionLocalOf { AppSpacing() }

val androidx.compose.material3.MaterialTheme.spacing: AppSpacing
    @androidx.compose.runtime.Composable
    @androidx.compose.runtime.ReadOnlyComposable
    get() = LocalSpacing.current
