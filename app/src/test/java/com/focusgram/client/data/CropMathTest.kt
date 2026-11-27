package com.focusgram.client.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CropMathTest {

    @Test
    fun `computeCoverScale picks larger axis ratio so frame has no empty borders`() {
        // Landscape 2000x1000 image in a 500x500 square frame -> needs 0.5x to cover height
        val scale = CropMath.computeCoverScale(
            imageWidth = 2000f,
            imageHeight = 1000f,
            frameWidth = 500f,
            frameHeight = 500f
        )
        assertEquals(0.5f, scale, 0.0001f)
    }

    @Test
    fun `clampOffset constrains pan within zoomed image bounds`() {
        // 1000x1000 image in 500x500 frame at 2x zoom -> displayed 1000x1000 -> max offset 250px
        val clamped = CropMath.clampOffset(
            offsetX = 400f,
            offsetY = -500f,
            scale = 2f,
            imageWidth = 1000f,
            imageHeight = 1000f,
            frameWidth = 500f,
            frameHeight = 500f
        )
        assertEquals(250f, clamped.x, 0.001f)
        assertEquals(-250f, clamped.y, 0.001f)
    }

    @Test
    fun `computeSourceCropRect maps zoomed center and pan accurately`() {
        // 1000x1000 image in 500x500 frame at 2x zoom, centered -> crops [250..750, 250..750]
        val centered = CropMath.computeSourceCropRect(
            scale = 2f,
            offsetX = 0f,
            offsetY = 0f,
            imageWidth = 1000f,
            imageHeight = 1000f,
            frameWidth = 500f,
            frameHeight = 500f
        )
        assertEquals(250f, centered.left, 0.01f)
        assertEquals(250f, centered.top, 0.01f)
        assertEquals(750f, centered.right, 0.01f)
        assertEquals(750f, centered.bottom, 0.01f)

        // Panning image to the right (+250px) reveals the left edge [0..500]
        val pannedRight = CropMath.computeSourceCropRect(
            scale = 2f,
            offsetX = 250f,
            offsetY = 0f,
            imageWidth = 1000f,
            imageHeight = 1000f,
            frameWidth = 500f,
            frameHeight = 500f
        )
        assertEquals(0f, pannedRight.left, 0.01f)
        assertEquals(500f, pannedRight.right, 0.01f)
        assertTrue(pannedRight.width == 500f)
    }
}
