package com.swifttechnology.bookingsystem.core.utils

import androidx.compose.ui.graphics.Color

object ColorUtils {
    /**
     * Parses a color string from the API.
     * Supports:
     *  RGB format: "(255, 0, 0)" or "255, 0, 0" or "rgb(255, 0, 0)"
     *  Hex format: "#RRGGBB"
     * 
     * Returns the parsed [Color] or the provided [default] if parsing fails.
     */
    fun parseColor(colorStr: String?, default: Color = Color(0xFF7C3AED)): Color {
        if (colorStr.isNullOrBlank()) return default
        
        return try {
            if (colorStr.startsWith("#")) {
                Color(android.graphics.Color.parseColor(colorStr))
            } else {
                // Try to find digits in the string
                val regex = Regex("""(\d+)""")
                val matches = regex.findAll(colorStr).map { it.value.toInt() }.toList()
                
                if (matches.size >= 3) {
                    Color(
                        red = matches[0].coerceIn(0, 255) / 255f,
                        green = matches[1].coerceIn(0, 255) / 255f,
                        blue = matches[2].coerceIn(0, 255) / 255f,
                        alpha = if (matches.size >= 4) matches[3].coerceIn(0, 255) / 255f else 1f
                    )
                } else {
                    default
                }
            }
        } catch (e: Exception) {
            default
        }
    }

    /**
     * Generates a light background color based on the provided [color].
     */
    fun getLightBackgroundColor(color: Color, isDark: Boolean): Color {
        return if (isDark) {
            color.copy(alpha = 0.15f)
        } else {
            color.copy(alpha = 0.08f)
        }
    }
}
