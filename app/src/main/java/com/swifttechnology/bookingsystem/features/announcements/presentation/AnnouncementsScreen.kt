package com.swifttechnology.bookingsystem.features.announcements.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.announcements.presentation.components.AddEditAnnouncementSheet
import com.swifttechnology.bookingsystem.features.announcements.presentation.components.AnnouncementCard

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    isEditable: Boolean = false,
    onEnterEditMode: () -> Unit = {},
    onExitEditMode: () -> Unit = {},
    viewModel: AnnouncementsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val colors  = MaterialTheme.customColors

    var selectedIds by remember { androidx.compose.runtime.mutableStateOf(emptySet<Long>()) }
    var viewingAnnouncement by remember { 
        androidx.compose.runtime.mutableStateOf<com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement?>(null) 
    }

    // Clear selection when exiting edit mode
    LaunchedEffect(isEditable) {
        if (!isEditable) {
            selectedIds = emptySet<Long>()
        }
    }

    // Keep search query in sync with parent
    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    // Exit edit mode if user changes tabs
    LaunchedEffect(uiState.selectedTab) {
        onExitEditMode()
    }

    // Auto-dismiss snackbar messages
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(2500)
            viewModel.clearMessages()
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            //     Tab Bar                                                        
            AnnouncementTabBar(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            Spacer(Modifier.height(Spacing.md))

            //     Content                                                        
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary)
                    }
                }

                uiState.displayedAnnouncements.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (uiState.selectedTab == AnnouncementTab.PINNED)
                                "No pinned announcements"
                            else
                                "No announcements yet",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
                        contentPadding = PaddingValues(bottom = 96.dp)  // space for FAB + bottom bar
                    ) {
                        items(
                            items = uiState.displayedAnnouncements,
                            key   = { it.id }
                        ) { announcement ->
                            AnnouncementCard(
                                announcement  = announcement,
                                isEditMode    = isEditable,
                                isSelected    = announcement.id in selectedIds,
                                showAccentBar = uiState.selectedTab == AnnouncementTab.PINNED,
                                onTap = {
                                    if (isEditable) {
                                        selectedIds = if (announcement.id in selectedIds) {
                                            selectedIds - announcement.id
                                        } else {
                                            selectedIds + announcement.id
                                        }
                                    } else {
                                        viewingAnnouncement = announcement
                                    }
                                },
                                onLongPress = {
                                    if (!isEditable) {
                                        onEnterEditMode()
                                        selectedIds = selectedIds + announcement.id
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        //     FAB (only when NOT in edit mode)                                   
        if (!isEditable) {
            FloatingActionButton(
                onClick         = viewModel::openAddSheet,
                containerColor  = colors.deepBlack,
                contentColor    = Color.White,
                shape           = CircleShape,
                modifier        = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = Spacing.md)
                    .navigationBarsPadding()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add announcement")
            }
        }

        //     Edit mode bottom action bar                                        
        AnimatedVisibility(
            visible  = isEditable,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter    = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit     = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            EditModeActionBar(
                hasSelection = selectedIds.isNotEmpty(),
                onEdit = {
                    val selected = uiState.displayedAnnouncements
                        .firstOrNull { it.id == selectedIds.firstOrNull() }
                    if (selected != null) {
                        viewModel.openEditSheet(selected)
                    }
                },
                onDelete = { 
                    viewModel.deleteSelected(selectedIds)
                    selectedIds = emptySet<Long>()
                    onExitEditMode()
                }
            )
        }

        //     Success / error snackbar                                            
        AnimatedVisibility(
            visible  = uiState.successMessage != null || uiState.errorMessage != null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (isEditable) 80.dp else 16.dp),
            enter    = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit     = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            val isError = uiState.errorMessage != null
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg)
                    .navigationBarsPadding()
                    .background(
                        color = if (isError) Color(0xFFDC2626) else Color(0xFF1C1C1E),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = uiState.successMessage ?: uiState.errorMessage ?: "",
                    color      = Color.White,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    //     Viewing dialog (Details)                                               
    viewingAnnouncement?.let { announcement ->
        com.swifttechnology.bookingsystem.features.announcements.presentation.components.AnnouncementDetailDialog(
            announcement = announcement,
            onDismiss = { viewingAnnouncement = null },
            onMoreOptions = { /* Handle more options if needed */ }
        )
    }

    //     Add / Edit bottom sheet                                                
    if (uiState.showAddEditSheet) {
        AddEditAnnouncementSheet(
            editingAnnouncement = uiState.editingAnnouncement,
            sheetState          = sheetState,
            onDismiss           = viewModel::dismissSheet,
            onSave              = viewModel::saveAnnouncement
        )
    }
}

//     Tab bar component                                                          

@Composable
private fun AnnouncementTabBar(
    selectedTab: AnnouncementTab,
    onTabSelected: (AnnouncementTab) -> Unit
) {
    val colors = MaterialTheme.customColors

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(colors.surfaceLight)
            .border(1.dp, colors.neutral200, RoundedCornerShape(CornerRadius.full))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AnnouncementTab.values().forEach { tab ->
                val isSelected = selectedTab == tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(CornerRadius.full))
                        .background(if (isSelected) Primary else Color.Transparent)
                        .clickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = tab.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = if (isSelected) Color.White else colors.textSecondary,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    )
                }
            }
        }
    }
}

//     Edit mode action bar                                                       

@Composable
private fun EditModeActionBar(
    hasSelection: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(Spacing.md),
        horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
    ) {
        // Edit button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(if (hasSelection) Color(0xFFEBEBF5) else Color(0xFFF2F2F7))
                .clickable(enabled = hasSelection, onClick = onEdit),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = if (hasSelection) Color(0xFF1C1C1E) else Color(0xFF3C3C43).copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
                Text("Edit", color = if (hasSelection) Color(0xFF1C1C1E) else Color(0xFF3C3C43).copy(alpha = 0.3f), fontWeight = FontWeight.SemiBold)
            }
        }

        // Delete button
        Box(
            modifier = Modifier
                .weight(1f)
                .height(50.dp)
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(if (hasSelection) Color(0xFFFFD4D4) else Color(0xFFF2F2F7))
                .clickable(enabled = hasSelection, onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = if (hasSelection) Color(0xFF1C1C1E) else Color(0xFF3C3C43).copy(alpha = 0.3f),
                    modifier = Modifier.size(18.dp)
                )
                Text("Delete", color = if (hasSelection) Color(0xFF1C1C1E) else Color(0xFF3C3C43).copy(alpha = 0.3f), fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
