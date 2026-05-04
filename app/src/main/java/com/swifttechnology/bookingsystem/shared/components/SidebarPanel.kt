package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import com.swifttechnology.bookingsystem.core.designsystem.customColors

@Composable
fun SidebarPanel(
    items: List<SidebarItem>,
    selectedItem: SidebarItem,
    onItemSelected: (SidebarItem) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.customColors.neutral100),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items, key = { it.route }) { item ->
            SidebarTile(
                item = item,
                isActive = item.route == selectedItem.route,
                onClick = { onItemSelected(item) }
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SidebarTile(
                item = SidebarItem("Logout", Icons.AutoMirrored.Outlined.Logout),
                isActive = false,
                onClick = onLogout
            )
        }
    }
}


