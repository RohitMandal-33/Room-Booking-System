package com.swifttechnology.bookingsystem.core.extensions

import androidx.compose.ui.graphics.Color

/**
 * Parse a hex color string (#RRGGBB or #AARRGGBB) into a Compose [Color].
 * Returns [Color.Unspecified] if parsing fails.
 */
fun String.toComposeColor(): Color = try {
    val hex = this.trimStart('#')
    Color(android.graphics.Color.parseColor("#$hex"))
} catch (e: IllegalArgumentException) {
    Color.Unspecified
}

/**
 * Returns a darker version of this color by reducing HSV brightness.
 */
fun Color.darken(fraction: Float = 0.15f): Color {
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt(),
        hsv
    )
    hsv[2] *= (1f - fraction)
    val argb = android.graphics.Color.HSVToColor(hsv)
    return Color(argb)
}
