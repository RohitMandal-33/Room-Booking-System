package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.Elevation


@Composable
fun CardView(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadius.lg,
    elevation: Dp = Elevation.sm,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val containerBg =
        if (isDark) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
    val borderColor =
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isDark) 0.6f else 1f)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = BorderStroke(1.25.dp, borderColor)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun CardViewPreview() {
    androidx.compose.material3.Text(
        text = "Card Content",
        modifier = Modifier.padding(Spacing.md)
    )
}
