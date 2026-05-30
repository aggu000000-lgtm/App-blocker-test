package com.example.appblocker.ui.home

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appblocker.ui.components.PillButton
import com.example.appblocker.ui.foundation.meshGradientBackground

/**
 * Reference implementation of the DESIGN_NORTH_STAR.md spec.
 *
 * - No card chrome anywhere
 * - One hero number (focus minutes today), animated count-up on first appear
 * - Slow ambient mesh-gradient background (Law 2)
 * - One primary CTA, pill shape, accent gradient (the only strong accent usage)
 * - All copy is whisper-quiet: tight display, calm body, single muted subtitle
 * - Light + dark adapt via the brand theme tokens; no hardcoded colors
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onStartFocusSession: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start
        ) {
            // ── Header eyebrow ────────────────────────────────────────────
            Column {
                Text(
                    text = "TODAY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Stay with it.",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // ── Hero block ────────────────────────────────────────────────
            HeroFocusNumber(targetMinutes = 47)

            // ── CTA + caption ─────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.Start) {
                PillButton(
                    text = "Start focus session",
                    onClick = onStartFocusSession,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Distractions go quiet for 25 minutes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

/**
 * Big quiet number: counts up from 0 to [targetMinutes] on first composition,
 * then sits still. Tight letter spacing, heavy weight — Robinhood-style.
 */
@Composable
private fun HeroFocusNumber(targetMinutes: Int) {
    var trigger by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { trigger = targetMinutes }

    val motion = com.example.appblocker.ui.theme.LocalMotion.current
    val count by animateIntAsState(
        targetValue = trigger,
        animationSpec = motion.interactiveSpringInt,
        label = "heroCount"
    )

    Column {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "focused minutes",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
