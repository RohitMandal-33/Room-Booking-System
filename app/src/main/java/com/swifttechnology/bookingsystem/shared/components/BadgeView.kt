package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.swifttechnology.bookingsystem.core.designsystem.Spacing


@Composable
fun BadgeView(
    count: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.error,
    contentColor: Color = Color.White,
    size: Dp = Spacing.ml
) {
    if (count <= 0) return

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(color = backgroundColor, shape = CircleShape)
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BadgeViewPreview() {
    BadgeView(count = 5)
}
