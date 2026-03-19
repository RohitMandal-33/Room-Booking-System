package com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents

import kotlin.math.roundToInt

object IntervalUtils {

    const val SNAP_MINUTES = 10
    const val MIN_DURATION_MINUTES = 30
    const val DAY_MINUTES = 1440

    fun snapToInterval(rawMinutes: Float, interval: Int = SNAP_MINUTES): Int =
        ((rawMinutes / interval).roundToInt() * interval).coerceIn(0, DAY_MINUTES)

    fun formatMinutes(totalMinutes: Int): String {
        val h = (totalMinutes / 60).coerceIn(0, 23)
        val m = totalMinutes % 60
        val displayH = when {
            h == 0 -> 12
            h <= 12 -> h
            else -> h - 12
        }
        return "$displayH:${m.toString().padStart(2, '0')} ${if (h < 12) "AM" else "PM"}"
    }

    /**
     * Find the nearest valid start position for a block-move drag.
     * Falls back to [previousStart] if no safe slot exists.
     */
    fun resolveMove(
        proposedStart: Int,
        duration: Int,
        blocked: List<TimeRange>,
        previousStart: Int
    ): Int {
        val clamped = proposedStart.coerceIn(0, DAY_MINUTES - duration)
        val candidate = TimeRange(clamped, clamped + duration)
        if (!hasOverlap(candidate, blocked)) return clamped

        val offender = blocked.first { it.overlaps(candidate) }
        val before = offender.startMinutes - duration
        val after = offender.endMinutes

        return when {
            before >= 0 && !hasOverlap(TimeRange(before, before + duration), blocked) -> before
            after + duration <= DAY_MINUTES && !hasOverlap(TimeRange(after, after + duration), blocked) -> after
            else -> previousStart
        }
    }

    // Resolve a top-handle resize (end is fixed, start moves).

    fun resolveResizeTop(
        proposedStart: Int,
        currentEnd: Int,
        blocked: List<TimeRange>,
        previousStart: Int
    ): Int {
        val clamped = proposedStart.coerceIn(0, currentEnd - MIN_DURATION_MINUTES)
        val candidate = TimeRange(clamped, currentEnd)
        if (!hasOverlap(candidate, blocked)) return clamped

        val latestEnd = blocked.filter { it.overlaps(candidate) }.maxOf { it.endMinutes }
        return if (latestEnd <= currentEnd - MIN_DURATION_MINUTES) latestEnd else previousStart
    }

    // Resolve a bottom-handle resize (start is fixed, end moves).

    fun resolveResizeBottom(
        proposedEnd: Int,
        currentStart: Int,
        blocked: List<TimeRange>,
        previousEnd: Int
    ): Int {
        val clamped = proposedEnd.coerceIn(currentStart + MIN_DURATION_MINUTES, DAY_MINUTES)
        val candidate = TimeRange(currentStart, clamped)
        if (!hasOverlap(candidate, blocked)) return clamped

        val earliestStart = blocked.filter { it.overlaps(candidate) }.minOf { it.startMinutes }
        return if (earliestStart >= currentStart + MIN_DURATION_MINUTES) earliestStart else previousEnd
    }

    private fun hasOverlap(range: TimeRange, blocked: List<TimeRange>): Boolean =
        blocked.any { it.overlaps(range) }
}

