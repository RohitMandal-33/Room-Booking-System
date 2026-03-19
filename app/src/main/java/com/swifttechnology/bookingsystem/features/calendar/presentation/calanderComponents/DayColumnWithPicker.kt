package com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.swifttechnology.bookingsystem.features.calendar.presentation.MeetingEvent
import java.time.LocalDate
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
            ) {
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
                                color = Color(0xFFAAAAAA),
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(HOUR_HEIGHT_DP.dp)
                                .border(0.5.dp, Color(0xFFEEEEEE))
                        )
                    }
                }

                regularEvents
                    .asSequence()
                    .filter { it.date == selectedDate }
                    .forEach { event ->
                        val startMin = event.startTime.hour * 60 + event.startTime.minute
                        val endMin = event.endTime.hour * 60 + event.endTime.minute
                        val topPx = startMin * minutePx
                        val heightPx = ((endMin - startMin) * minutePx)
                            .coerceAtLeast(with(density) { MIN_BLOCK_HEIGHT_DP.dp.toPx() })

                        Box(
                            modifier = Modifier
                                .padding(start = TIME_LABEL_WIDTH_DP.dp, end = 4.dp)
                                .offset { IntOffset(0, topPx.roundToInt()) }
                                .fillMaxWidth()
                                .height(with(density) { heightPx.toDp() })
                                .padding(horizontal = 2.dp, vertical = 1.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(event.color.copy(alpha = 0.65f))
                                .padding(4.dp)
                        ) {
                            Text(
                                text = event.title,
                                fontSize = 10.sp,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                pickerState.blockedSlots.forEach { slot ->
                    Box(
                        modifier = Modifier
                            .padding(start = TIME_LABEL_WIDTH_DP.dp, end = 4.dp)
                            .offset { IntOffset(0, (slot.startMinutes * minutePx).roundToInt()) }
                            .fillMaxWidth()
                            .height(with(density) { (slot.durationMinutes * minutePx).toDp() })
                            .background(Color(0x22FF3B30))
                            .border(0.5.dp, Color(0x55FF3B30))
                    ) {
                        Text(
                            text = "Booked",
                            fontSize = 9.sp,
                            color = Color(0xFFFF3B30).copy(alpha = 0.7f),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }

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

        if (pickerState.isDragging) {
            pickerState.previewRange?.let { preview ->
                TimeOverlay(
                    range = preview,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 8.dp, top = 8.dp)
                        .zIndex(20f)
                )
            }
        }
    }
}
