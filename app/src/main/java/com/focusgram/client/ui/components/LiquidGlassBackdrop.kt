package com.focusgram.client.ui.components

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.focusgram.client.ui.theme.LocalFocusThemeTokens

/**
 * Renders the authentic iOS 26 Frosted Blur / themed ambient liquid mesh backdrop behind
 * translucent glass surfaces (Settings, Focus Sheet, Overlays).
 * Uses hardware RenderEffect blur on Android 12+ (API 31+) and soft radial gradients on API 26-30.
 */
@Composable
fun LiquidAmbientBackdrop(
    modifier: Modifier = Modifier,
    intensity: Float = 1f,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val tokens = LocalFocusThemeTokens.current
    val baseColor = MaterialTheme.colorScheme.background
    val orbs = tokens.ambientOrbs
    val orbAlpha = (if (tokens.isDark) 0.28f else 0.34f) * intensity.coerceIn(0.2f, 1.4f)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseColor)
    ) {
        val meshModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Modifier
                .fillMaxSize()
                .blur(radius = 52.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
        } else {
            Modifier.fillMaxSize()
        }

        Canvas(modifier = meshModifier) {
            val w = size.width
            val h = size.height
            val c1 = orbs.getOrElse(0) { Color(0xFF007AFF) }
            val c2 = orbs.getOrElse(1) { Color(0xFFAF52DE) }
            val c3 = orbs.getOrElse(2) { Color(0xFFFF2D55) }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(c1.copy(alpha = orbAlpha), Color.Transparent),
                    center = Offset(w * 0.16f, h * 0.14f),
                    radius = w * 0.72f
                ),
                radius = w * 0.72f,
                center = Offset(w * 0.16f, h * 0.14f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(c2.copy(alpha = orbAlpha * 0.92f), Color.Transparent),
                    center = Offset(w * 0.88f, h * 0.36f),
                    radius = w * 0.68f
                ),
                radius = w * 0.68f,
                center = Offset(w * 0.88f, h * 0.36f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(c3.copy(alpha = orbAlpha * 0.85f), Color.Transparent),
                    center = Offset(w * 0.32f, h * 0.84f),
                    radius = w * 0.76f
                ),
                radius = w * 0.76f,
                center = Offset(w * 0.32f, h * 0.84f)
            )
        }

        content()
    }
}
