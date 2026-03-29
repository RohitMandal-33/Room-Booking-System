package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimeOverlay(range: TimeRange, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xDD1C1C1E))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "${IntervalUtils.formatMinutes(range.startMinutes)} – ${IntervalUtils.formatMinutes(range.endMinutes)}",
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = IntervalUtils.formatDuration(range.durationMinutes),
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.Normal
        )
    }
}
