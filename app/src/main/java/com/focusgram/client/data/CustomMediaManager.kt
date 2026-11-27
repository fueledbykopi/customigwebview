package com.focusgram.client.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.focusgram.client.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Pure, unit-testable coordinate math for the pinch-to-zoom & pan photo cropper.
 *
 * Coordinate convention:
 * - At scale == 1f and offset == (0f, 0f), the source image is centered inside the crop frame
 *   using ContentScale.Crop ("cover") so the frame is 100% filled with zero empty edges.
 * - Positive offsetX/offsetY moves the image right/down inside the viewport.
 */
object CropMath {

    data class ClampedOffset(val x: Float, val y: Float)

    data class SourceCropRect(
        val left: Float,
        val top: Float,
        val right: Float,
        val bottom: Float
    ) {
        val width: Float get() = (right - left).coerceAtLeast(1f)
        val height: Float get() = (bottom - top).coerceAtLeast(1f)
    }

    /**
     * Base display scale factor that makes the source image cover the entire crop frame at zoom 1x.
     */
    fun computeCoverScale(
        imageWidth: Float,
        imageHeight: Float,
        frameWidth: Float,
        frameHeight: Float
    ): Float {
        if (imageWidth <= 0f || imageHeight <= 0f || frameWidth <= 0f || frameHeight <= 0f) {
            return 1f
        }
        return max(frameWidth / imageWidth, frameHeight / imageHeight)
    }

    /**
     * Clamps pan offsets so the zoomed image never separates from any edge of the crop frame.
     */
    fun clampOffset(
        offsetX: Float,
        offsetY: Float,
        scale: Float,
        imageWidth: Float,
        imageHeight: Float,
        frameWidth: Float,
        frameHeight: Float
    ): ClampedOffset {
        val safeScale = scale.coerceAtLeast(1f)
        val coverScale = computeCoverScale(imageWidth, imageHeight, frameWidth, frameHeight)
        val displayedWidth = imageWidth * coverScale * safeScale
        val displayedHeight = imageHeight * coverScale * safeScale

        val maxOffsetX = ((displayedWidth - frameWidth) / 2f).coerceAtLeast(0f)
        val maxOffsetY = ((displayedHeight - frameHeight) / 2f).coerceAtLeast(0f)

        return ClampedOffset(
            x = offsetX.coerceIn(-maxOffsetX, maxOffsetX),
            y = offsetY.coerceIn(-maxOffsetY, maxOffsetY)
        )
    }

    /**
     * Maps the current viewport transform (scale, offset) back to source bitmap pixel coordinates.
     */
    fun computeSourceCropRect(
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        imageWidth: Float,
        imageHeight: Float,
        frameWidth: Float,
        frameHeight: Float
    ): SourceCropRect {
        val safeScale = scale.coerceAtLeast(1f)
        val coverScale = computeCoverScale(imageWidth, imageHeight, frameWidth, frameHeight)
        val totalScale = coverScale * safeScale

        val clamped = clampOffset(
            offsetX = offsetX,
            offsetY = offsetY,
            scale = safeScale,
            imageWidth = imageWidth,
            imageHeight = imageHeight,
            frameWidth = frameWidth,
            frameHeight = frameHeight
        )

        val visibleSourceWidth = (frameWidth / totalScale).coerceAtMost(imageWidth)
        val visibleSourceHeight = (frameHeight / totalScale).coerceAtMost(imageHeight)

        val centerSourceX = (imageWidth / 2f) - (clamped.x / totalScale)
        val centerSourceY = (imageHeight / 2f) - (clamped.y / totalScale)

        val left = (centerSourceX - visibleSourceWidth / 2f)
            .coerceIn(0f, (imageWidth - visibleSourceWidth).coerceAtLeast(0f))
        val top = (centerSourceY - visibleSourceHeight / 2f)
            .coerceIn(0f, (imageHeight - visibleSourceHeight).coerceAtLeast(0f))

        return SourceCropRect(
            left = left,
            top = top,
            right = (left + visibleSourceWidth).coerceAtMost(imageWidth),
            bottom = (top + visibleSourceHeight).coerceAtMost(imageHeight)
        )
    }
}

object CustomMediaManager {

    private const val MEDIA_DIR_NAME = "custom_media"
    private const val MAX_DECODE_DIMENSION = 2048
    private const val MAX_BG_OUTPUT_WIDTH = 1080
    private const val APP_ICON_OUTPUT_SIZE = 512

    suspend fun decodeBitmapFromUri(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, uri)
                ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    val maxSide = max(info.size.width, info.size.height)
                    if (maxSide > MAX_DECODE_DIMENSION) {
                        val ratio = MAX_DECODE_DIMENSION.toFloat() / maxSide.toFloat()
                        decoder.setTargetSize(
                            (info.size.width * ratio).roundToInt().coerceAtLeast(1),
                            (info.size.height * ratio).roundToInt().coerceAtLeast(1)
                        )
                    }
                }
            } else {
                val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, bounds)
                }
                var sampleSize = 1
                val maxSide = max(bounds.outWidth, bounds.outHeight)
                while (maxSide / (sampleSize * 2) >= MAX_DECODE_DIMENSION) {
                    sampleSize *= 2
                }
                val decodeOpts = BitmapFactory.Options().apply {
                    inSampleSize = sampleSize
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream, null, decodeOpts)
                }
            }
        }.getOrNull()
    }

    suspend fun cropAndSave(
        context: Context,
        sourceBitmap: Bitmap,
        target: CustomMediaTarget,
        scale: Float,
        offsetX: Float,
        offsetY: Float,
        frameWidth: Float,
        frameHeight: Float
    ): String? = withContext(Dispatchers.IO) {
        runCatching {
            val cropRect = CropMath.computeSourceCropRect(
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                imageWidth = sourceBitmap.width.toFloat(),
                imageHeight = sourceBitmap.height.toFloat(),
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )

            val outputWidth: Int
            val outputHeight: Int
            if (target.isSquareIcon) {
                outputWidth = APP_ICON_OUTPUT_SIZE
                outputHeight = APP_ICON_OUTPUT_SIZE
            } else {
                val aspect = (frameHeight / frameWidth.coerceAtLeast(1f)).coerceIn(0.5f, 3f)
                outputWidth = MAX_BG_OUTPUT_WIDTH
                outputHeight = (MAX_BG_OUTPUT_WIDTH * aspect).roundToInt().coerceIn(720, 2400)
            }

            val outputBitmap = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(outputBitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
            val matrix = Matrix().apply {
                postTranslate(-cropRect.left, -cropRect.top)
                postScale(
                    outputWidth.toFloat() / cropRect.width,
                    outputHeight.toFloat() / cropRect.height
                )
            }
            canvas.drawBitmap(sourceBitmap, matrix, paint)

            val dir = File(context.filesDir, MEDIA_DIR_NAME).apply { mkdirs() }
            // Clean previous files for this target so disk usage stays tiny
            dir.listFiles()?.forEach { existing ->
                if (existing.name.startsWith(target.filePrefix)) {
                    existing.delete()
                }
            }

            val ext = if (target.isSquareIcon) "png" else "jpg"
            val outFile = File(dir, "${target.filePrefix}${System.currentTimeMillis()}.$ext")
            FileOutputStream(outFile).use { fos ->
                if (target.isSquareIcon) {
                    outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                } else {
                    outputBitmap.compress(Bitmap.CompressFormat.JPEG, 88, fos)
                }
            }
            outputBitmap.recycle()
            outFile.absolutePath
        }.getOrNull()
    }

    suspend fun loadBitmapFromPath(path: String): Bitmap? = withContext(Dispatchers.IO) {
        if (path.isBlank()) return@withContext null
        runCatching {
            val file = File(path)
            if (!file.exists()) return@withContext null
            BitmapFactory.decodeFile(file.absolutePath)
        }.getOrNull()
    }

    suspend fun deleteFile(path: String) = withContext(Dispatchers.IO) {
        if (path.isBlank()) return@withContext
        runCatching {
            val file = File(path)
            if (file.exists()) file.delete()
        }
    }

    /**
     * Requests the Android launcher to pin a custom Home Screen app icon using the user's
     * cropped photo (Android 8.0+ / API 26+).
     */
    fun pinCustomAppIconShortcut(
        context: Context,
        iconBitmap: Bitmap,
        label: String = "FocusGram"
    ): Boolean {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            return false
        }
        return runCatching {
            // Prepare a padded adaptive bitmap (108dp canvas with 72dp visible safe zone,
            // scaled slightly larger so full-bleed photos look crisp inside any launcher mask)
            val adaptiveSize = 432
            val adaptiveBitmap = Bitmap.createBitmap(adaptiveSize, adaptiveSize, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(adaptiveBitmap)
            val scaled = Bitmap.createScaledBitmap(iconBitmap, adaptiveSize, adaptiveSize, true)
            canvas.drawBitmap(scaled, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG))
            if (scaled !== iconBitmap) {
                scaled.recycle()
            }

            val launchIntent = Intent(context, MainActivity::class.java).apply {
                action = Intent.ACTION_MAIN
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            val shortcut = ShortcutInfoCompat.Builder(
                context,
                "focusgram_custom_icon_${System.currentTimeMillis()}"
            )
                .setShortLabel(label.ifBlank { "FocusGram" })
                .setLongLabel(label.ifBlank { "FocusGram" })
                .setIcon(IconCompat.createWithAdaptiveBitmap(adaptiveBitmap))
                .setIntent(launchIntent)
                .build()

            ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
        }.getOrDefault(false)
    }
}
