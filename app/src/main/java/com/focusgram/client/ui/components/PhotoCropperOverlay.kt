package com.focusgram.client.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.focusgram.client.data.CropMath
import com.focusgram.client.data.CustomMediaTarget
import com.focusgram.client.ui.theme.LocalFocusThemeTokens
import java.util.Locale

/**
 * Full-screen interactive pinch-to-zoom & pan photo cropper for Custom Chat Background,
 * Custom Timeline Background, and Custom App Icon.
 */
@Composable
fun PhotoCropperOverlay(
    bitmap: Bitmap,
    target: CustomMediaTarget,
    isSaving: Boolean,
    onCancel: () -> Unit,
    onConfirmCrop: (
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        frameWidth: Float,
        frameHeight: Float
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val tokens = LocalFocusThemeTokens.current
    val imageBitmap = remember(bitmap) { bitmap.asImageBitmap() }
    val density = LocalDensity.current

    var scale by remember(bitmap, target) { mutableFloatStateOf(1f) }
    var offsetX by remember(bitmap, target) { mutableFloatStateOf(0f) }
    var offsetY by remember(bitmap, target) { mutableFloatStateOf(0f) }
    var latestFrameW by remember(bitmap, target) { mutableFloatStateOf(1f) }
    var latestFrameH by remember(bitmap, target) { mutableFloatStateOf(1f) }

    val frameShape = if (target.isSquareIcon) {
        RoundedCornerShape(percent = 24) // iOS Squircle App Icon shape
    } else {
        RoundedCornerShape(26.dp)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF06070D))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Surface(
                    onClick = { if (!isSaving) onCancel() },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.22f))
                ) {
                    Text(
                        text = "Batal",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = target.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Cubit untuk zoom • Geser posisi",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center
                    )
                }

                Surface(
                    onClick = {
                        if (!isSaving) {
                            scale = 1f
                            offsetX = 0f
                            offsetY = 0f
                        }
                    },
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.12f),
                    border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.22f))
                ) {
                    Text(
                        text = "Reset",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            // Interactive Pinch & Pan Viewport
            BoxWithConstraints(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                val maxW = maxWidth
                val maxH = maxHeight

                val (frameWidthDp, frameHeightDp) = if (target.isSquareIcon) {
                    val side = minOf(maxW, maxH).coerceAtMost(300.dp)
                    side to side
                } else {
                    val targetAspect = 9f / 19.2f
                    val availableAspect = if (maxH.value > 0f) maxW.value / maxH.value else targetAspect
                    if (availableAspect > targetAspect) {
                        val h = maxH
                        val w = (h.value * targetAspect).dp
                        w to h
                    } else {
                        val w = maxW
                        val h = (w.value / targetAspect).dp
                        w to h
                    }
                }

                val frameWPx = with(density) { frameWidthDp.toPx() }.coerceAtLeast(1f)
                val frameHPx = with(density) { frameHeightDp.toPx() }.coerceAtLeast(1f)
                latestFrameW = frameWPx
                latestFrameH = frameHPx

                // Gesture surface spans full available area for effortless two-finger pinching
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(bitmap, frameWPx, frameHPx) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val prevScale = scale
                                val newScale = (prevScale * zoom).coerceIn(1f, 5f)
                                val ratio = if (prevScale > 0f) newScale / prevScale else 1f
                                val clamped = CropMath.clampOffset(
                                    offsetX = offsetX * ratio + pan.x,
                                    offsetY = offsetY * ratio + pan.y,
                                    scale = newScale,
                                    imageWidth = bitmap.width.toFloat(),
                                    imageHeight = bitmap.height.toFloat(),
                                    frameWidth = frameWPx,
                                    frameHeight = frameHPx
                                )
                                scale = newScale
                                offsetX = clamped.x
                                offsetY = clamped.y
                            }
                        }
                        .pointerInput(bitmap, frameWPx, frameHPx) {
                            detectTapGestures(
                                onDoubleTap = {
                                    if (scale > 1.15f) {
                                        scale = 1f
                                        offsetX = 0f
                                        offsetY = 0f
                                    } else {
                                        val newScale = 2.2f
                                        val clamped = CropMath.clampOffset(
                                            offsetX = offsetX * newScale,
                                            offsetY = offsetY * newScale,
                                            scale = newScale,
                                            imageWidth = bitmap.width.toFloat(),
                                            imageHeight = bitmap.height.toFloat(),
                                            frameWidth = frameWPx,
                                            frameHeight = frameHPx
                                        )
                                        scale = newScale
                                        offsetX = clamped.x
                                        offsetY = clamped.y
                                    }
                                }
                            )
                        }
                ) {
                    // Clipped Crop Frame Preview
                    Box(
                        modifier = Modifier
                            .size(width = frameWidthDp, height = frameHeightDp)
                            .clip(frameShape)
                            .background(Color(0xFF10131C))
                    ) {
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = target.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scale
                                    scaleY = scale
                                    translationX = offsetX
                                    translationY = offsetY
                                }
                        )

                        // Rule-of-thirds / Icon safe-zone guide lines
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val guideColor = Color.White.copy(alpha = 0.28f)
                            val strokeW = 1.dp.toPx()

                            val thirdW = size.width / 3f
                            val thirdH = size.height / 3f
                            drawLine(guideColor, Offset(thirdW, 0f), Offset(thirdW, size.height), strokeW)
                            drawLine(guideColor, Offset(thirdW * 2f, 0f), Offset(thirdW * 2f, size.height), strokeW)
                            drawLine(guideColor, Offset(0f, thirdH), Offset(size.width, thirdH), strokeW)
                            drawLine(guideColor, Offset(0f, thirdH * 2f), Offset(size.width, thirdH * 2f), strokeW)

                            if (target.isSquareIcon) {
                                // Adaptive circular mask hint inside squircle
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.35f),
                                    radius = size.minDimension * 0.44f,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeW)
                                )
                            }
                        }

                        // Luminous Liquid Glass Crop Frame Rim
                        Surface(
                            shape = frameShape,
                            color = Color.Transparent,
                            border = BorderStroke(1.6.dp, tokens.specularBorderBrush),
                            modifier = Modifier.fillMaxSize()
                        ) {}
                    }
                }
            }

            // Bottom Frosted Liquid Glass Controls (Zoom Slider + Save CTA)
            Surface(
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFF161A26).copy(alpha = 0.88f),
                border = BorderStroke(0.8.dp, tokens.specularBorderBrush),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Zoom (Pinch / Slider)",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White.copy(alpha = 0.86f)
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
                        ) {
                            Text(
                                text = String.format(Locale.US, "%.1fx", scale),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Slider(
                        value = scale,
                        onValueChange = { nextScale ->
                            val prev = scale
                            val ratio = if (prev > 0f) nextScale / prev else 1f
                            val clamped = CropMath.clampOffset(
                                offsetX = offsetX * ratio,
                                offsetY = offsetY * ratio,
                                scale = nextScale,
                                imageWidth = bitmap.width.toFloat(),
                                imageHeight = bitmap.height.toFloat(),
                                frameWidth = latestFrameW,
                                frameHeight = latestFrameH
                            )
                            scale = nextScale
                            offsetX = clamped.x
                            offsetY = clamped.y
                        },
                        valueRange = 1f..5f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Button(
                        onClick = {
                            if (!isSaving) {
                                onConfirmCrop(scale, offsetX, offsetY, latestFrameW, latestFrameH)
                            }
                        },
                        enabled = !isSaving,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Menyimpan Foto...",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        } else {
                            Icon(
                                imageVector = FocusIcons.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Terapkan Foto",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}
