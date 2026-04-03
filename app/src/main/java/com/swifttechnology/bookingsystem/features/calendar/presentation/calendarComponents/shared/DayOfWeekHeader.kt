package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors

@Composable
fun DayOfWeekHeader(withTimeColumn: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs)
    ) {
        if (withTimeColumn) {
            Spacer(modifier = Modifier.width(50.dp))
        }
        val days = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
        days.forEach { day ->
            Text(
                text = day,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.customColors.textSecondary
            )
        }
    }
}
