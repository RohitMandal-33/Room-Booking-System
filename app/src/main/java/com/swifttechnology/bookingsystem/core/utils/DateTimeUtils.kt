package com.swifttechnology.bookingsystem.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Utility for handling polymorphic date and time data from the API.
 */
object DateTimeUtils {

    /**
     * Parses a polymorphic time object into a [LocalTime].
     * Handles:
     * 1. String: "HH:mm:ss" or "HH:mm"
     * 2. Map (from Gson/JSON object): { "hour": 10, "minute": 30, ... }
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun parseLocalTime(input: Any?): LocalTime? {
        if (input == null) return null

        // Case 1: String format
        if (input is String) {
            if (input.isBlank()) return null
            return try {
                LocalTime.parse(input)
            } catch (e: Exception) {
                // Try fallback format if it's just HH:mm
                try {
                    LocalTime.parse(input, DateTimeFormatter.ofPattern("H:m[:s]"))
                } catch (e2: Exception) {
                    null
                }
            }
        }

        // Case 2: Map/Object format (LinkedTreeMap from Gson)
        if (input is Map<*, *>) {
            return try {
                val hour = (input["hour"] as? Number)?.toInt() ?: return null
                val minute = (input["minute"] as? Number)?.toInt() ?: 0
                val second = (input["second"] as? Number)?.toInt() ?: 0
                val nano = (input["nano"] as? Number)?.toInt() ?: 0
                LocalTime.of(hour, minute, second, nano)
            } catch (e: Exception) {
                null
            }
        }

        return null
    }

    /**
     * Formats a polymorphic time into a display string (e.g., "10:30 AM").
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatTimeDisplay(input: Any?): String {
        val time = parseLocalTime(input) ?: return ""
        return try {
            val formatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())
            time.format(formatter)
        } catch (e: Exception) {
            ""
        }
    }
}
