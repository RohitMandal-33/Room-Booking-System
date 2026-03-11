package com.swifttechnology.bookingsystem.core.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a card-style rounded clip + shadow — equivalent to iOS View+Extensions cardStyle().
 */
fun Modifier.cardStyle(
    cornerRadius: Dp = 12.dp,
    elevation: Dp = 4.dp
): Modifier = this
    .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
    .clip(RoundedCornerShape(cornerRadius))

/**
 * Clickable with no ripple/indication — for custom touch handling.
 */
fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}
