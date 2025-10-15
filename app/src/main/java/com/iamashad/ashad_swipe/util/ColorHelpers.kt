package com.iamashad.ashad_swipe.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.scale
import androidx.palette.graphics.Palette
import kotlin.math.sqrt

/**
 * Extracts two visually distinct colors from an image bitmap
 * to be used as a gradient background.
 */
fun extractGradientColors(bmp: Bitmap): Pair<Color, Color> {
    // Downscale image for faster palette processing
    val scaled = bmp.scale(256, 256)

    val palette = Palette.from(scaled)
        .maximumColorCount(16)   // small palette for speed
        .clearFilters()          // allow even extreme colors
        .generate()

    // Pick a strong primary color (prefer vibrant tones)
    val primaryInt = listOfNotNull(
        palette.vibrantSwatch?.rgb,
        palette.darkVibrantSwatch?.rgb,
        palette.lightVibrantSwatch?.rgb,
        palette.dominantSwatch?.rgb
    ).firstOrNull() ?: 0xFF888888.toInt()
    val primary = Color(primaryInt)

    // Gather secondary candidates
    val candidates = buildList {
        palette.darkVibrantSwatch?.rgb?.let(::add)
        palette.lightVibrantSwatch?.rgb?.let(::add)
        palette.mutedSwatch?.rgb?.let(::add)
        palette.darkMutedSwatch?.rgb?.let(::add)
        palette.lightMutedSwatch?.rgb?.let(::add)
        palette.dominantSwatch?.rgb?.let(::add)
    }.distinct()
        .filter { it != primaryInt }
        .map { Color(it) }

    // Pick the candidate most different from primary
    val minDistance = 22f // perceptual ΔE threshold
    var secondary = candidates.maxByOrNull { colorDistanceLab(primary, it) }

    // Fallback if palette colors are too similar
    if (secondary == null || colorDistanceLab(primary, secondary) < minDistance) {
        // Try a small hue shift first
        secondary = shiftHue(primary, delta = 28f, satMul = 1.05f, valMul = 0.92f)
        if (colorDistanceLab(primary, secondary) < minDistance) {
            // Bigger hue shift if still too close
            secondary = shiftHue(primary, delta = 160f, satMul = 0.95f, valMul = 1.05f)
        }
    }

    // Ensure both are opaque
    return primary.copy(alpha = 1f) to secondary.copy(alpha = 1f)
}

/**
 * Returns approximate perceptual distance (ΔE) between two colors
 * using the CIE Lab color space.
 */
private fun colorDistanceLab(a: Color, b: Color): Float {
    val lab1 = DoubleArray(3)
    val lab2 = DoubleArray(3)
    ColorUtils.colorToLAB(a.toArgb(), lab1)
    ColorUtils.colorToLAB(b.toArgb(), lab2)
    val dl = lab1[0] - lab2[0]
    val da = lab1[1] - lab2[1]
    val db = lab1[2] - lab2[2]
    return sqrt((dl * dl + da * da + db * db).toFloat())
}

/**
 * Shifts the hue of a color (and optionally adjusts saturation/value)
 * to produce a complementary or variant tone.
 */
private fun shiftHue(color: Color, delta: Float, satMul: Float = 1f, valMul: Float = 1f): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsv)
    hsv[0] = (hsv[0] + delta) % 360f
    hsv[1] = (hsv[1] * satMul).coerceIn(0f, 1f)
    hsv[2] = (hsv[2] * valMul).coerceIn(0f, 1f)
    return Color(android.graphics.Color.HSVToColor(hsv))
}

/**
 * Chooses white or black text for best contrast on a background color.
 */
fun bestOnColor(bg: Color): Color {
    val bgArgb = bg.toArgb()
    val cBlack = ColorUtils.calculateContrast(android.graphics.Color.BLACK, bgArgb)
    val cWhite = ColorUtils.calculateContrast(android.graphics.Color.WHITE, bgArgb)
    return if (cWhite >= cBlack) Color.White else Color.Black
}
