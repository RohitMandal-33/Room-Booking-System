package com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.foundation.layout.Box
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
import kotlin.math.roundToInt

// Sizing constants
private val DOT_SIZE         = 10.dp          // visible dot diameter
private val TOUCH_TARGET     = 36.dp          // invisible touch-zone box (larger than dot)
private val CORNER_RADIUS    = 8.dp


private val PickerFill   = Color(0xFFE8EAF6)
private val PickerBorder = Color(0xFF5C6BC0)
private val PickerDot    = Color(0xFF5C6BC0)

@Composable
internal fun PickerEventBlock(
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
    val haptic       = LocalHapticFeedback.current
    val density      = LocalDensity.current

    // Convert touch-target size to px for hit-testing
    val touchTargetPx = with(density) { TOUCH_TARGET.toPx() }
    val minHeightPx   = IntervalUtils.MIN_DURATION_MINUTES * minutePx
    val dayMaxPx      = IntervalUtils.DAY_MINUTES * minutePx

    val topPxState    = remember { mutableFloatStateOf(event.timeRange.startMinutes * minutePx) }
    val heightPxState = remember { mutableFloatStateOf(event.timeRange.durationMinutes * minutePx) }
    var isDraggingLocally by remember { mutableStateOf(false) }

    LaunchedEffect(event.timeRange) {
        if (!isDraggingLocally) {
            topPxState.floatValue    = event.timeRange.startMinutes * minutePx
            heightPxState.floatValue = event.timeRange.durationMinutes * minutePx
        }
    }

    var lastHapticSnap by remember { mutableIntStateOf(-1) }

    // Outer Box: no clip so dots can overlap the border
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
            //  All gesture detection lives here
            .pointerInput(event.id) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    down.consume()

                    val touchX   = down.position.x
                    val touchY   = down.position.y
                    val blockW   = size.width.toFloat()
                    val blockH   = size.height.toFloat()

                    // Corner-based handle detection:
                    //   TOP    → top-left  corner box
                    //   BOTTOM → bottom-right corner box
                    //   BODY   → everything else
                    val handle = when {
                        touchX < touchTargetPx && touchY < touchTargetPx ->
                            DragHandle.TOP
                        touchX > blockW - touchTargetPx && touchY > blockH - touchTargetPx ->
                            DragHandle.BOTTOM
                        else ->
                            DragHandle.BODY
                    }

                    val baseTopPx    = topPxState.floatValue
                    val baseHeightPx = heightPxState.floatValue
                    val baseStartMin = event.timeRange.startMinutes
                    val baseEndMin   = event.timeRange.endMinutes

                    isDraggingLocally = true
                    lastHapticSnap    = -1
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
                                    IntervalUtils.snapToInterval(rawTop / minutePx)
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
                                topPxState.floatValue    = rawTop
                                heightPxState.floatValue =
                                    (baseHeightPx - accPx).coerceAtLeast(minHeightPx)

                                val snappedStart =
                                    IntervalUtils.snapToInterval(rawTop / minutePx)
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
                                    IntervalUtils.snapToInterval(rawBottom / minutePx)
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

                    val finalTop     = topPxState.floatValue
                    val finalBottom  = finalTop + heightPxState.floatValue
                    val snappedStart = IntervalUtils.snapToInterval(finalTop / minutePx)
                    val snappedEnd   = IntervalUtils.snapToInterval(finalBottom / minutePx)

                    when (handle) {
                        DragHandle.BODY   -> onMoveCommitted(snappedStart)
                        DragHandle.TOP    -> onResizeTopCommitted(snappedStart)
                        DragHandle.BOTTOM -> onResizeBottomCommitted(snappedEnd)
                    }
                }
            }
    ) {
        //Visible block: border + fill
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(CORNER_RADIUS))
                .background(PickerFill)
                .border(1.5.dp, PickerBorder, RoundedCornerShape(CORNER_RADIUS))
        ) {
            Text(
                text = event.title,
                fontSize = 12.sp,
                color = PickerBorder,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
            )
        }

        //  TOP-LEFT: transparent touch target + dot centred on corner (shifted a little right)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(TOUCH_TARGET)
                .align(Alignment.TopStart)
                // Shifted right by 12dp from the edge
                .offset(x = -(TOUCH_TARGET / 2) + 12.dp, y = -(TOUCH_TARGET / 2))
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    .background(PickerDot, CircleShape)
            )
        }

        // BOTTOM-RIGHT: transparent touch target + dot centred on corner (shifted a little left)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(TOUCH_TARGET)
                .align(Alignment.BottomEnd)
                // Shifted left by 12dp from the edge
                .offset(x = (TOUCH_TARGET / 2) - 12.dp, y = (TOUCH_TARGET / 2))
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    .background(PickerDot, CircleShape)
            )
        }
    }
}
