package com.swifttechnology.bookingsystem.shared.components

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
import com.swifttechnology.bookingsystem.core.designsystem.Elevation


@Composable
fun CardView(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = CornerRadius.lg,
    elevation: Dp = Elevation.sm,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(cornerRadius),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun CardViewPreview() {
    androidx.compose.material3.Text(
        text = "Card Content",
        modifier = Modifier.padding(16.dp)
    )
}
