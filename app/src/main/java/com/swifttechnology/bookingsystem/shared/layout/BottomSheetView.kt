package com.swifttechnology.bookingsystem.shared.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import com.swifttechnology.bookingsystem.core.designsystem.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetView(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
    title: String? = null,
    showDragHandle: Boolean = true,
    showCloseButton: Boolean = false,
    scrimColor: Color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
    headerContent: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        scrimColor = scrimColor,
        dragHandle = if (showDragHandle) null else { {} }, // null = default handle, {} = no handle
        contentWindowInsets = { WindowInsets.systemBars },
    ) {
        Column(modifier = Modifier.padding(bottom = Spacing.xl)) {

            // ── Header row (title + optional close button) ──────────────────
            val hasTitle = !title.isNullOrBlank()
            if (hasTitle || showCloseButton || headerContent != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = Spacing.md, end = Spacing.xs, top = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Title or custom header fills remaining space
                    Column(modifier = Modifier.weight(1f)) {
                        if (hasTitle) {
                            Text(
                                text = title!!,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier
                                    .padding(vertical = Spacing.sm)
                                    .semantics { heading() },
                            )
                        }
                        headerContent?.invoke()
                    }

                    if (showCloseButton) {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // ── Body ─────────────────────────────────────────────────────────
            content()
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Title only")
@Composable
private fun PreviewTitleOnly() {
    BottomSheetView(onDismiss = {}, title = "Pick a date") {
        Text("Content goes here", modifier = Modifier.padding(horizontal = Spacing.md))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Title + close button")
@Composable
private fun PreviewWithClose() {
    BottomSheetView(onDismiss = {}, title = "Filter results", showCloseButton = true) {
        Text("Filter options", modifier = Modifier.padding(horizontal = Spacing.md))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "Custom header slot")
@Composable
private fun PreviewCustomHeader() {
    BottomSheetView(
        onDismiss = {},
        showCloseButton = true,
        headerContent = {
            Text("Custom subtitle", style = MaterialTheme.typography.bodySmall)
        },
    ) {
        Text("Body content", modifier = Modifier.padding(horizontal = Spacing.md))
    }
}
