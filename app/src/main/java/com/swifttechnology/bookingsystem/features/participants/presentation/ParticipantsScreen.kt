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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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

private fun resolveStatusStyle(status: String): StatusStyle = when (status.lowercase()) {
    "active" -> StatusStyle(
        label       = "Active",
        textColor   = Color(0xFF1A7F4B),
        bgColor     = Color(0xFFEAF7F0),
        borderColor = Color(0xFFA3D9BC),
        dotColor    = Color(0xFF34C759)
    )
    else -> StatusStyle(
        label       = "Inactive",
        textColor   = Color(0xFFB02020),
        bgColor     = Color(0xFFFFF0F0),
        borderColor = Color(0xFFF5BABA),
        dotColor    = Color(0xFFFF3B30)
    )
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

            when {
                uiState.isLoading    -> LoadingContent()
                uiState.error != null -> ErrorContent(uiState.error!!)
                else -> {
                    when (uiState.selectedTab) {
                        ParticipantsTab.ALL_PARTICIPANTS -> {
                            DirectoryContent(
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
                                }
                            )
                        }
                        ParticipantsTab.CUSTOM_GROUPS -> {
                            CustomGroupsDirectoryContent(
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
                                departments = filteredDepartments,
                                searchQuery = uiState.searchQuery,
                                onSearchQueryChanged = viewModel::onSearchQueryChanged
                            )
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
                    .background(Color.White)
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
                            containerColor = Color(0xFFEBEBF5),
                            contentColor = Color(0xFF1C1C1E),
                            disabledContainerColor = Color(0xFFF2F2F7),
                            disabledContentColor = Color(0xFF3C3C43).copy(alpha = 0.3f)
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
                            containerColor = Color(0xFFFFD4D4),
                            contentColor = Color(0xFF1C1C1E),
                            disabledContainerColor = Color(0xFFF2F2F7),
                            disabledContentColor = Color(0xFF3C3C43).copy(alpha = 0.3f)
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
    Column {
        Text(
            text = "Members Directory",
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = Color(0xFF1C1C1E),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DirectorySearchBar(
            query = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
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
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFF3C3C43).copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1C1C1E)
                )
                if (group.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = group.description,
                        fontSize = 14.sp,
                        color = Color(0xFF3C3C43).copy(alpha = 0.65f),
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
                        tint = Color(0xFF3C3C43).copy(alpha = 0.55f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Members : ${group.memberCount}",
                        fontSize = 14.sp,
                        color = Color(0xFF3C3C43).copy(alpha = 0.75f),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF3C3C43).copy(alpha = 0.55f),
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
                                    color = Color(0xFFF2F2F7),
                                    border = BorderStroke(
                                        0.5.dp,
                                        Color(0xFF3C3C43).copy(alpha = 0.12f)
                                    )
                                ) {
                                    Text(
                                        text = name,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 13.sp,
                                        color = Color(0xFF1C1C1E),
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
    departments: List<Department>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    Column {
        Text(
            text       = "Departments",
            fontWeight = FontWeight.SemiBold,
            fontSize   = 17.sp,
            color      = Color(0xFF1C1C1E),
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DirectorySearchBar(
            query          = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
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
            color  = Color.White,
            border = BorderStroke(1.dp, Color(0xFF3C3C43).copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text       = department.departmentName,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = Color(0xFF1C1C1E)
                )
                if (!department.description.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text      = department.description,
                        fontSize  = 14.sp,
                        color     = Color(0xFF3C3C43).copy(alpha = 0.65f),
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
    Surface(color = Color.Transparent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            ParticipantsTabItem(
                label      = "All Members",
                icon       = Icons.Outlined.People,
                isSelected = selectedTab == ParticipantsTab.ALL_PARTICIPANTS,
                badge      = null,
                modifier   = Modifier.weight(1f),
                onClick    = { onTabSelected(ParticipantsTab.ALL_PARTICIPANTS) }
            )
            ParticipantsTabItem(
                label      = "Custom Groups",
                icon       = Icons.Outlined.FolderOpen,
                isSelected = selectedTab == ParticipantsTab.CUSTOM_GROUPS,
                badge      = customGroupsCount,
                modifier   = Modifier.weight(1f),
                onClick    = { onTabSelected(ParticipantsTab.CUSTOM_GROUPS) }
            )
            ParticipantsTabItem(
                label      = "Departments",
                icon       = Icons.Outlined.Business,
                isSelected = selectedTab == ParticipantsTab.DEPARTMENTS,
                badge      = departmentsCount,
                modifier   = Modifier.weight(1f),
                onClick    = { onTabSelected(ParticipantsTab.DEPARTMENTS) }
            )
        }
    }
}

@Composable
fun ParticipantsTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    badge: Int?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val activeColor   = Color(0xFF007AFF)
    val inactiveColor = Color(0xFF3C3C43).copy(alpha = 0.6f)
    val tint          = if (isSelected) activeColor else inactiveColor

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector    = icon,
                contentDescription = null,
                tint           = tint,
                modifier       = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text       = label,
                color      = tint,
                fontSize   = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) activeColor
                            else Color(0xFF3C3C43).copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text       = badge.toString(),
                        color      = if (isSelected) Color.White else Color(0xFF3C3C43),
                        fontSize   = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (isSelected) activeColor else Color.Transparent)
        )
    }
}

  
// Directory content
  

@Composable
fun DirectoryContent(
    participants: List<Participant>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    isEditable: Boolean = false,
    selectedParticipantIds: Set<Long> = emptySet(),
    onSelectionChanged: (Long, Boolean) -> Unit = { _, _ -> },
    onLongPress: () -> Unit = {}
) {
    Column {
        Text(
            text       = "Members Directory",
            fontWeight = FontWeight.SemiBold,
            fontSize   = 17.sp,
            color      = Color(0xFF1C1C1E),
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )

        DirectorySearchBar(
            query          = searchQuery,
            onQueryChanged = onSearchQueryChanged
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(participants, key = { it.id }) { participant ->
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
                color    = Color(0xFF3C3C43).copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint               = Color(0xFF3C3C43).copy(alpha = 0.4f),
                modifier           = Modifier.size(18.dp)
            )
        },
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(12.dp),
        colors    = OutlinedTextFieldDefaults.colors(
            focusedBorderColor     = Color(0xFF007AFF).copy(alpha = 0.5f),
            unfocusedBorderColor   = Color(0xFF3C3C43).copy(alpha = 0.15f),
            focusedContainerColor  = Color(0xFFF2F2F7),
            unfocusedContainerColor = Color(0xFFF2F2F7)
        ),
        singleLine = true,
        textStyle  = LocalTextStyle.current.copy(fontSize = 14.sp)
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
            shape  = RoundedCornerShape(20.dp),
            color  = Color.White,
            border = BorderStroke(1.dp, Color(0xFF3C3C43).copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
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
                        fontSize   = 16.sp,
                        color      = Color(0xFF1C1C1E)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = participant.position,
                        fontSize = 13.sp,
                        color    = Color(0xFF3C3C43).copy(alpha = 0.55f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = participant.department,
                        fontSize = 13.sp,
                        color    = Color(0xFF3C3C43).copy(alpha = 0.55f)
                    )
                }
                if (participant.status.equals("active", ignoreCase = true)) {
                    RoleBadge(role = participant.role)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //    Divider                                        
            HorizontalDivider(
                color     = Color(0xFF3C3C43).copy(alpha = 0.08f),
                thickness = 0.5.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            //    Bottom row: contact info + status pill         
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    ContactRow(icon = Icons.Outlined.Email, text = participant.email)
                    Spacer(modifier = Modifier.height(5.dp))
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
    val style = resolveStatusStyle(status)

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

  
// Contact row
  

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Color(0xFF3C3C43).copy(alpha = 0.45f),
            modifier           = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text     = text,
            fontSize = 13.sp,
            color    = Color(0xFF3C3C43).copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

  
// Loading / Error states
  

@Composable
fun LoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color(0xFF007AFF))
    }
}

@Composable
fun ErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text     = "Error: $message",
            color    = Color(0xFFFF3B30),
            fontSize = 14.sp
        )
    }
}