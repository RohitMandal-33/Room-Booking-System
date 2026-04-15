package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

enum class DialogStyle {
    WARNING,
    INFO
}

@Composable
fun ReusableAlertDialog(
    style: DialogStyle,
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissText: String = "Cancel"
) {
    val (icon, iconTint) = when (style) {
        DialogStyle.WARNING -> Icons.Outlined.ErrorOutline to Color(0xFFD32F2F)
        DialogStyle.INFO -> Icons.Outlined.Info to MaterialTheme.colorScheme.primary
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontSize = 18.sp
            )
        },
        text = {
            androidx.compose.foundation.layout.Row {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.alpha(0.9f)
                )
                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier
                        .weight(0.1f)
                )
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = iconTint
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissText)
            }
        },
        modifier = modifier
    )
}

