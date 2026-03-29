package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DayPickerUiState(
    val draggableEvent: DraggableEvent? = null,
    val blockedSlots: List<TimeRange> = emptyList(),
    val previewRange: TimeRange? = null,
    val isDragging: Boolean = false
)

@HiltViewModel
class DayTimePickerViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(DayPickerUiState())
    val uiState: StateFlow<DayPickerUiState> = _uiState.asStateFlow()

    fun onDragStarted() =
        _uiState.update { it.copy(isDragging = true) }

    fun onPreviewChanged(preview: TimeRange) =
        _uiState.update { it.copy(previewRange = preview) }

    fun onTimeSlotLongPressed(startMinutes: Int) = _uiState.update { state ->
        val duration = 60
        state.copy(
            draggableEvent = DraggableEvent(
                timeRange = TimeRange(startMinutes, startMinutes + duration)
            )
        )
    }

    fun initializePicker(startMinutes: Int, endMinutes: Int) = _uiState.update { state ->
        state.copy(
            draggableEvent = DraggableEvent(
                timeRange = TimeRange(startMinutes, endMinutes)
            )
        )
    }

    fun onMoveCommitted(proposedStart: Int) = _uiState.update { state ->
        val event = state.draggableEvent ?: return@update state
        val dur = event.timeRange.durationMinutes
        val resolved = IntervalUtils.resolveMove(
            proposedStart = proposedStart,
            duration = dur,
            blocked = state.blockedSlots,
            previousStart = event.timeRange.startMinutes
        )
        val adjusted = resolved != proposedStart
        if (adjusted) scheduleAdjustedReset()
        state.copy(
            draggableEvent = event.copy(
                timeRange = TimeRange(resolved, resolved + dur),
                wasAdjusted = adjusted
            ),
            isDragging = false,
            previewRange = null
        )
    }

    fun onResizeTopCommitted(proposedStart: Int) = _uiState.update { state ->
        val event = state.draggableEvent ?: return@update state
        val resolved = IntervalUtils.resolveResizeTop(
            proposedStart = proposedStart,
            currentEnd = event.timeRange.endMinutes,
            blocked = state.blockedSlots,
            previousStart = event.timeRange.startMinutes
        )
        val adjusted = resolved != proposedStart
        if (adjusted) scheduleAdjustedReset()
        state.copy(
            draggableEvent = event.copy(
                timeRange = event.timeRange.copy(startMinutes = resolved),
                wasAdjusted = adjusted
            ),
            isDragging = false,
            previewRange = null
        )
    }

    fun onResizeBottomCommitted(proposedEnd: Int) = _uiState.update { state ->
        val event = state.draggableEvent ?: return@update state
        val resolved = IntervalUtils.resolveResizeBottom(
            proposedEnd = proposedEnd,
            currentStart = event.timeRange.startMinutes,
            blocked = state.blockedSlots,
            previousEnd = event.timeRange.endMinutes
        )
        val adjusted = resolved != proposedEnd
        if (adjusted) scheduleAdjustedReset()
        state.copy(
            draggableEvent = event.copy(
                timeRange = event.timeRange.copy(endMinutes = resolved),
                wasAdjusted = adjusted
            ),
            isDragging = false,
            previewRange = null
        )
    }

    fun onDragCancelled() =
        _uiState.update { it.copy(isDragging = false, previewRange = null) }

    fun onCancelBooking() =
        _uiState.update { it.copy(draggableEvent = null, isDragging = false, previewRange = null) }

    fun setBlockedSlots(slots: List<TimeRange>) =
        _uiState.update { it.copy(blockedSlots = slots) }

    private fun scheduleAdjustedReset() {
        viewModelScope.launch {
            delay(1200L)
            _uiState.update { state ->
                val event = state.draggableEvent ?: return@update state
                state.copy(draggableEvent = event.copy(wasAdjusted = false))
            }
        }
    }
}
