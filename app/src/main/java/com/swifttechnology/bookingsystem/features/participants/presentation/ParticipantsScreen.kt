package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.platform.LocalDensity
import com.swifttechnology.bookingsystem.features.meetingrooms.presentation.AddFab
import com.swifttechnology.bookingsystem.shared.components.DialogStyle
import com.swifttechnology.bookingsystem.shared.components.ReusableAlertDialog
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import com.swifttechnology.bookingsystem.features.department.domain.model.Department

// Status styling data class
  

private data class StatusStyle(
    val label: String,
    val textColor: Color,
    val bgColor: Color,
    val borderColor: Color,
    val dotColor: Color
)

private fun resolveStatusStyle(status: String, dark: Boolean): StatusStyle = when (status.lowercase()) {
    "active" -> if (dark) {
        StatusStyle(
            label       = "Active",
            textColor   = Color(0xFF86EFAC),
            bgColor     = Color(0xFF14532D),
            borderColor = Color(0xFF166534),
            dotColor    = Color(0xFF34C759)
        )
    } else {
        StatusStyle(
            label       = "Active",
            textColor   = Color(0xFF1A7F4B),
            bgColor     = Color(0xFFEAF7F0),
            borderColor = Color(0xFFA3D9BC),
            dotColor    = Color(0xFF34C759)
        )
    }
    else -> if (dark) {
        StatusStyle(
            label       = "Inactive",
            textColor   = Color(0xFFFCA5A5),
            bgColor     = Color(0xFF450A0A),
            borderColor = Color(0xFF7F1D1D),
            dotColor    = Color(0xFFFF3B30)
        )
    } else {
        StatusStyle(
            label       = "Inactive",
            textColor   = Color(0xFFB02020),
            bgColor     = Color(0xFFFFF0F0),
            borderColor = Color(0xFFF5BABA),
            dotColor    = Color(0xFFFF3B30)
        )
    }
}

  
// Screen
  

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(
    searchQuery: String,
    isEditable: Boolean = false,
    onNavigate: (String) -> Unit,
    onEditSelected: (Participant) -> Unit = {},
    onEditGroupSelected: (CustomGroup) -> Unit = {},
    onEnterEditMode: () -> Unit = {},
    onExitEditMode: () -> Unit = {},
    viewModel: ParticipantsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedParticipantIds by remember { mutableStateOf(setOf<Long>()) }
    var selectedGroupIds by remember { mutableStateOf(setOf<Long>()) }
    var showDeleteGroupDialog by remember { mutableStateOf(false) }

    LaunchedEffect(isEditable) {
        if (!isEditable) {
            selectedParticipantIds = emptySet()
            selectedGroupIds = emptySet()
        }
    }

    LaunchedEffect(uiState.selectedTab) {
        onExitEditMode()
    }

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    val filteredCustomGroups = remember(uiState.customGroups, uiState.searchQuery) {
        val q = uiState.searchQuery.trim()
        if (q.isEmpty()) uiState.customGroups
        else uiState.customGroups.filter { g ->
            g.name.contains(q, ignoreCase = true) ||
                g.description.contains(q, ignoreCase = true)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ParticipantsTabRow(
                selectedTab      = uiState.selectedTab,
                onTabSelected    = viewModel::onTabSelected,
                customGroupsCount = uiState.customGroups.size,
                departmentsCount = uiState.departments.size
            )
            Spacer(modifier = Modifier.height(8.dp))

            val density = LocalDensity.current
            val swipeThreshold = with(density) { 100.dp.toPx() }
            val tabs = ParticipantsTab.values()
            val currentIndex = tabs.indexOf(uiState.selectedTab)
            // Track whether a card swipe is in progress so tab-swipe is suppressed
            var isCardSwiping by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .pointerInput(uiState.selectedTab, isEditable) {
                        // Tab-swipe is only active when NOT in edit mode and no card is being swiped
                        if (isEditable) return@pointerInput
                        var dragAccumulator = 0f
                        detectHorizontalDragGestures(
                            onDragStart = { isCardSwiping = false },
                            onDragEnd = {
                                if (!isCardSwiping) {
                                    if (dragAccumulator > swipeThreshold) {
                                        if (currentIndex > 0) {
                                            viewModel.onTabSelected(tabs[currentIndex - 1])
                                        }
                                    } else if (dragAccumulator < -swipeThreshold) {
                                        if (currentIndex < tabs.size - 1) {
                                            viewModel.onTabSelected(tabs[currentIndex + 1])
                                        }
                                    }
                                }
                                dragAccumulator = 0f
                                isCardSwiping = false
                            },
                            onDragCancel = {
                                dragAccumulator = 0f
                                isCardSwiping = false
                            },
                            onHorizontalDrag = { change, dragAmount ->
                                if (!isCardSwiping) {
                                    change.consume()
                                    dragAccumulator += dragAmount
                                }
                            }
                        )
                    }
            ) {
                when {
                    uiState.isLoading    -> LoadingContent()
                    uiState.error != null -> ErrorContent(uiState.error!!)
                    else -> {
                        when (uiState.selectedTab) {
                            ParticipantsTab.ALL_PARTICIPANTS -> {
                                DirectoryContent(
                                    modifier = Modifier.fillMaxSize(),
                                    participants       = uiState.participants,
                                    searchQuery        = uiState.searchQuery,
                                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                    isEditable         = isEditable,
                                    selectedParticipantIds = selectedParticipantIds,
                                    onSelectionChanged = { id, selected ->
                                        selectedParticipantIds = if (selected) {
                                            selectedParticipantIds + id
                                        } else {
                                            selectedParticipantIds - id
                                        }
                                    },
                                    onLongPress = {
                                        if (!isEditable) onEnterEditMode()
                                    },
                                    onDisableParticipant = { id -> viewModel.toggleParticipantStatus(id, "INACTIVE") },
                                    onEnableParticipant  = { id -> viewModel.toggleParticipantStatus(id, "ACTIVE") }
                                )
                            }
                            ParticipantsTab.CUSTOM_GROUPS -> {
                                CustomGroupsDirectoryContent(
                                    modifier = Modifier.fillMaxSize(),
                                    groups = filteredCustomGroups,
                                    searchQuery = uiState.searchQuery,
                                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                                    expandedGroupId = uiState.expandedCustomGroupId,
                                    participants = uiState.participants,
                                    resolvedUserNames = uiState.resolvedUserNames,
                                    onMembersRowClick = viewModel::onCustomGroupMembersClick,
                                    isEditable = isEditable,
                                    selectedGroupIds = selectedGroupIds,
                                    onSelectionChanged = { id, selected ->
                                        selectedGroupIds = if (selected) {
                                            selectedGroupIds + id
                                        } else {
                                            selectedGroupIds - id
                                        }
                                    },
                                    onLongPress = {
                                        if (!isEditable) onEnterEditMode()
                                    }
                                )
                            }
                            ParticipantsTab.DEPARTMENTS -> {
                                val filteredDepartments = if (uiState.searchQuery.isBlank()) {
                                    uiState.departments
                                } else {
                                    uiState.departments.filter {
                                        it.departmentName.contains(uiState.searchQuery, ignoreCase = true) ||
                                        (it.description?.contains(uiState.searchQuery, ignoreCase = true) == true)
                                    }
                                }
                                DepartmentsDirectoryContent(
                                    modifier = Modifier.fillMaxSize(),
                                    departments = filteredDepartments,
                                    searchQuery = uiState.searchQuery,
                                    onSearchQueryChanged = viewModel::onSearchQueryChanged
                                )
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = !isEditable,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            AddFab(
                onClick  = {
                    when (uiState.selectedTab) {
                        ParticipantsTab.ALL_PARTICIPANTS -> {
                            onNavigate(com.swifttechnology.bookingsystem.navigation.ScreenRoutes.PARTICIPANT_ADD)
                        }
                        ParticipantsTab.CUSTOM_GROUPS -> {
                            onNavigate(com.swifttechnology.bookingsystem.navigation.ScreenRoutes.CUSTOM_GROUP_ADD)
                        }
                        ParticipantsTab.DEPARTMENTS -> {
                            onNavigate(com.swifttechnology.bookingsystem.navigation.ScreenRoutes.DEPARTMENT_ADD)
                        }
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = isEditable,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            val isOnAllTab = uiState.selectedTab == ParticipantsTab.ALL_PARTICIPANTS
            val editEnabled = if (isOnAllTab) selectedParticipantIds.size == 1 else selectedGroupIds.size == 1
            val deleteEnabled = if (isOnAllTab) selectedParticipantIds.isNotEmpty() else selectedGroupIds.isNotEmpty()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            if (isOnAllTab) {
                                val selectedId = selectedParticipantIds.firstOrNull()
                                val participant = uiState.participants.find { it.id == selectedId }
                                if (participant != null) {
                                    onEditSelected(participant)
                                }
                            } else {
                                val selectedId = selectedGroupIds.firstOrNull()
                                val group = uiState.customGroups.find { it.id == selectedId }
                                if (group != null) {
                                    onEditGroupSelected(group)
                                }
                            }
                        },
                        enabled = editEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            if (isOnAllTab) {
                                /* Handle participant delete logic here */
                            } else {
                                showDeleteGroupDialog = true
                            }
                        },
                        enabled = deleteEnabled,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    ) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Delete", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        if (showDeleteGroupDialog) {
            ReusableAlertDialog(
                style = DialogStyle.WARNING,
                title = "Delete Custom Group${if (selectedGroupIds.size > 1) "s" else ""}",
                message = "Are you sure you want to delete ${selectedGroupIds.size} custom group${if (selectedGroupIds.size > 1) "s" else ""}? This action cannot be undone.",
                confirmText = "Delete",
                onConfirm = {
                    viewModel.deleteCustomGroups(selectedGroupIds)
                    selectedGroupIds = emptySet()
                    showDeleteGroupDialog = false
                },
                onDismiss = { showDeleteGroupDialog = false }
            )
        }

    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomGroupsDirectoryContent(
    modifier: Modifier = Modifier,
    groups: List<CustomGroup>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    expandedGroupId: Long?,
    participants: List<Participant>,
    resolvedUserNames: Map<Long, String>,
    onMembersRowClick: (Long) -> Unit,
    isEditable: Boolean = false,
    selectedGroupIds: Set<Long> = emptySet(),
    onSelectionChanged: (Long, Boolean) -> Unit = { _, _ -> },
    onLongPress: () -> Unit = {}
) {
    Column(modifier = modifier) {
        Text(
            text = "Members Directory",
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DirectorySearchBar(
            query = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(groups, key = { it.id }) { group ->
                CustomGroupCard(
                    group = group,
                    isExpanded = expandedGroupId == group.id,
                    memberDisplayNames = memberDisplayNamesForGroup(group, participants, resolvedUserNames),
                    onMembersRowClick = { onMembersRowClick(group.id) },
                    isEditable = isEditable,
                    isSelected = selectedGroupIds.contains(group.id),
                    onSelectionChange = { selected ->
                        onSelectionChanged(group.id, selected)
                    },
                    onLongPress = {
                        onLongPress()
                        onSelectionChanged(group.id, true)
                    }
                )
            }
        }
    }
}

private fun memberDisplayNamesForGroup(
    group: CustomGroup,
    participants: List<Participant>,
    resolvedUserNames: Map<Long, String>
): List<String> =
    group.memberIds.map { id ->
        participants.find { it.id == id }?.name
            ?: group.preResolvedMemberNames[id]
            ?: resolvedUserNames[id]
            ?: "User #$id"
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomGroupCard(
    group: CustomGroup,
    isExpanded: Boolean,
    memberDisplayNames: List<String>,
    onMembersRowClick: () -> Unit,
    isEditable: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(250),
        label = "chevron"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(isEditable) {
                detectTapGestures(
                    onTap = {
                        if (isEditable) {
                            onSelectionChange(!isSelected)
                        }
                    },
                    onLongPress = {
                        if (!isEditable) {
                            onLongPress()
                        }
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isEditable,
            enter = fadeIn(animationSpec = tween(250)) + expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(250)
            ) + slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = tween(250)
            ),
            exit = fadeOut(animationSpec = tween(250)) + shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(250)
            ) + slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(250)
            )
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF007AFF)
                ),
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (group.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = group.description,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onMembersRowClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Members : ${group.memberCount}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            memberDisplayNames.forEach { name ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(
                                        0.5.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                                    )
                                ) {
                                    Text(
                                        text = name,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DepartmentsDirectoryContent(
    modifier: Modifier = Modifier,
    departments: List<Department>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text       = "Departments",
            fontWeight = FontWeight.SemiBold,
            fontSize   = 17.sp,
            color      = MaterialTheme.colorScheme.onSurface,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DirectorySearchBar(
            query          = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(departments, key = { it.id }) { department ->
                DepartmentCard(department = department)
            }
        }
    }
}

@Composable
fun DepartmentCard(department: Department) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(20.dp),
            color  = MaterialTheme.colorScheme.background,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text       = department.departmentName,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                if (!department.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text      = department.description,
                        fontSize  = 14.sp,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

// Tab row
  

@Composable
fun ParticipantsTabRow(
    selectedTab: ParticipantsTab,
    onTabSelected: (ParticipantsTab) -> Unit,
    customGroupsCount: Int,
    departmentsCount: Int
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(color = Color.Transparent) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp)
            ) {
                ParticipantsTabItem(
                    label      = "All Members",
                    icon       = Icons.Outlined.People,
                    isSelected = selectedTab == ParticipantsTab.ALL_PARTICIPANTS,
                    modifier   = Modifier.weight(1f),
                    onClick    = { onTabSelected(ParticipantsTab.ALL_PARTICIPANTS) }
                )
                ParticipantsTabItem(
                    label      = "Custom Groups",
                    icon       = Icons.Outlined.FolderOpen,
                    isSelected = selectedTab == ParticipantsTab.CUSTOM_GROUPS,
                    modifier   = Modifier.weight(1f),
                    onClick    = { onTabSelected(ParticipantsTab.CUSTOM_GROUPS) }
                )
                ParticipantsTabItem(
                    label      = "Departments",
                    icon       = Icons.Outlined.Business,
                    isSelected = selectedTab == ParticipantsTab.DEPARTMENTS,
                    modifier   = Modifier.weight(1f),
                    onClick    = { onTabSelected(ParticipantsTab.DEPARTMENTS) }
                )
            }
        }
        
        // Line with shadow effect that shows the toggle boundary
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .shadow(elevation = 3.dp)
                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun ParticipantsTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val activeColor   = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val tint          = if (isSelected) activeColor else inactiveColor

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp)
        ) {
            Icon(
                imageVector    = icon,
                contentDescription = null,
                tint           = tint,
                modifier       = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text       = label,
                color      = tint,
                fontSize   = 12.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                modifier   = Modifier.weight(1f, fill = false)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(3.dp)
                .shadow(
                    elevation = if (isSelected) 2.dp else 0.dp,
                    shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)
                )
                .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                .background(if (isSelected) activeColor else Color.Transparent)
        )
    }
}

  
// Directory content
  

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectoryContent(
    modifier: Modifier = Modifier,
    participants: List<Participant>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    isEditable: Boolean = false,
    selectedParticipantIds: Set<Long> = emptySet(),
    onSelectionChanged: (Long, Boolean) -> Unit = { _, _ -> },
    onLongPress: () -> Unit = {},
    onDisableParticipant: (Long) -> Unit = {},
    onEnableParticipant:  (Long) -> Unit = {}
) {
    var participantToDisable by remember { mutableStateOf<Participant?>(null) }
    var participantToEnable  by remember { mutableStateOf<Participant?>(null) }

    Column(modifier = modifier) {
        Text(
            text       = "Members Directory",
            fontWeight = FontWeight.SemiBold,
            fontSize   = 17.sp,
            color      = MaterialTheme.colorScheme.onSurface,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DirectorySearchBar(
            query          = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(participants, key = { it.id }) { participant ->
                val isActive = participant.status.equals("active", ignoreCase = true)

                val dismissState = rememberSwipeToDismissBoxState(
                    confirmValueChange = { dismissValue ->
                        if (dismissValue == SwipeToDismissBoxValue.StartToEnd) {
                            if (isActive) {
                                participantToDisable = participant
                            } else {
                                participantToEnable = participant
                            }
                            false // always snap back; action confirmed via dialog
                        } else {
                            false
                        }
                    },
                    positionalThreshold = { it * 0.5f }
                )

                // Disable the SwipeToDismissBox entirely while in edit mode to avoid
                // conflict with checkbox selection gestures
                val swipeEnabled = !isEditable

                SwipeToDismissBox(
                    state = dismissState,
                    enableDismissFromStartToEnd = swipeEnabled,
                    enableDismissFromEndToStart = false,
                    backgroundContent = {
                        // Animate the background alpha so it only becomes visible AFTER
                        // the checkbox AnimatedVisibility exit animation finishes (250 ms).
                        // - Entering edit mode  → go to 0 immediately (no delay)
                        // - Exiting  edit mode  → wait 250 ms, then fade in over 150 ms
                        val bgAlpha by animateFloatAsState(
                            targetValue    = if (isEditable) 0f else 1f,
                            animationSpec  = if (isEditable)
                                tween(durationMillis = 0)
                            else
                                tween(durationMillis = 150, delayMillis = 250),
                            label = "swipeBgAlpha"
                        )

                        // Nothing to draw until the alpha is meaningful
                        if (bgAlpha == 0f) return@SwipeToDismissBox

                        val bgColor  = if (isActive) Color(0xFFFF3B30).copy(alpha = 0.18f * bgAlpha)
                                       else          Color(0xFF007AFF).copy(alpha = 0.15f * bgAlpha)
                        val iconTint = if (isActive) Color(0xFFFF3B30).copy(alpha = bgAlpha)
                                       else          Color(0xFF007AFF).copy(alpha = bgAlpha)
                        val icon     = if (isActive) Icons.Outlined.Block else Icons.Outlined.CheckCircle
                        val iconDesc = if (isActive) "Disable" else "Enable"

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(bgColor)
                                .padding(horizontal = 20.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Icon(
                                imageVector        = icon,
                                contentDescription = iconDesc,
                                tint               = iconTint,
                                modifier           = Modifier.size(26.dp)
                            )
                        }
                    }
                ) {
                    ParticipantCard(
                        participant = participant,
                        isEditable = isEditable,
                        isSelected = selectedParticipantIds.contains(participant.id),
                        onSelectionChange = { selected ->
                            onSelectionChanged(participant.id, selected)
                        },
                        onLongPress = {
                            onLongPress()
                            onSelectionChanged(participant.id, true)
                        }
                    )
                }
            }
        }
    }

    // Disable dialog
    if (participantToDisable != null) {
        AlertDialog(
            onDismissRequest = { participantToDisable = null },
            title = { Text("Disable User") },
            text = { Text("Are you sure you want to disable ${participantToDisable?.name}? They will no longer be able to access the system.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDisableParticipant(participantToDisable!!.id)
                        participantToDisable = null
                    }
                ) {
                    Text("Disable", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { participantToDisable = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Enable dialog
    if (participantToEnable != null) {
        AlertDialog(
            onDismissRequest = { participantToEnable = null },
            title = { Text("Enable User") },
            text = { Text("Are you sure you want to re-enable ${participantToEnable?.name}? They will regain access to the system.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEnableParticipant(participantToEnable!!.id)
                        participantToEnable = null
                    }
                ) {
                    Text("Enable", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { participantToEnable = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DirectorySearchBar(
    query: String,
    onQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChanged,
        placeholder   = {
            Text(
                "Search by name, email, or department...",
                color    = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                modifier           = Modifier.size(18.dp)
            )
        },
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = OutlinedTextFieldDefaults.colors(
            focusedBorderColor     = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            unfocusedBorderColor   = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
            focusedContainerColor  = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        singleLine = true,
        textStyle  = LocalTextStyle.current.copy(fontSize = 12.sp)
    )
}

  
// Participant card
  

@Composable
fun ParticipantCard(
    participant: Participant,
    isEditable: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .pointerInput(isEditable) {
                detectTapGestures(
                    onTap = {
                        if (isEditable) {
                            onSelectionChange(!isSelected)
                        }
                    },
                    onLongPress = {
                        if (!isEditable) {
                            onLongPress()
                        }
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isEditable,
            enter = fadeIn(animationSpec = tween(250)) + expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(250)
            ) + slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = tween(250)
            ),
            exit = fadeOut(animationSpec = tween(250)) + shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(250)
            ) + slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(250)
            )
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(20.dp),
            color  = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp)
            ) {
            //    Top row: name block + role badge               
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = participant.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 14.sp,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = participant.position,
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = participant.department,
                        fontSize = 11.sp,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (participant.status.equals("active", ignoreCase = true)) {
                    RoleBadge(role = participant.role)
                } else {
                    // Show a "DISABLED" badge in place of the role badge
                    DisabledBadge()
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            //    Divider                                        
            HorizontalDivider(
                color     = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                thickness = 0.5.dp
            )

            Spacer(modifier = Modifier.height(6.dp))

            //    Bottom row: contact info + status pill         
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    ContactRow(icon = Icons.Outlined.Email, text = participant.email)
                    Spacer(modifier = Modifier.height(4.dp))
                    ContactRow(icon = Icons.Outlined.Phone, text = participant.phone)
                }
            }
            }
        }
    }
}

  
// Status pill 
  

@Composable
fun StatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val style = resolveStatusStyle(status, dark = isSystemInDarkTheme())

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(style.bgColor)
            .border(
                width  = 0.5.dp,
                color  = style.borderColor,
                shape  = RoundedCornerShape(20.dp)
            )
            .padding(start = 7.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(style.dotColor)
        )
        Text(
            text       = style.label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = style.textColor
        )
    }
}

  
// Role badge
  

@Composable
fun RoleBadge(role: String) {
    val uppercaseRole = role.uppercase()

    val (textColor, bgColor, borderColor) = when (uppercaseRole) {
        "ADMIN"   -> Triple(
            Color(0xFF185FA5),
            Color(0xFFEEF4FF),
            Color(0xFFB5D4F4)
        )
        "MANAGER" -> Triple(
            Color(0xFF6A3BAF),
            Color(0xFFF5EEFF),
            Color(0xFFD9BEFF)
        )
        "STAFF"   -> Triple(
            Color(0xFF3B6D11),
            Color(0xFFEAF3DE),
            Color(0xFFC0DD97)
        )
        else      -> Triple(
            Color(0xFF3C3C43).copy(alpha = 0.7f),
            Color.Transparent,
            Color(0xFF3C3C43).copy(alpha = 0.2f)
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width  = 0.5.dp,
                color  = borderColor,
                shape  = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text       = uppercaseRole,
            fontSize   = 11.sp,
            color      = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

  
// Disabled badge (shown when participant status is not active)
  

@Composable
fun DisabledBadge() {
    val isDark = isSystemInDarkTheme()
    val textColor   = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB02020)
    val bgColor     = if (isDark) Color(0xFF450A0A) else Color(0xFFFFF0F0)
    val borderColor = if (isDark) Color(0xFF7F1D1D) else Color(0xFFF5BABA)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width = 0.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector        = Icons.Outlined.Block,
            contentDescription = null,
            tint               = textColor,
            modifier           = Modifier.size(10.dp)
        )
        Text(
            text       = "DISABLED",
            fontSize   = 11.sp,
            color      = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

  
// Contact row
  

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text     = text,
            fontSize = 12.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

  
// Loading / Error states
  

@Composable
fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text     = "Error: $message",
            color    = MaterialTheme.colorScheme.error,
            fontSize = 14.sp
        )
    }
}