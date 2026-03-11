package com.swifttechnology.bookingsystem.shared.models

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents an empty/no-data state for lists and screens.
 */
data class EmptyState(
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector? = null
)
