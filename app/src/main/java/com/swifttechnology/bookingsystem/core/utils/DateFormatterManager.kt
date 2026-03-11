package com.swifttechnology.bookingsystem.core.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Thread-safe date formatting utility.
 * Caches formatters per pattern to avoid repeated allocations.
 * Mirrors iOS DateFormatterManager.
 */
object DateFormatterManager {

    private val formatters = mutableMapOf<String, SimpleDateFormat>()

    private fun getFormatter(pattern: String, timeZone: TimeZone = TimeZone.getDefault()): SimpleDateFormat {
        return formatters.getOrPut(pattern) {
            SimpleDateFormat(pattern, Locale.getDefault()).apply {
                this.timeZone = timeZone
            }
        }
    }

    /** Format a [Date] to a display string, e.g. "Mar 01, 2026". */
    fun formatDisplay(date: Date): String =
        getFormatter(Constants.DATE_FORMAT_DISPLAY).format(date)

    /** Format a [Date] to API format (UTC), e.g. "2026-03-01T05:12:23Z". */
    fun formatForApi(date: Date): String =
        getFormatter(Constants.DATE_FORMAT_API, TimeZone.getTimeZone("UTC")).format(date)

    /** Format a [Date] to time-only string, e.g. "10:30 AM". */
    fun formatTime(date: Date): String =
        getFormatter(Constants.TIME_FORMAT_DISPLAY).format(date)

    /** Format a [Date] to display date+time, e.g. "Mar 01, 2026 10:30 AM". */
    fun formatDateTime(date: Date): String =
        getFormatter(Constants.DATE_TIME_DISPLAY).format(date)

    /** Parse an API ISO-8601 string to [Date], returns null on parse failure. */
    fun parseApiDate(dateString: String): Date? = try {
        getFormatter(Constants.DATE_FORMAT_API, TimeZone.getTimeZone("UTC")).parse(dateString)
    } catch (e: Exception) {
        null
    }
}
