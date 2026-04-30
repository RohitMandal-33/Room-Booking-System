package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.dayview

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.DragHandle
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.DraggableEvent
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.IntervalUtils
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.TimeRange
import kotlin.math.roundToInt

// Sizing constants
private val DOT_SIZE = 10.dp
private val TOUCH_TARGET = 40.dp
private val CORNER_RADIUS = 10.dp

// How far from the corner the dot sits along the edge
private val DOT_EDGE_INSET = 20.dp

// Colors
private val PickerFill = Color(0xFFEDE7F6)
private val PickerFillAdjusted = Color(0xFFFFE0B2)
private val PickerBorder = Color(0xFF5C6BC0)
private val PickerBorderAdjusted = Color(0xFFFF9800)
private val PickerDot = Color(0xFF5C6BC0)

@Composable
fun PickerEventBlock(
    event: DraggableEvent,
    minutePx: Float,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    onDragStarted: () -> Unit,
    onPreviewChanged: (TimeRange) -> Unit,
    onMoveCommitted: (Int) -> Unit,
    onResizeTopCommitted: (Int) -> Unit,
    onResizeBottomCommitted: (Int) -> Unit,
    onDragCancelled: () -> Unit,
    onPointerViewportY: (Float) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val density = LocalDensity.current

    val touchTargetPx = with(density) { TOUCH_TARGET.toPx() }
    val minHeightPx = IntervalUtils.MIN_DURATION_MINUTES * minutePx
    val dayMaxPx = IntervalUtils.DAY_MINUTES * minutePx

    val topPxState = remember { mutableFloatStateOf(event.timeRange.startMinutes * minutePx) }
    val heightPxState = remember { mutableFloatStateOf(event.timeRange.durationMinutes * minutePx) }
    var isDraggingLocally by remember { mutableStateOf(false) }

    LaunchedEffect(event.timeRange) {
        if (!isDraggingLocally) {
            topPxState.floatValue = event.timeRange.startMinutes * minutePx
            heightPxState.floatValue = event.timeRange.durationMinutes * minutePx
        }
    }

    var lastHapticSnap by remember { mutableIntStateOf(-1) }

    // Animated colors for conflict-adjust flash
    val animatedFill by animateColorAsState(
        targetValue = if (event.wasAdjusted) PickerFillAdjusted else PickerFill,
        animationSpec = tween(durationMillis = 400),
        label = "pickerFillAnim"
    )
    val animatedBorder by animateColorAsState(
        targetValue = if (event.wasAdjusted) PickerBorderAdjusted else PickerBorder,
        animationSpec = tween(durationMillis = 400),
        label = "pickerBorderAnim"
    )

    Box(
        modifier = modifier
            .offset { IntOffset(0, topPxState.floatValue.roundToInt()) }
            .layout { measurable, constraints ->
                val h = heightPxState.floatValue.roundToInt().coerceAtLeast(1)
                val placeable =
                    measurable.measure(constraints.copy(minHeight = h, maxHeight = h))
                layout(placeable.width, h) { placeable.place(0, 0) }
            }
            .padding(horizontal = 4.dp)
            .pointerInput(event.id) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()

                    val touchX = down.position.x
                    val touchY = down.position.y
                    val blockW = size.width.toFloat()
                    val blockH = size.height.toFloat()

                    // Handle detection: top-left strip, bottom-right strip, or body
                    val handle = when {
                        touchY < touchTargetPx && touchX < blockW * 0.5f ->
                            DragHandle.TOP
                        touchY > blockH - touchTargetPx && touchX > blockW * 0.5f ->
                            DragHandle.BOTTOM
                        else ->
                            DragHandle.BODY
                    }

                    val baseTopPx = topPxState.floatValue
                    val baseHeightPx = heightPxState.floatValue
                    val baseStartMin = event.timeRange.startMinutes
                    val baseEndMin = event.timeRange.endMinutes

                    isDraggingLocally = true
                    lastHapticSnap = -1
                    onDragStarted()

                    var accPx = 0f

                    drag(down.id) { change ->
                        accPx += change.positionChange().y
                        change.consume()

                        when (handle) {
                            DragHandle.BODY -> {
                                val rawTop = (baseTopPx + accPx)
                                    .coerceIn(0f, dayMaxPx - baseHeightPx)
                                topPxState.floatValue = rawTop

                                val snappedStart =
                                    IntervalUtils.snapToInterval((rawTop / minutePx).roundToInt())
                                if (snappedStart != lastHapticSnap) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    lastHapticSnap = snappedStart
                                    onPreviewChanged(
                                        TimeRange(
                                            snappedStart,
                                            snappedStart + (baseEndMin - baseStartMin)
                                        )
                                    )
                                }
                            }

                            DragHandle.TOP -> {
                                val rawTop = (baseTopPx + accPx)
                                    .coerceIn(0f, baseTopPx + baseHeightPx - minHeightPx)
                                topPxState.floatValue = rawTop
                                heightPxState.floatValue =
                                    (baseHeightPx - accPx).coerceAtLeast(minHeightPx)

                                val snappedStart =
                                    IntervalUtils.snapToInterval((rawTop / minutePx).roundToInt())
                                if (snappedStart != lastHapticSnap) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    lastHapticSnap = snappedStart
                                    onPreviewChanged(TimeRange(snappedStart, baseEndMin))
                                }
                            }

                            DragHandle.BOTTOM -> {
                                val rawBottom = (baseTopPx + baseHeightPx + accPx)
                                    .coerceIn(baseTopPx + minHeightPx, dayMaxPx)
                                heightPxState.floatValue = rawBottom - baseTopPx

                                val snappedEnd =
                                    IntervalUtils.snapToInterval((rawBottom / minutePx).roundToInt())
                                if (snappedEnd != lastHapticSnap) {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    lastHapticSnap = snappedEnd
                                    onPreviewChanged(TimeRange(baseStartMin, snappedEnd))
                                }
                            }
                        }

                        onPointerViewportY(
                            (topPxState.floatValue + touchY) - scrollState.value
                        )
                    }

                    isDraggingLocally = false

                    val finalTop = topPxState.floatValue
                    val finalBottom = finalTop + heightPxState.floatValue
                    val snappedStart = IntervalUtils.snapToInterval((finalTop / minutePx).roundToInt())
                    val snappedEnd = IntervalUtils.snapToInterval((finalBottom / minutePx).roundToInt())

                    when (handle) {
                        DragHandle.BODY -> onMoveCommitted(snappedStart)
                        DragHandle.TOP -> onResizeTopCommitted(snappedStart)
                        DragHandle.BOTTOM -> onResizeBottomCommitted(snappedEnd)
                    }
                }
            }
    ) {
        // Visible block: border + fill with shadow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shadow(
                    elevation = if (isDraggingLocally) 8.dp else 2.dp,
                    shape = RoundedCornerShape(CORNER_RADIUS),
                    clip = false
                )
                .clip(RoundedCornerShape(CORNER_RADIUS))
                .background(animatedFill)
                .border(1.5.dp, animatedBorder, RoundedCornerShape(CORNER_RADIUS))
        ) {
        // Content layout: title + live time range + duration
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Format a minute offset (from midnight) into "h:mm AM/PM"
            fun minutesToTimeLabel(totalMinutes: Int): String {
                val clamped = totalMinutes.coerceIn(0, 24 * 60 - 1)
                val h = clamped / 60
                val m = clamped % 60
                val isPm = h >= 12
                val h12 = when {
                    h == 0 -> 12
                    h > 12 -> h - 12
                    else   -> h
                }
                return "%d:%02d %s".format(h12, m, if (isPm) "PM" else "AM")
            }

            val startMin = (topPxState.floatValue / minutePx).roundToInt()
            val durMin   = (heightPxState.floatValue / minutePx).roundToInt().coerceAtLeast(0)
            val endMin   = startMin + durMin

            Text(
                text = event.title,
                fontSize = 12.sp,
                color = PickerBorder,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
            // Time range line
            Text(
                text = "${minutesToTimeLabel(startMin)} – ${minutesToTimeLabel(endMin)}",
                fontSize = 10.sp,
                color = PickerBorder.copy(alpha = 0.85f),
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            // Duration line
            val h = durMin / 60
            val m = durMin % 60
            val durationLabel = buildString {
                if (h > 0) append("${h}h ")
                if (m > 0) append("${m}min")
                if (h == 0 && m == 0) append("0min")
            }.trim()
            Text(
                text = durationLabel,
                fontSize = 10.sp,
                color = PickerBorder.copy(alpha = 0.65f),
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        }

        // TOP-LEFT resize dot
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(TOUCH_TARGET)
                .align(Alignment.TopStart)
                .offset(
                    x = DOT_EDGE_INSET - TOUCH_TARGET / 2,
                    y = -(TOUCH_TARGET / 2)
                )
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    .shadow(2.dp, CircleShape)
                    .background(PickerDot, CircleShape)
            )
        }

        // BOTTOM-RIGHT resize dot
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(TOUCH_TARGET)
                .align(Alignment.BottomEnd)
                .offset(
                    x = -(DOT_EDGE_INSET - TOUCH_TARGET / 2),
                    y = TOUCH_TARGET / 2
                )
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    .shadow(2.dp, CircleShape)
                    .background(PickerDot, CircleShape)
            )
        }
    }
}
