package com.example.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.painter.Painter

data class AppIdentity(
    val appName: String,
    val appLogo: @Composable () -> Painter
)

val LocalAppIdentity: ProvidableCompositionLocal<AppIdentity> = staticCompositionLocalOf {
    error("No AppIdentity provided")
}
