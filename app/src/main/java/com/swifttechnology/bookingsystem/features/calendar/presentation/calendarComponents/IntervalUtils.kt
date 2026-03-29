package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents

import kotlin.math.abs
import kotlin.math.roundToInt

object IntervalUtils {

    const val SNAP_MINUTES = 5
    const val MIN_DURATION_MINUTES = 15
    const val DAY_MINUTES = 1440

    fun snapToInterval(rawMinutes: Int, interval: Int = SNAP_MINUTES): Int =
        ((rawMinutes.toDouble() / interval).roundToInt() * interval).coerceIn(0, DAY_MINUTES)

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

    fun formatDuration(minutes: Int): String = when {
        minutes < 60 -> "${minutes}min"
        minutes % 60 == 0 -> "${minutes / 60}h"
        else -> "${minutes / 60}h ${minutes % 60}min"
    }

    fun resolveMove(
        proposedStart: Int,
        duration: Int,
        blocked: List<TimeRange>,
        previousStart: Int
    ): Int {
        val clamped = proposedStart.coerceIn(0, DAY_MINUTES - duration)
        val candidate = TimeRange(clamped, clamped + duration)
        if (!hasOverlap(candidate, blocked)) return clamped

        // Collect all candidate positions: just before or just after each blocked slot
        data class Candidate(val start: Int, val distance: Int)

        val candidates = mutableListOf<Candidate>()
        for (slot in blocked) {
            val before = slot.startMinutes - duration
            if (before >= 0) {
                val tr = TimeRange(before, before + duration)
                if (!hasOverlap(tr, blocked)) {
                    candidates += Candidate(before, abs(before - clamped))
                }
            }
            val after = slot.endMinutes
            if (after + duration <= DAY_MINUTES) {
                val tr = TimeRange(after, after + duration)
                if (!hasOverlap(tr, blocked)) {
                    candidates += Candidate(after, abs(after - clamped))
                }
            }
        }

        return candidates.minByOrNull { it.distance }?.start ?: previousStart
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
