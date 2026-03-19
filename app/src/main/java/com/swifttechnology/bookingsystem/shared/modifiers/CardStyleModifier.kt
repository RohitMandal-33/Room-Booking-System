package com.swifttechnology.bookingsystem.shared.modifiers

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation


fun Modifier.cardStyle(
    cornerRadius: Dp = CornerRadius.lg,
    elevation: Dp = Elevation.sm
): Modifier = this
    .shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
    .clip(RoundedCornerShape(cornerRadius))


fun Modifier.appShadow(
    elevation: Dp = Elevation.md,
    cornerRadius: Dp = CornerRadius.md
): Modifier = this.shadow(elevation = elevation, shape = RoundedCornerShape(cornerRadius))
