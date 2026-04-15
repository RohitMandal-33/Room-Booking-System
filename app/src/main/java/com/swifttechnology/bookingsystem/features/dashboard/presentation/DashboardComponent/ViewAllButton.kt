package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.customColors

@Composable
fun ViewAllButton(
    modifier: Modifier = Modifier,
    text: String = "View All",
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick        = onClick,
        modifier       = modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(CornerRadius.md),
        contentPadding = PaddingValues(vertical = 10.dp),
        border         = BorderStroke(1.dp, MaterialTheme.customColors.divider),
        colors         = ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
