package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import com.swifttechnology.bookingsystem.shared.components.SecondaryButton

@Composable
fun CalendarBottomBar(
    pickerState: DayPickerUiState,
    onCancel: () -> Unit,
    onProceed: () -> Unit,
    modifier: Modifier = Modifier,
    purplePrimary: Color = Color(0xFF6C3EE8),
    purpleLight: Color = Color(0xFFEDE9FF)
) {
    val draggable = pickerState.draggableEvent ?: return
    val range      = draggable.timeRange
    val startLabel = IntervalUtils.formatMinutes(range.startMinutes)
    val endLabel   = IntervalUtils.formatMinutes(range.endMinutes)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm)
            .clip(RoundedCornerShape(16.dp))
            .border(1.5.dp, purplePrimary.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .background(purpleLight)
            .padding(horizontal = Spacing.md, vertical = Spacing.md)
    ) {
        Column {
            Text(
                text = "Selected Time Slot:",
                fontSize = 12.sp,
                color = purplePrimary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(Spacing.sm))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                TimeBox(time = startLabel)
                Spacer(modifier = Modifier.width(Spacing.sm))
                Text(
                    text = "-",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.width(Spacing.sm))
                TimeBox(time = endLabel)

                Spacer(modifier = Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Box(modifier = Modifier.width(90.dp)) {
                         SecondaryButton(text = "Cancel", onClick = onCancel)
                    }
                    Box(modifier = Modifier.width(100.dp)) {
                        PrimaryButton(text = "Proceed", onClick = onProceed)
                    }
                }
            }
        }
    }
}
