package com.focusgram.client.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Lightweight SF-Symbols-inspired vector icons built lazily as singletons.
 * Replaces the ~35 MB material-icons-extended artifact to minimize APK size,
 * DEX count, CI build time, and runtime memory usage.
 */
object FocusIcons {

    private fun strokeIcon(
        name: String,
        strokeWidth: Float = 1.9f,
        block: PathBuilder.() -> Unit
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = strokeWidth,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
            pathBuilder = block
        )
    }.build()

    private fun filledIcon(
        name: String,
        block: PathBuilder.() -> Unit
    ): ImageVector = ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            pathFillType = PathFillType.NonZero,
            pathBuilder = block
        )
    }.build()

    val HomeOutline: ImageVector by lazy {
        strokeIcon("HomeOutline") {
            moveTo(3.5f, 10.8f)
            lineTo(12f, 3.8f)
            lineTo(20.5f, 10.8f)
            moveTo(5.8f, 9.2f)
            verticalLineTo(19.2f)
            curveTo(5.8f, 19.9f, 6.4f, 20.5f, 7.1f, 20.5f)
            horizontalLineTo(10f)
            verticalLineTo(15.2f)
            curveTo(10f, 14.5f, 10.6f, 14f, 11.2f, 14f)
            horizontalLineTo(12.8f)
            curveTo(13.4f, 14f, 14f, 14.5f, 14f, 15.2f)
            verticalLineTo(20.5f)
            horizontalLineTo(16.9f)
            curveTo(17.6f, 20.5f, 18.2f, 19.9f, 18.2f, 19.2f)
            verticalLineTo(9.2f)
        }
    }

    val HomeFilled: ImageVector by lazy {
        filledIcon("HomeFilled") {
            moveTo(11.3f, 3.3f)
            curveTo(11.7f, 2.95f, 12.3f, 2.95f, 12.7f, 3.3f)
            lineTo(21.1f, 10.3f)
            curveTo(21.6f, 10.7f, 21.3f, 11.5f, 20.7f, 11.5f)
            horizontalLineTo(19.2f)
            verticalLineTo(19.2f)
            curveTo(19.2f, 20.2f, 18.4f, 21f, 17.4f, 21f)
            horizontalLineTo(14.5f)
            curveTo(14.1f, 21f, 13.8f, 20.7f, 13.8f, 20.3f)
            verticalLineTo(15.5f)
            curveTo(13.8f, 14.9f, 13.3f, 14.4f, 12.7f, 14.4f)
            horizontalLineTo(11.3f)
            curveTo(10.7f, 14.4f, 10.2f, 14.9f, 10.2f, 15.5f)
            verticalLineTo(20.3f)
            curveTo(10.2f, 20.7f, 9.9f, 21f, 9.5f, 21f)
            horizontalLineTo(6.6f)
            curveTo(5.6f, 21f, 4.8f, 20.2f, 4.8f, 19.2f)
            verticalLineTo(11.5f)
            horizontalLineTo(3.3f)
            curveTo(2.7f, 11.5f, 2.4f, 10.7f, 2.9f, 10.3f)
            close()
        }
    }

    val SearchOutline: ImageVector by lazy {
        strokeIcon("SearchOutline") {
            moveTo(10.8f, 4.2f)
            arcTo(6.6f, 6.6f, 0f, true, false, 10.8f, 17.4f)
            arcTo(6.6f, 6.6f, 0f, true, false, 10.8f, 4.2f)
            moveTo(15.7f, 15.7f)
            lineTo(20.2f, 20.2f)
        }
    }

    val SearchFilled: ImageVector by lazy {
        strokeIcon("SearchFilled", strokeWidth = 2.4f) {
            moveTo(10.8f, 4.2f)
            arcTo(6.6f, 6.6f, 0f, true, false, 10.8f, 17.4f)
            arcTo(6.6f, 6.6f, 0f, true, false, 10.8f, 4.2f)
            moveTo(15.7f, 15.7f)
            lineTo(20.2f, 20.2f)
        }
    }

    val PaperplaneOutline: ImageVector by lazy {
        strokeIcon("PaperplaneOutline") {
            moveTo(20.6f, 3.8f)
            lineTo(3.6f, 10.2f)
            lineTo(10.2f, 13.3f)
            lineTo(13.3f, 20f)
            lineTo(20.6f, 3.8f)
            close()
            moveTo(20.2f, 4.2f)
            lineTo(10.2f, 13.3f)
        }
    }

    val PaperplaneFilled: ImageVector by lazy {
        filledIcon("PaperplaneFilled") {
            moveTo(21.2f, 3.5f)
            curveTo(21.5f, 2.8f, 20.8f, 2.1f, 20.1f, 2.4f)
            lineTo(3.1f, 9.2f)
            curveTo(2.3f, 9.5f, 2.3f, 10.6f, 3.1f, 11f)
            lineTo(9.7f, 14f)
            lineTo(12.7f, 20.6f)
            curveTo(13.1f, 21.4f, 14.2f, 21.4f, 14.5f, 20.6f)
            close()
        }
    }

    val BellOutline: ImageVector by lazy {
        strokeIcon("BellOutline") {
            moveTo(18f, 9.5f)
            curveTo(18f, 6.2f, 15.3f, 3.8f, 12f, 3.8f)
            curveTo(8.7f, 3.8f, 6f, 6.2f, 6f, 9.5f)
            curveTo(6f, 13.8f, 4.3f, 15.6f, 3.8f, 16.4f)
            curveTo(3.5f, 16.9f, 3.8f, 17.5f, 4.4f, 17.5f)
            horizontalLineTo(19.6f)
            curveTo(20.2f, 17.5f, 20.5f, 16.9f, 20.2f, 16.4f)
            curveTo(19.7f, 15.6f, 18f, 13.8f, 18f, 9.5f)
            close()
            moveTo(9.8f, 20.2f)
            curveTo(10.4f, 20.9f, 11.2f, 21.2f, 12f, 21.2f)
            curveTo(12.8f, 21.2f, 13.6f, 20.9f, 14.2f, 20.2f)
        }
    }

    val BellFilled: ImageVector by lazy {
        filledIcon("BellFilled") {
            moveTo(12f, 3f)
            curveTo(8.2f, 3f, 5.4f, 5.8f, 5.4f, 9.5f)
            curveTo(5.4f, 13.4f, 3.9f, 15.1f, 3.2f, 16.1f)
            curveTo(2.7f, 16.8f, 3.2f, 18f, 4.2f, 18f)
            horizontalLineTo(19.8f)
            curveTo(20.8f, 18f, 21.3f, 16.8f, 20.8f, 16.1f)
            curveTo(20.1f, 15.1f, 18.6f, 13.4f, 18.6f, 9.5f)
            curveTo(18.6f, 5.8f, 15.8f, 3f, 12f, 3f)
            close()
            moveTo(9.4f, 19.3f)
            curveTo(9.8f, 20.6f, 10.8f, 21.5f, 12f, 21.5f)
            curveTo(13.2f, 21.5f, 14.2f, 20.6f, 14.6f, 19.3f)
            close()
        }
    }

    val PersonOutline: ImageVector by lazy {
        strokeIcon("PersonOutline") {
            moveTo(12f, 4.2f)
            arcTo(3.8f, 3.8f, 0f, true, false, 12f, 11.8f)
            arcTo(3.8f, 3.8f, 0f, true, false, 12f, 4.2f)
            moveTo(4.8f, 19.8f)
            curveTo(5.6f, 16.6f, 8.4f, 14.8f, 12f, 14.8f)
            curveTo(15.6f, 14.8f, 18.4f, 16.6f, 19.2f, 19.8f)
        }
    }

    val PersonFilled: ImageVector by lazy {
        filledIcon("PersonFilled") {
            moveTo(12f, 3.6f)
            arcTo(4.2f, 4.2f, 0f, true, false, 12f, 12f)
            arcTo(4.2f, 4.2f, 0f, true, false, 12f, 3.6f)
            close()
            moveTo(4.2f, 19.6f)
            curveTo(4.2f, 16.1f, 7.6f, 14f, 12f, 14f)
            curveTo(16.4f, 14f, 19.8f, 16.1f, 19.8f, 19.6f)
            curveTo(19.8f, 20.4f, 19.2f, 21f, 18.4f, 21f)
            horizontalLineTo(5.6f)
            curveTo(4.8f, 21f, 4.2f, 20.4f, 4.2f, 19.6f)
            close()
        }
    }

    val ChevronLeft: ImageVector by lazy {
        strokeIcon("ChevronLeft", strokeWidth = 2.2f) {
            moveTo(15f, 5.5f)
            lineTo(8.5f, 12f)
            lineTo(15f, 18.5f)
        }
    }

    val Shield: ImageVector by lazy {
        strokeIcon("Shield") {
            moveTo(12f, 3.2f)
            lineTo(19f, 6.2f)
            verticalLineTo(11.6f)
            curveTo(19f, 16.1f, 15.9f, 19.4f, 12f, 21f)
            curveTo(8.1f, 19.4f, 5f, 16.1f, 5f, 11.6f)
            verticalLineTo(6.2f)
            close()
        }
    }

    val ShieldFilled: ImageVector by lazy {
        filledIcon("ShieldFilled") {
            moveTo(12f, 2.8f)
            lineTo(19.5f, 5.9f)
            verticalLineTo(11.6f)
            curveTo(19.5f, 16.4f, 16.2f, 19.9f, 12f, 21.5f)
            curveTo(7.8f, 19.9f, 4.5f, 16.4f, 4.5f, 11.6f)
            verticalLineTo(5.9f)
            close()
        }
    }

    val Refresh: ImageVector by lazy {
        strokeIcon("Refresh") {
            moveTo(19.2f, 12f)
            arcTo(7.2f, 7.2f, 0f, true, true, 17.1f, 6.9f)
            lineTo(19.5f, 9.2f)
            moveTo(19.5f, 4.8f)
            verticalLineTo(9.2f)
            horizontalLineTo(15.1f)
        }
    }

    val Ellipsis: ImageVector by lazy {
        filledIcon("Ellipsis") {
            moveTo(6.2f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, false, 6.2f, 13.5f)
            arcTo(1.5f, 1.5f, 0f, true, false, 6.2f, 10.5f)
            close()
            moveTo(12f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, false, 12f, 13.5f)
            arcTo(1.5f, 1.5f, 0f, true, false, 12f, 10.5f)
            close()
            moveTo(17.8f, 10.5f)
            arcTo(1.5f, 1.5f, 0f, true, false, 17.8f, 13.5f)
            arcTo(1.5f, 1.5f, 0f, true, false, 17.8f, 10.5f)
            close()
        }
    }

    val Copy: ImageVector by lazy {
        strokeIcon("Copy") {
            moveTo(9.5f, 8.5f)
            horizontalLineTo(18.5f)
            curveTo(19.3f, 8.5f, 20f, 9.2f, 20f, 10f)
            verticalLineTo(19f)
            curveTo(20f, 19.8f, 19.3f, 20.5f, 18.5f, 20.5f)
            horizontalLineTo(9.5f)
            curveTo(8.7f, 20.5f, 8f, 19.8f, 8f, 19f)
            verticalLineTo(10f)
            curveTo(8f, 9.2f, 8.7f, 8.5f, 9.5f, 8.5f)
            close()
            moveTo(16f, 8.5f)
            verticalLineTo(5.5f)
            curveTo(16f, 4.7f, 15.3f, 4f, 14.5f, 4f)
            horizontalLineTo(5.5f)
            curveTo(4.7f, 4f, 4f, 4.7f, 4f, 5.5f)
            verticalLineTo(14.5f)
            curveTo(4f, 15.3f, 4.7f, 16f, 5.5f, 16f)
            horizontalLineTo(8f)
        }
    }

    val Share: ImageVector by lazy {
        // Classic iOS Share (box with upward arrow)
        strokeIcon("Share") {
            moveTo(12f, 15f)
            verticalLineTo(4f)
            moveTo(8.2f, 7.8f)
            lineTo(12f, 4f)
            lineTo(15.8f, 7.8f)
            moveTo(6f, 11.2f)
            horizontalLineTo(5.5f)
            curveTo(4.7f, 11.2f, 4f, 11.9f, 4f, 12.7f)
            verticalLineTo(19f)
            curveTo(4f, 19.8f, 4.7f, 20.5f, 5.5f, 20.5f)
            horizontalLineTo(18.5f)
            curveTo(19.3f, 20.5f, 20f, 19.8f, 20f, 19f)
            verticalLineTo(12.7f)
            curveTo(20f, 11.9f, 19.3f, 11.2f, 18.5f, 11.2f)
            horizontalLineTo(18f)
        }
    }

    val Compass: ImageVector by lazy {
        strokeIcon("Compass") {
            moveTo(12f, 3.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 20.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 3.5f)
            moveTo(15.6f, 8.4f)
            lineTo(13.6f, 13.6f)
            lineTo(8.4f, 15.6f)
            lineTo(10.4f, 10.4f)
            close()
        }
    }

    val Gear: ImageVector by lazy {
        strokeIcon("Gear") {
            moveTo(12f, 8.8f)
            arcTo(3.2f, 3.2f, 0f, true, false, 12f, 15.2f)
            arcTo(3.2f, 3.2f, 0f, true, false, 12f, 8.8f)
            moveTo(12f, 3.2f)
            verticalLineTo(5.2f)
            moveTo(12f, 18.8f)
            verticalLineTo(20.8f)
            moveTo(3.2f, 12f)
            horizontalLineTo(5.2f)
            moveTo(18.8f, 12f)
            horizontalLineTo(20.8f)
            moveTo(5.8f, 5.8f)
            lineTo(7.2f, 7.2f)
            moveTo(16.8f, 16.8f)
            lineTo(18.2f, 18.2f)
            moveTo(18.2f, 5.8f)
            lineTo(16.8f, 7.2f)
            moveTo(7.2f, 16.8f)
            lineTo(5.8f, 18.2f)
        }
    }

    val ReelsOff: ImageVector by lazy {
        strokeIcon("ReelsOff") {
            moveTo(4f, 4f)
            lineTo(20f, 20f)
            moveTo(16f, 10.5f)
            lineTo(20.5f, 8f)
            verticalLineTo(16f)
            lineTo(16f, 13.5f)
            moveTo(8.5f, 6.5f)
            horizontalLineTo(14.2f)
            curveTo(15.2f, 6.5f, 16f, 7.3f, 16f, 8.3f)
            verticalLineTo(12f)
            moveTo(15.5f, 17.5f)
            horizontalLineTo(5.8f)
            curveTo(4.8f, 17.5f, 4f, 16.7f, 4f, 15.7f)
            verticalLineTo(8.3f)
            curveTo(4f, 7.7f, 4.3f, 7.2f, 4.7f, 6.9f)
        }
    }

    val ExploreOff: ImageVector by lazy {
        strokeIcon("ExploreOff") {
            moveTo(4f, 4f)
            lineTo(20f, 20f)
            moveTo(5f, 5f)
            horizontalLineTo(10f)
            verticalLineTo(10f)
            horizontalLineTo(5f)
            close()
            moveTo(14f, 5f)
            horizontalLineTo(19f)
            verticalLineTo(10f)
            horizontalLineTo(14f)
            close()
            moveTo(5f, 14f)
            horizontalLineTo(10f)
            verticalLineTo(19f)
            horizontalLineTo(5f)
            close()
        }
    }

    val EyeSlash: ImageVector by lazy {
        strokeIcon("EyeSlash") {
            moveTo(3.5f, 3.5f)
            lineTo(20.5f, 20.5f)
            moveTo(10.6f, 6.2f)
            curveTo(11.1f, 6.1f, 11.5f, 6f, 12f, 6f)
            curveTo(16.6f, 6f, 20f, 9.6f, 21f, 12f)
            curveTo(20.5f, 13.2f, 19.6f, 14.5f, 18.4f, 15.6f)
            moveTo(14.5f, 17.6f)
            curveTo(13.7f, 17.9f, 12.9f, 18f, 12f, 18f)
            curveTo(7.4f, 18f, 4f, 14.4f, 3f, 12f)
            curveTo(3.8f, 10.2f, 5.4f, 8.2f, 7.6f, 7f)
        }
    }

    val Phone: ImageVector by lazy {
        strokeIcon("Phone") {
            moveTo(8f, 3.2f)
            horizontalLineTo(16f)
            curveTo(17.1f, 3.2f, 18f, 4.1f, 18f, 5.2f)
            verticalLineTo(18.8f)
            curveTo(18f, 19.9f, 17.1f, 20.8f, 16f, 20.8f)
            horizontalLineTo(8f)
            curveTo(6.9f, 20.8f, 6f, 19.9f, 6f, 18.8f)
            verticalLineTo(5.2f)
            curveTo(6f, 4.1f, 6.9f, 3.2f, 8f, 3.2f)
            close()
            moveTo(10.8f, 17.8f)
            horizontalLineTo(13.2f)
        }
    }

    val Stories: ImageVector by lazy {
        strokeIcon("Stories") {
            moveTo(12f, 4.5f)
            arcTo(7.5f, 7.5f, 0f, true, false, 12f, 19.5f)
            arcTo(7.5f, 7.5f, 0f, true, false, 12f, 4.5f)
            moveTo(12f, 8f)
            arcTo(4f, 4f, 0f, true, false, 12f, 16f)
            arcTo(4f, 4f, 0f, true, false, 12f, 8f)
        }
    }

    val HeartOutline: ImageVector by lazy {
        strokeIcon("HeartOutline") {
            moveTo(12f, 20f)
            curveTo(12f, 20f, 4f, 15.1f, 4f, 9.4f)
            curveTo(4f, 6.6f, 6.1f, 4.5f, 8.6f, 4.5f)
            curveTo(10.2f, 4.5f, 11.4f, 5.3f, 12f, 6.5f)
            curveTo(12.6f, 5.3f, 13.8f, 4.5f, 15.4f, 4.5f)
            curveTo(17.9f, 4.5f, 20f, 6.6f, 20f, 9.4f)
            curveTo(20f, 15.1f, 12f, 20f, 12f, 20f)
            close()
        }
    }

    val SpeakerSlash: ImageVector by lazy {
        strokeIcon("SpeakerSlash") {
            moveTo(4f, 9.5f)
            horizontalLineTo(7.5f)
            lineTo(12.5f, 5.2f)
            verticalLineTo(18.8f)
            lineTo(7.5f, 14.5f)
            horizontalLineTo(4f)
            close()
            moveTo(16f, 9.5f)
            lineTo(20.5f, 14.5f)
            moveTo(20.5f, 9.5f)
            lineTo(16f, 14.5f)
        }
    }

    val Contrast: ImageVector by lazy {
        strokeIcon("Contrast") {
            moveTo(12f, 3.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 20.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 3.5f)
            moveTo(12f, 3.5f)
            verticalLineTo(20.5f)
            moveTo(12f, 7.5f)
            lineTo(16.8f, 12.3f)
            moveTo(12f, 12f)
            lineTo(16.5f, 16.5f)
        }
    }

    val Block: ImageVector by lazy {
        strokeIcon("Block") {
            moveTo(12f, 3.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 20.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 3.5f)
            moveTo(6.2f, 6.2f)
            lineTo(17.8f, 17.8f)
        }
    }

    val Moon: ImageVector by lazy {
        strokeIcon("Moon") {
            moveTo(19.2f, 14.8f)
            curveTo(17.9f, 15.4f, 16.5f, 15.6f, 15f, 15.4f)
            curveTo(11.3f, 14.9f, 8.6f, 11.7f, 8.8f, 8f)
            curveTo(8.9f, 6.6f, 9.4f, 5.2f, 10.3f, 4.1f)
            curveTo(6.5f, 4.8f, 3.8f, 8.1f, 4f, 12.1f)
            curveTo(4.2f, 16.7f, 8.1f, 20.2f, 12.7f, 20f)
            curveTo(15.5f, 19.9f, 17.9f, 18.2f, 19.2f, 14.8f)
            close()
        }
    }

    val Timer: ImageVector by lazy {
        strokeIcon("Timer") {
            moveTo(12f, 6.2f)
            arcTo(7.2f, 7.2f, 0f, true, false, 12f, 20.6f)
            arcTo(7.2f, 7.2f, 0f, true, false, 12f, 6.2f)
            moveTo(9.5f, 3.2f)
            horizontalLineTo(14.5f)
            moveTo(12f, 13.4f)
            lineTo(15f, 10.4f)
        }
    }

    val Globe: ImageVector by lazy {
        strokeIcon("Globe") {
            moveTo(12f, 3.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 20.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 3.5f)
            moveTo(3.8f, 12f)
            horizontalLineTo(20.2f)
            moveTo(12f, 3.5f)
            curveTo(14.5f, 6.2f, 15.5f, 9f, 15.5f, 12f)
            curveTo(15.5f, 15f, 14.5f, 17.8f, 12f, 20.5f)
            moveTo(12f, 3.5f)
            curveTo(9.5f, 6.2f, 8.5f, 9f, 8.5f, 12f)
            curveTo(8.5f, 15f, 9.5f, 17.8f, 12f, 20.5f)
        }
    }

    val Sparkles: ImageVector by lazy {
        strokeIcon("Sparkles") {
            moveTo(12f, 3.5f)
            lineTo(13.8f, 9.2f)
            lineTo(19.5f, 11f)
            lineTo(13.8f, 12.8f)
            lineTo(12f, 18.5f)
            lineTo(10.2f, 12.8f)
            lineTo(4.5f, 11f)
            lineTo(10.2f, 9.2f)
            close()
            moveTo(18.2f, 16.2f)
            lineTo(18.8f, 18f)
            lineTo(20.5f, 18.6f)
            lineTo(18.8f, 19.2f)
            lineTo(18.2f, 21f)
            lineTo(17.6f, 19.2f)
            lineTo(15.8f, 18.6f)
            lineTo(17.6f, 18f)
            close()
        }
    }

    val Trash: ImageVector by lazy {
        strokeIcon("Trash") {
            moveTo(4.5f, 6.5f)
            horizontalLineTo(19.5f)
            moveTo(9.2f, 6.5f)
            verticalLineTo(4.6f)
            curveTo(9.2f, 4f, 9.7f, 3.5f, 10.3f, 3.5f)
            horizontalLineTo(13.7f)
            curveTo(14.3f, 3.5f, 14.8f, 4f, 14.8f, 4.6f)
            verticalLineTo(6.5f)
            moveTo(6.5f, 6.5f)
            lineTo(7.4f, 19f)
            curveTo(7.5f, 19.9f, 8.2f, 20.5f, 9.1f, 20.5f)
            horizontalLineTo(14.9f)
            curveTo(15.8f, 20.5f, 16.5f, 19.9f, 16.6f, 19f)
            lineTo(17.5f, 6.5f)
            moveTo(10.2f, 10.5f)
            verticalLineTo(16.5f)
            moveTo(13.8f, 10.5f)
            verticalLineTo(16.5f)
        }
    }

    val InfoCircle: ImageVector by lazy {
        strokeIcon("InfoCircle") {
            moveTo(12f, 3.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 20.5f)
            arcTo(8.5f, 8.5f, 0f, true, false, 12f, 3.5f)
            moveTo(12f, 11f)
            verticalLineTo(16.2f)
            moveTo(12f, 8f)
            verticalLineTo(8.2f)
        }
    }

    val WifiSlash: ImageVector by lazy {
        strokeIcon("WifiSlash") {
            moveTo(3.5f, 3.5f)
            lineTo(20.5f, 20.5f)
            moveTo(12f, 18.2f)
            verticalLineTo(18.4f)
            moveTo(8.5f, 15.2f)
            curveTo(9.5f, 14.3f, 10.7f, 13.8f, 12f, 13.8f)
            curveTo(12.8f, 13.8f, 13.6f, 14f, 14.3f, 14.4f)
            moveTo(5.6f, 12.1f)
            curveTo(7.1f, 10.7f, 9f, 9.9f, 11f, 9.8f)
            moveTo(15.5f, 10.6f)
            curveTo(16.6f, 11f, 17.6f, 11.6f, 18.4f, 12.3f)
            moveTo(3.2f, 9f)
            curveTo(4.7f, 7.6f, 6.4f, 6.6f, 8.3f, 6.1f)
            moveTo(12.6f, 5.8f)
            curveTo(15.7f, 6f, 18.5f, 7.2f, 20.8f, 9.2f)
        }
    }

    val ReelsPlayOutline: ImageVector by lazy {
        strokeIcon("ReelsPlayOutline") {
            moveTo(5.2f, 4.5f)
            horizontalLineTo(18.8f)
            curveTo(19.9f, 4.5f, 20.8f, 5.4f, 20.8f, 6.5f)
            verticalLineTo(17.5f)
            curveTo(20.8f, 18.6f, 19.9f, 19.5f, 18.8f, 19.5f)
            horizontalLineTo(5.2f)
            curveTo(4.1f, 19.5f, 3.2f, 18.6f, 3.2f, 17.5f)
            verticalLineTo(6.5f)
            curveTo(3.2f, 5.4f, 4.1f, 4.5f, 5.2f, 4.5f)
            close()
            moveTo(3.5f, 8.8f)
            horizontalLineTo(20.5f)
            moveTo(8.5f, 4.5f)
            lineTo(10.5f, 8.8f)
            moveTo(14.5f, 4.5f)
            lineTo(16.5f, 8.8f)
            moveTo(10.2f, 11.4f)
            lineTo(15.2f, 14.1f)
            lineTo(10.2f, 16.8f)
            close()
        }
    }

    val ReelsPlayFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "ReelsPlayFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                pathFillType = PathFillType.EvenOdd
            ) {
                moveTo(5.5f, 4f)
                horizontalLineTo(18.5f)
                curveTo(20f, 4f, 21f, 5f, 21f, 6.5f)
                verticalLineTo(17.5f)
                curveTo(21f, 19f, 20f, 20f, 18.5f, 20f)
                horizontalLineTo(5.5f)
                curveTo(4f, 20f, 3f, 19f, 3f, 17.5f)
                verticalLineTo(6.5f)
                curveTo(3f, 5f, 4f, 4f, 5.5f, 4f)
                close()
                moveTo(10f, 9f)
                verticalLineTo(15f)
                lineTo(15.6f, 12f)
                close()
            }
        }.build()
    }

    val Photo: ImageVector by lazy {
        strokeIcon("Photo") {
            moveTo(5f, 4.5f)
            horizontalLineTo(19f)
            curveTo(20f, 4.5f, 20.8f, 5.3f, 20.8f, 6.3f)
            verticalLineTo(17.7f)
            curveTo(20.8f, 18.7f, 20f, 19.5f, 19f, 19.5f)
            horizontalLineTo(5f)
            curveTo(4f, 19.5f, 3.2f, 18.7f, 3.2f, 17.7f)
            verticalLineTo(6.3f)
            curveTo(3.2f, 5.3f, 4f, 4.5f, 5f, 4.5f)
            close()
            moveTo(8.4f, 8.2f)
            arcTo(1.4f, 1.4f, 0f, true, false, 8.4f, 11f)
            arcTo(1.4f, 1.4f, 0f, true, false, 8.4f, 8.2f)
            moveTo(4.2f, 17.2f)
            lineTo(9.2f, 12.4f)
            lineTo(12.8f, 15.6f)
            lineTo(15.6f, 13f)
            lineTo(19.8f, 17.2f)
        }
    }

    val Palette: ImageVector by lazy {
        strokeIcon("Palette") {
            moveTo(12f, 3.5f)
            curveTo(7.3f, 3.5f, 3.5f, 7.2f, 3.5f, 11.8f)
            curveTo(3.5f, 16.4f, 7.2f, 20.2f, 11.8f, 20.2f)
            curveTo(13.2f, 20.2f, 14.3f, 19.2f, 14.3f, 17.9f)
            curveTo(14.3f, 17.3f, 14.1f, 16.8f, 13.7f, 16.4f)
            curveTo(13.4f, 16f, 13.2f, 15.5f, 13.2f, 15f)
            curveTo(13.2f, 14.1f, 13.9f, 13.4f, 14.8f, 13.4f)
            horizontalLineTo(16.7f)
            curveTo(18.8f, 13.4f, 20.5f, 11.7f, 20.5f, 9.6f)
            curveTo(20.5f, 6.2f, 16.7f, 3.5f, 12f, 3.5f)
            close()
            moveTo(7.5f, 11.5f)
            verticalLineTo(11.6f)
            moveTo(9.5f, 7.8f)
            verticalLineTo(7.9f)
            moveTo(14.2f, 7.8f)
            verticalLineTo(7.9f)
            moveTo(16.8f, 10.2f)
            verticalLineTo(10.3f)
        }
    }

    val AppGrid: ImageVector by lazy {
        strokeIcon("AppGrid") {
            moveTo(4.5f, 4.5f)
            horizontalLineTo(10.5f)
            verticalLineTo(10.5f)
            horizontalLineTo(4.5f)
            close()
            moveTo(13.5f, 4.5f)
            horizontalLineTo(19.5f)
            verticalLineTo(10.5f)
            horizontalLineTo(13.5f)
            close()
            moveTo(4.5f, 13.5f)
            horizontalLineTo(10.5f)
            verticalLineTo(19.5f)
            horizontalLineTo(4.5f)
            close()
            moveTo(13.5f, 13.5f)
            horizontalLineTo(19.5f)
            verticalLineTo(19.5f)
            horizontalLineTo(13.5f)
            close()
        }
    }

    val Check: ImageVector by lazy {
        strokeIcon("Check", strokeWidth = 2.4f) {
            moveTo(4.8f, 12.6f)
            lineTo(9.6f, 17.4f)
            lineTo(19.2f, 7.2f)
        }
    }

    val ChatBubble: ImageVector by lazy {
        strokeIcon("ChatBubble") {
            moveTo(4.5f, 6.2f)
            curveTo(4.5f, 4.9f, 5.5f, 4f, 6.8f, 4f)
            horizontalLineTo(17.2f)
            curveTo(18.5f, 4f, 19.5f, 4.9f, 19.5f, 6.2f)
            verticalLineTo(14.4f)
            curveTo(19.5f, 15.7f, 18.5f, 16.6f, 17.2f, 16.6f)
            horizontalLineTo(9.6f)
            lineTo(5.2f, 20f)
            verticalLineTo(16.6f)
            curveTo(4.7f, 16.2f, 4.5f, 15.4f, 4.5f, 14.4f)
            close()
        }
    }
}
