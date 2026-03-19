package com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DayPickerUiState(
    val draggableEvent: DraggableEvent = DraggableEvent(),
    val blockedSlots: List<TimeRange> = listOf(
        TimeRange(10 * 60 + 30, 11 * 60 + 30), // 10:30 – 11:30
        TimeRange(14 * 60, 15 * 60) // 14:00 – 15:00
    ),
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

    fun onMoveCommitted(proposedStart: Int) = _uiState.update { state ->
        val dur = state.draggableEvent.timeRange.durationMinutes
        val resolved = IntervalUtils.resolveMove(
            proposedStart = proposedStart,
            duration = dur,
            blocked = state.blockedSlots,
            previousStart = state.draggableEvent.timeRange.startMinutes
        )
        state.copy(
            draggableEvent = state.draggableEvent.copy(
                timeRange = TimeRange(resolved, resolved + dur)
            ),
            isDragging = false,
            previewRange = null
        )
    }

    fun onResizeTopCommitted(proposedStart: Int) = _uiState.update { state ->
        val resolved = IntervalUtils.resolveResizeTop(
            proposedStart = proposedStart,
            currentEnd = state.draggableEvent.timeRange.endMinutes,
            blocked = state.blockedSlots,
            previousStart = state.draggableEvent.timeRange.startMinutes
        )
        state.copy(
            draggableEvent = state.draggableEvent.copy(
                timeRange = state.draggableEvent.timeRange.copy(startMinutes = resolved)
            ),
            isDragging = false,
            previewRange = null
        )
    }

    fun onResizeBottomCommitted(proposedEnd: Int) = _uiState.update { state ->
        val resolved = IntervalUtils.resolveResizeBottom(
            proposedEnd = proposedEnd,
            currentStart = state.draggableEvent.timeRange.startMinutes,
            blocked = state.blockedSlots,
            previousEnd = state.draggableEvent.timeRange.endMinutes
        )
        state.copy(
            draggableEvent = state.draggableEvent.copy(
                timeRange = state.draggableEvent.timeRange.copy(endMinutes = resolved)
            ),
            isDragging = false,
            previewRange = null
        )
    }

    fun onDragCancelled() =
        _uiState.update { it.copy(isDragging = false, previewRange = null) }

    fun setBlockedSlots(slots: List<TimeRange>) =
        _uiState.update { it.copy(blockedSlots = slots) }
}

