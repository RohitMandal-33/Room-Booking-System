package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.dayview

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.DayPickerUiState
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.formatHourLabel
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.IntervalUtils
import com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared.TimeRange
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

private const val HOUR_HEIGHT_DP = 60
private const val TIME_LABEL_WIDTH_DP = 64
private const val MIN_BLOCK_HEIGHT_DP = 20

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayColumnWithPicker(
    selectedDate: LocalDate,
    regularEvents: List<MeetingEvent>,
    pickerState: DayPickerUiState,
    onGridLongPress: (Int) -> Unit,
    onEventClick: (MeetingEvent) -> Unit = {},
    onDragStarted: () -> Unit,
    onPreviewChanged: (TimeRange) -> Unit,
    onMoveCommitted: (Int) -> Unit,
    onResizeTopCommitted: (Int) -> Unit,
    onResizeBottomCommitted: (Int) -> Unit,
    onDragCancelled: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val hourHeightPx = with(density) { HOUR_HEIGHT_DP.dp.toPx() }
    val minutePx = hourHeightPx / 60f

    val scrollState = rememberScrollState()
    val pointerViewportY = remember { mutableFloatStateOf(0f) }
    var viewportH by remember { mutableFloatStateOf(0f) }

    // Auto-scroll to current time on first composition
    val now = LocalTime.now()
    val currentTimeMinutes = now.hour * 60 + now.minute
    LaunchedEffect(Unit) {
        val targetScroll = ((currentTimeMinutes - 60).coerceAtLeast(0) * minutePx).roundToInt()
        scrollState.animateScrollTo(targetScroll)
    }

    // Auto-scroll during drags
    LaunchedEffect(pickerState.isDragging) {
        if (!pickerState.isDragging) return@LaunchedEffect
        while (true) {
            val edgePx = with(density) { 80.dp.toPx() }
            val pY = pointerViewportY.floatValue
            val vH = viewportH
            when {
                pY in 0f..edgePx -> scrollState.scrollBy(
                    -((1f - pY / edgePx) * 14f).coerceAtLeast(1f)
                )
                pY in (vH - edgePx)..vH -> scrollState.scrollBy(
                    (((pY - (vH - edgePx)) / edgePx) * 14f).coerceAtLeast(1f)
                )
            }
            delay(16L)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { viewportH = it.height.toFloat() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((HOUR_HEIGHT_DP * 24).dp)
                    .pointerInput(selectedDate) {
                        detectTapGestures(
                            onLongPress = { offset ->
                                val minute = (offset.y / minutePx).toInt()
                                val snappedStart = IntervalUtils.snapToInterval(minute)
                                onGridLongPress(snappedStart)
                            }
                        )
                    }
            ) {
                // Hour rows + grid lines
                repeat(24) { hour ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(y = (hour * HOUR_HEIGHT_DP).dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .width(TIME_LABEL_WIDTH_DP.dp)
                                .padding(end = 8.dp, top = 2.dp)
                        ) {
                            Text(
                                text = formatHourLabel(hour),
                                fontSize = 11.sp,
                                color = Color(0xFF9E9E9E),
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(HOUR_HEIGHT_DP.dp)
                                .border(0.5.dp, Color(0xFFE8E8E8))
                        )
                    }
                }

                // 30-minute sub-grid dashed lines
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                val timeLabelWidthPx = with(density) { TIME_LABEL_WIDTH_DP.dp.toPx() }
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    for (hour in 0 until 24) {
                        val y = (hour * HOUR_HEIGHT_DP + HOUR_HEIGHT_DP / 2) * density.density
                        drawLine(
                            color = Color(0xFFEEEEEE),
                            start = Offset(timeLabelWidthPx, y),
                            end = Offset(size.width, y),
                            strokeWidth = 0.5f * density.density,
                            pathEffect = dashEffect
                        )
                    }
                }

                // Past time overlay
                val isToday = selectedDate == LocalDate.now()
                val isPastDate = selectedDate.isBefore(LocalDate.now())
                
                if (isToday || isPastDate) {
                    val overlayEndMinutes = if (isPastDate) 24 * 60 else currentTimeMinutes
                    val overlayHeightY = with(density) { (overlayEndMinutes * minutePx).toDp() }
                    Box(
                        modifier = Modifier
                            .padding(start = TIME_LABEL_WIDTH_DP.dp)
                            .fillMaxWidth()
                            .height(overlayHeightY)
                            .background(Color(0xFFD0CCDD).copy(alpha = 0.5f))
                    )
                }

                // Current time indicator
                if (selectedDate == LocalDate.now()) {
                    val currentTimePx = currentTimeMinutes * minutePx
                    val currentTimeY = with(density) { currentTimePx.toDp() }

                    // Red line
                    Box(
                        modifier = Modifier
                            .padding(start = (TIME_LABEL_WIDTH_DP - 4).dp)
                            .offset(y = currentTimeY)
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(Color(0xFFEF4444))
                            .zIndex(5f)
                    )
                    // Red circle at left edge
                    Box(
                        modifier = Modifier
                            .offset(
                                x = (TIME_LABEL_WIDTH_DP - 8).dp,
                                y = currentTimeY - 4.dp
                            )
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                            .zIndex(5f)
                    )
                }



                // Past blocked slots (lilac-gray)
                pickerState.pastBlockedSlots.forEach { slot ->
                    Box(
                        modifier = Modifier
                            .padding(start = TIME_LABEL_WIDTH_DP.dp, end = 4.dp)
                            .offset { IntOffset(0, (slot.startMinutes * minutePx).roundToInt()) }
                            .fillMaxWidth()
                            .height(with(density) { (slot.durationMinutes * minutePx).toDp() })
                            .background(Color(0xFFD0CCDD).copy(alpha = 0.5f))
                            .border(0.5.dp, Color(0xFFB0ACBD).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                    )
                }

                // Booked meeting slots (red)
                pickerState.bookedBlockedSlots.forEach { slot ->
                    Box(
                        modifier = Modifier
                            .padding(start = TIME_LABEL_WIDTH_DP.dp, end = 4.dp)
                            .offset { IntOffset(0, (slot.startMinutes * minutePx).roundToInt()) }
                            .fillMaxWidth()
                            .height(with(density) { (slot.durationMinutes * minutePx).toDp() })
                            .background(Color(0xFFFF7070))
                            .border(0.5.dp, Color(0xFFCC5A5A), RoundedCornerShape(4.dp))
                    )
                }

                // Regular events rendered on the day grid (on top of blocked areas)
                regularEvents
                    .asSequence()
                    .filter { it.date == selectedDate }
                    .forEach { event ->
                        val startMin = event.startTime.hour * 60 + event.startTime.minute
                        val endMin = event.endTime.hour * 60 + event.endTime.minute
                        val topPx = startMin * minutePx
                        val heightPx = ((endMin - startMin) * minutePx)
                            .coerceAtLeast(with(density) { MIN_BLOCK_HEIGHT_DP.dp.toPx() })
                        val durationMin = endMin - startMin

                        Box(
                            modifier = Modifier
                                .padding(start = TIME_LABEL_WIDTH_DP.dp, end = 4.dp)
                                .offset { IntOffset(0, topPx.roundToInt()) }
                                .fillMaxWidth()
                                .height(with(density) { heightPx.toDp() })
                                .padding(horizontal = 2.dp, vertical = 1.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(event.color.copy(alpha = 0.75f))
                                .pointerInput(event) {
                                    detectTapGestures(
                                        onTap = { onEventClick(event) },
                                        onLongPress = { onEventClick(event) }
                                    )
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Column {
                                Text(
                                    text = event.title,
                                    fontSize = if (durationMin < 30) 10.sp else 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = if (durationMin < 30) 1 else 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                if (durationMin >= 20) {
                                    Text(
                                        text = "${IntervalUtils.formatMinutes(startMin)} – ${IntervalUtils.formatMinutes(endMin)}",
                                        fontSize = 9.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontWeight = FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                // Picker (draggable scheduling block)
                if (pickerState.draggableEvent != null) {
                    PickerEventBlock(
                        event = pickerState.draggableEvent,
                        minutePx = minutePx,
                        scrollState = scrollState,
                        modifier = Modifier
                            .padding(start = TIME_LABEL_WIDTH_DP.dp, end = 4.dp)
                            .fillMaxWidth()
                            .zIndex(10f),
                        onDragStarted = onDragStarted,
                        onPreviewChanged = onPreviewChanged,
                        onMoveCommitted = onMoveCommitted,
                        onResizeTopCommitted = onResizeTopCommitted,
                        onResizeBottomCommitted = onResizeBottomCommitted,
                        onDragCancelled = onDragCancelled,
                        onPointerViewportY = { pointerViewportY.floatValue = it }
                    )
                }
            }
        }

        // Time overlay shown during drag
        if (pickerState.isDragging) {
            pickerState.previewRange?.let { preview ->
                TimeOverlay(
                    range = preview,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 12.dp, top = 12.dp)
                        .zIndex(20f)
                )
            }
        }
    }
}
