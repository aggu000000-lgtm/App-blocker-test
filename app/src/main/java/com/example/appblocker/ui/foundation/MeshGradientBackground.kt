package com.example.appblocker.ui.foundation

import android.os.Build
import android.graphics.ComposeShader
import android.graphics.LinearGradient
import android.graphics.PorterDuff
import android.graphics.RadialGradient
import android.graphics.RuntimeShader
import android.graphics.Shader
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import com.example.appblocker.ui.theme.BrandColors
import com.example.appblocker.ui.theme.LocalMotion
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun rememberMeshGradientProgress(): Float {
    val isPowerSaveMode = rememberPowerSaveMode()
    val isReduceMotion = rememberIsReduceMotion()
    val isVisible = rememberIsVisible()
    val shouldAnimate = isVisible && !isReduceMotion

    val dynamism = LocalDynamism.current

    var progress by remember { mutableFloatStateOf(0f) }

    val targetSpeed = if (isPowerSaveMode) {
        1f / DynamismLevel.Ambient.durationSeconds
    } else {
        1f / dynamism.durationSeconds
    }

    val currentSpeed by animateFloatAsState(
        targetValue = targetSpeed,
        animationSpec = tween(5000),
        label = "gradientSpeed"
    )

    LaunchedEffect(shouldAnimate) {
        if (shouldAnimate) {
            var lastTime = withFrameNanos { it }
            while (true) {
                val currentTime = withFrameNanos { it }
                val dt = (currentTime - lastTime) / 1_000_000_000f // seconds
                lastTime = currentTime
                
                // Requirement 1: no wrapping to prevent repeating patterns
                progress += dt * currentSpeed
            }
        }
    }
    
    return progress
}

private fun deriveHarmoniousPalette(baseAccent: Color): List<Color> {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(baseAccent.toArgb(), hsl)
    val h = hsl[0]
    val s = hsl[1]
    val l = hsl[2]

    return listOf(
        baseAccent,
        Color(ColorUtils.HSLToColor(floatArrayOf((h + 15) % 360, s, Math.min(1f, l + 0.1f)))),
        Color(ColorUtils.HSLToColor(floatArrayOf((h - 15 + 360) % 360, s, Math.max(0f, l - 0.1f)))),
        Color(ColorUtils.HSLToColor(floatArrayOf((h + 30) % 360, Math.min(1f, s + 0.1f), l))),
        Color(ColorUtils.HSLToColor(floatArrayOf((h - 30 + 360) % 360, Math.max(0f, s - 0.1f), l)))
    )
}

val FBM_SHADER_SRC = """
    uniform float2 resolution;
    uniform float time;
    
    uniform half4 color1;
    uniform half4 color2;
    uniform half4 color3;
    uniform half4 color4;
    uniform half4 color5;
    uniform half4 baseColor;

    float random(float2 st) {
        return fract(sin(dot(st.xy, float2(12.9898, 78.233))) * 43758.5453123);
    }

    float noise(float2 st) {
        float2 i = floor(st);
        float2 f = fract(st);

        float a = random(i);
        float b = random(i + float2(1.0, 0.0));
        float c = random(i + float2(0.0, 1.0));
        float d = random(i + float2(1.0, 1.0));

        float2 u = f * f * (3.0 - 2.0 * f);

        return mix(a, b, u.x) +
                (c - a) * u.y * (1.0 - u.x) +
                (d - b) * u.x * u.y;
    }

    float fbm(float2 st) {
        float value = 0.0;
        float amplitude = 0.5;
        for (int i = 0; i < 4; i++) {
            value += amplitude * noise(st);
            st *= 2.0;
            amplitude *= 0.5;
        }
        return value;
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord.xy / resolution.xy;
        uv.x *= resolution.x / resolution.y; // Correct aspect ratio stretching
        
        float2 q = float2(0.0);
        q.x = fbm(uv + 0.02 * time);
        q.y = fbm(uv + float2(1.0));

        float2 r = float2(0.0);
        r.x = fbm(uv + 1.0 * q + float2(1.7, 9.2) + 0.15 * time);
        r.y = fbm(uv + 1.0 * q + float2(8.3, 2.8) + 0.126 * time);

        float f = fbm(uv + r);

        // Map f (0.0 to ~1.0) to 5 color stops
        half4 c = mix(color1, color2, clamp((f - 0.0) * 4.0, 0.0, 1.0));
        c = mix(c, color3, clamp((f - 0.25) * 4.0, 0.0, 1.0));
        c = mix(c, color4, clamp((f - 0.5) * 4.0, 0.0, 1.0));
        c = mix(c, color5, clamp((f - 0.75) * 4.0, 0.0, 1.0));
        
        // SRC_OVER blend with base color to avoid pure white/black and act as transition
        float alpha = c.a;
        half4 outColor;
        outColor.rgb = (c.rgb * alpha) + (baseColor.rgb * (1.0 - alpha));
        outColor.a = 1.0;
        
        return outColor;
    }
"""

fun Modifier.meshGradientBackground(base: Color): Modifier = composed {
    val t = rememberMeshGradientProgress()
    val isHighFidelity = LocalDynamism.current == DynamismLevel.HighFidelity

    val highFidelityAlpha by animateFloatAsState(
        targetValue = if (isHighFidelity) 1f else 0f,
        animationSpec = tween(5000),
        label = "highFidelityAlpha"
    )

    // Requirement 2: derive a palette of at least 5 harmonious tonal 'stops'
    // Requirement 5: morph smoothly when the primary accent color changes
    val baseAccent = MaterialTheme.colorScheme.primary
    val palette = remember(baseAccent) { deriveHarmoniousPalette(baseAccent) }
    
    // Dynamic alpha based on fidelity and ambient state
    val baseAlpha = 0.15f + (0.10f * highFidelityAlpha)

    val c1 by animateColorAsState(targetValue = palette[0].copy(alpha = baseAlpha), animationSpec = tween(2000), label = "c1")
    val c2 by animateColorAsState(targetValue = palette[1].copy(alpha = baseAlpha), animationSpec = tween(2000), label = "c2")
    val c3 by animateColorAsState(targetValue = palette[2].copy(alpha = baseAlpha), animationSpec = tween(2000), label = "c3")
    val c4 by animateColorAsState(targetValue = palette[3].copy(alpha = baseAlpha), animationSpec = tween(2000), label = "c4")
    val c5 by animateColorAsState(targetValue = palette[4].copy(alpha = baseAlpha), animationSpec = tween(2000), label = "c5")

    val runtimeShader = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            RuntimeShader(FBM_SHADER_SRC)
        } else {
            null
        }
    }

    this
        .fillMaxSize()
        .drawBehind {
            val w = size.width
            val h = size.height

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && runtimeShader != null) {
                runtimeShader.setFloatUniform("resolution", w, h)
                runtimeShader.setFloatUniform("time", t)
                
                // Set color stops
                runtimeShader.setColorUniform("color1", c1.toArgb())
                runtimeShader.setColorUniform("color2", c2.toArgb())
                runtimeShader.setColorUniform("color3", c3.toArgb())
                runtimeShader.setColorUniform("color4", c4.toArgb())
                runtimeShader.setColorUniform("color5", c5.toArgb())
                runtimeShader.setColorUniform("baseColor", base.toArgb())

                drawRect(brush = ShaderBrush(runtimeShader))
            } else {
                // Fallback for older APIs using previous geometric movement
                val twoPi = (2f * Math.PI).toFloat()
                
                // Wrap t for the fallback so it doesn't cause floating point issues in trig functions
                val wrappedT = t % 1f

                val center1 = Offset(
                    x = w * (0.25f + 0.10f * cos(wrappedT * twoPi)),
                    y = h * (0.20f + 0.08f * sin(wrappedT * twoPi))
                )
                val rad1 = maxOf(w, h) * 0.65f

                val center2 = Offset(
                    x = w * (0.80f + 0.10f * cos(wrappedT * twoPi + Math.PI.toFloat())),
                    y = h * (0.80f + 0.08f * sin(wrappedT * twoPi + Math.PI.toFloat()))
                )
                val rad2 = maxOf(w, h) * 0.55f
                
                val center3 = Offset(
                    x = w * (0.50f + 0.15f * cos(wrappedT * twoPi * 1.5f)),
                    y = h * (0.50f + 0.15f * sin(wrappedT * twoPi * 1.5f))
                )
                val rad3 = maxOf(w, h) * 0.50f

                val center4 = Offset(
                    x = w * (0.20f + 0.20f * cos(wrappedT * twoPi * 0.8f + Math.PI.toFloat())),
                    y = h * (0.80f + 0.10f * sin(wrappedT * twoPi * 0.8f + Math.PI.toFloat()))
                )
                val rad4 = maxOf(w, h) * 0.60f

                val gradient1 = RadialGradient(
                    center1.x, center1.y, rad1,
                    intArrayOf(BrandColors.accentHalo.copy(alpha = 0.18f).toArgb(), android.graphics.Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
                
                val gradient2 = RadialGradient(
                    center2.x, center2.y, rad2,
                    intArrayOf(BrandColors.accentEnd.copy(alpha = 0.12f).toArgb(), android.graphics.Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
                
                val gradient3 = RadialGradient(
                    center3.x, center3.y, rad3,
                    intArrayOf(Color(0xFF56CCF2).copy(alpha = 0.15f * highFidelityAlpha).toArgb(), android.graphics.Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
                
                val gradient4 = RadialGradient(
                    center4.x, center4.y, rad4,
                    intArrayOf(Color(0xFF9B51E0).copy(alpha = 0.15f * highFidelityAlpha).toArgb(), android.graphics.Color.TRANSPARENT),
                    null,
                    Shader.TileMode.CLAMP
                )
                
                val baseShader = LinearGradient(
                    0f, 0f, 0f, 10f,
                    intArrayOf(base.toArgb(), base.toArgb()),
                    null,
                    Shader.TileMode.CLAMP
                )

                val halos12 = ComposeShader(gradient2, gradient1, PorterDuff.Mode.ADD)
                val halos34 = ComposeShader(gradient4, gradient3, PorterDuff.Mode.ADD)
                val combinedHalos = ComposeShader(halos12, halos34, PorterDuff.Mode.ADD)
                
                val finalShader = ComposeShader(baseShader, combinedHalos, PorterDuff.Mode.SRC_OVER)

                drawRect(brush = ShaderBrush(finalShader))
            }
        }
}
