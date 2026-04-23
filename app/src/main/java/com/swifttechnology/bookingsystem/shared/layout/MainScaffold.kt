package com.swifttechnology.bookingsystem.shared.layout

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.ui.input.pointer.pointerInput
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.shared.components.SidebarItem
import com.swifttechnology.bookingsystem.shared.components.SidebarPanel
import com.swifttechnology.bookingsystem.shared.components.DialogStyle
import com.swifttechnology.bookingsystem.shared.components.ReusableAlertDialog
import com.swifttechnology.bookingsystem.shared.components.TopBar
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    sidebarItems: List<SidebarItem>,
    selectedItem: SidebarItem,
    title: String,
    onSidebarItemSelected: (SidebarItem) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.customColors.neutral100,
    showEditIcon: Boolean = false,
    isEditMode: Boolean = false,
    showTopBar: Boolean = true,
    showTodayIcon: Boolean = false,
    onTodayClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = false,
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
                            showLogoutDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Spacing.ms)
                )
            }
        }
    ) {
        if (showLogoutDialog) {
            ReusableAlertDialog(
                style = DialogStyle.WARNING,
                title = "Logout",
                message = "Are you sure you want to log out?",
                confirmText = "Logout",
                onConfirm = {
                    showLogoutDialog = false
                    onLogout()
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                containerColor = containerColor
            ) { innerPadding ->
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .padding(if (showTopBar) Spacing.md else 0.dp)
                        .fillMaxSize()
                ) {
                    if (showTopBar) {
                        TopBar(
                            title = title,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            showEditIcon = showEditIcon,
                            isEditMode = isEditMode,
                            showTodayIcon = showTodayIcon,
                            onTodayClick = onTodayClick,
                            onEditClick = onEditClick,
                            onBackClick = onBackClick
                        )
                        Spacer(modifier = Modifier.height(Spacing.md))
                    }
                    content()
                }
            }

            // Overlay to capture taps and close drawer when open
            if (drawerState.isOpen) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures {
                                scope.launch { drawerState.close() }
                            }
                        }
                )
            }
        }
    }
}

