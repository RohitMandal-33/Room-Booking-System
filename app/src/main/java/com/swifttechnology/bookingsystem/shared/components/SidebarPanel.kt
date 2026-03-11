package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
    Column(
        modifier = modifier.background(MaterialTheme.customColors.neutral100),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Small top spacer so items don't start at the very edge
        Spacer(modifier = Modifier.height(8.dp))

        items.forEach { item ->
            SidebarTile(
                item = item,
                isActive = item.route == selectedItem.route,
                onClick = { onItemSelected(item) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        SidebarTile(
            item = SidebarItem("Logout", Icons.AutoMirrored.Outlined.Logout),
            isActive = false,
            onClick = onLogout
        )
    }
}


