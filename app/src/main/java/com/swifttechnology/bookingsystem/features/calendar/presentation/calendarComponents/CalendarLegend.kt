package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.Spacing

@Composable
fun CalendarLegendRow(
    modifier: Modifier = Modifier,
    unavailableColor: Color = Color(0xFFD0CCDD),
    bookedColor: Color = Color(0xFFFF7070).copy(alpha = 0.75f),
    selectionColor: Color = Color(0xFF9B6DFF).copy(alpha = 0.75f)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = unavailableColor, label = "Unavailable")
        Spacer(modifier = Modifier.width(Spacing.md))
        LegendItem(color = bookedColor, label = "Booked")
        Spacer(modifier = Modifier.width(Spacing.md))
        LegendItem(color = selectionColor, label = "Selection")
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color(0xFF555555)
        )
    }
}
