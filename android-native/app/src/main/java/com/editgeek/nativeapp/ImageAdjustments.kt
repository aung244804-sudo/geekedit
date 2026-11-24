package com.editgeek.nativeapp

import android.graphics.Bitmap
import kotlin.math.roundToInt

/**
 * Applies simple offline pixel adjustments. This is not generative AI; it is a
 * deterministic local edit that works without network access or API credentials.
 */
fun applyImageAdjustments(
    source: Bitmap,
    brightness: Float,
    contrast: Float,
    saturation: Float
): Bitmap {
    val width = source.width
    val height = source.height
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)
    source.getPixels(pixels, 0, width, 0, 0, width, height)

    val contrastFactor = contrast.coerceIn(0f, 2f)
    val saturationFactor = saturation.coerceIn(0f, 2f)
    val brightnessOffset = brightness.coerceIn(-100f, 100f)
    for (i in pixels.indices) {
        val color = pixels[i]
        val a = (color ushr 24) and 0xff
        val r = (color ushr 16) and 0xff
        val g = (color ushr 8) and 0xff
        val b = color and 0xff

        val gray = 0.213f * r + 0.715f * g + 0.072f * b
        val sr = gray + (r - gray) * saturationFactor
        val sg = gray + (g - gray) * saturationFactor
        val sb = gray + (b - gray) * saturationFactor
        val nr = (((sr - 128f) * contrastFactor) + 128f + brightnessOffset).roundToInt().coerceIn(0, 255)
        val ng = (((sg - 128f) * contrastFactor) + 128f + brightnessOffset).roundToInt().coerceIn(0, 255)
        val nb = (((sb - 128f) * contrastFactor) + 128f + brightnessOffset).roundToInt().coerceIn(0, 255)
        pixels[i] = (a shl 24) or (nr shl 16) or (ng shl 8) or nb
    }
    output.setPixels(pixels, 0, width, 0, 0, width, height)
    return output
}
