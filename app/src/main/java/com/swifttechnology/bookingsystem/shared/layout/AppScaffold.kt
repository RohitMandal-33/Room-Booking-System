package com.swifttechnology.bookingsystem.shared.layout

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

/**
 * App-level Scaffold wrapper.
 * Centralizes topBar/bottomBar/FAB composition — mirrors iOS AppScaffold.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
private fun AppScaffoldPreview() {
    AppScaffold(
        topBar = { Text("TopBar") },
        content = { Text("Content") }
    )
}
