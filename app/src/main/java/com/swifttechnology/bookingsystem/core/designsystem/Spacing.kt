package com.swifttechnology.bookingsystem.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Layout constants that are not “rhythm” spacing but belong with the design tokens. */
object Layout {
    val navigationDrawerWidth: Dp = 280.dp
}

object Spacing {
    val none: Dp = 0.dp
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val ms: Dp = 12.dp
    val md: Dp = 16.dp
    val ml: Dp = 20.dp
    val lg: Dp = 24.dp
    val xl: Dp = 32.dp
    val xxl: Dp = 48.dp
    val xxxl: Dp = 64.dp
}

object ButtonTokens {
    val height: Dp = 52.dp
}

/** Corner radius tokens */
object CornerRadius {
    val sm: Dp = 4.dp
    val md: Dp = 8.dp
    val lg: Dp = 12.dp
    val xl: Dp = 16.dp
    val full: Dp = 100.dp
}

/** Elevation tokens */
object Elevation {
    val none: Dp = 0.dp
    val sm: Dp = 2.dp
    val md: Dp = 4.dp
    val lg: Dp = 8.dp
    val xl: Dp = 16.dp
}

/**
 * Horizontal inset for primary screen content so phones, tablets, and unfolded devices
 * stay visually balanced without duplicating magic numbers in every screen.
 */
@Composable
fun adaptiveHorizontalScreenPadding(): Dp {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp >= 900 -> Spacing.xl
        widthDp >= 600 -> Spacing.lg
        else -> Spacing.md
    }
}
