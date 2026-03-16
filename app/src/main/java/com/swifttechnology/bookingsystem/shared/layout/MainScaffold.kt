package com.swifttechnology.bookingsystem.shared.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.SidebarPanel
import com.swifttechnology.bookingsystem.shared.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    sidebarItems: List<SidebarItem>,
    selectedItem: SidebarItem,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSidebarItemSelected: (SidebarItem) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    searchPlaceholder: String = "Search...",
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.customColors.dashboardBg,
    onEditClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(280.dp),
                windowInsets = WindowInsets.systemBars,
                drawerContainerColor = MaterialTheme.customColors.neutral100
            ) {
                SidebarPanel(
                    items = sidebarItems,
                    selectedItem = selectedItem,
                    onItemSelected = { item ->
                        onSidebarItemSelected(item)
                        scope.launch { drawerState.close() }
                    },
                    onLogout = {
                        scope.launch {
                            drawerState.close()
                            onLogout()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }
        }
    ) {
        Scaffold(
            containerColor = containerColor
        ) { innerPadding ->
            Column(
                modifier = modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                TopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    searchPlaceholder = searchPlaceholder,
                    onEditClick = onEditClick
                )
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

