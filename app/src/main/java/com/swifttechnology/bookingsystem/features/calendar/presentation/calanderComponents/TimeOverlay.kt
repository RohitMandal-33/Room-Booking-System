package com.swifttechnology.bookingsystem.features.calendar.presentation.calanderComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun TimeOverlay(range: TimeRange, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xDD1C1C1E))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = "${IntervalUtils.formatMinutes(range.startMinutes)} – ${IntervalUtils.formatMinutes(range.endMinutes)}",
            fontSize = 13.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

