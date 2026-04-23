package com.swifttechnology.bookingsystem.features.booking.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Info
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.Neutral200
import com.swifttechnology.bookingsystem.core.designsystem.Neutral300
import com.swifttechnology.bookingsystem.core.designsystem.Neutral400
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.presentation.InternalMember
import com.swifttechnology.bookingsystem.features.booking.presentation.RoomBookingFormState
import com.swifttechnology.bookingsystem.features.participants.presentation.components.CustomGroupCard

enum class GroupBy { PEOPLE, TEAMS, GROUPS }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InternalParticipantsBottomSheet(
    formState: RoomBookingFormState,
    availableParticipants: List<InternalMember>,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    onClose: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isSearching: Boolean = false
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var groupBy by remember { mutableStateOf(GroupBy.PEOPLE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = statusBarPadding)
    ) {
        DragHandle()

        SheetHeader(title = "Internal Member", onClose = onClose)

        if (formState.participants.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                formState.participants.forEach { member ->
                    SelectedParticipantChip(
                        name = member.name,
                        onRemove = {
                            onFormStateChanged(
                                formState.copy(participants = formState.participants - member)
                            )
                        }
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f).imePadding()) {
            InternalParticipantsContent(
                formState = formState,
                onFormStateChanged = onFormStateChanged,
                groupBy = groupBy,
                onGroupByChange = { groupBy = it },
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                isSearching = isSearching,
                filteredParticipants = availableParticipants
            )
        }
    }
}

@Composable
private fun InternalParticipantsContent(
    formState: RoomBookingFormState,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    groupBy: GroupBy,
    onGroupByChange: (GroupBy) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredParticipants: List<InternalMember>,
    modifier: Modifier = Modifier,
    isSearching: Boolean = false,
    viewModel: ParticipantsSheetViewModel = hiltViewModel()
) {
    val apiDepartments by viewModel.departments.collectAsStateWithLifecycle()
    val customGroups by viewModel.customGroups.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    var departmentDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf<String?>(null) }
    var expandedGroupIds by remember { mutableStateOf(setOf<Long>()) }

    // local cache of resolved names for groups to avoid "User #123" flicker
    var locallyResolvedNames by remember { mutableStateOf(mapOf<Long, String>()) }

    val departments = remember(apiDepartments, filteredParticipants) {
        if (apiDepartments.isNotEmpty()) apiDepartments else filteredParticipants.map { it.department }.distinct().sorted()
    }

    val filteredGroups = remember(customGroups, searchQuery) {
        if (searchQuery.isBlank()) customGroups
        else customGroups.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val displayList = when (groupBy) {
        GroupBy.TEAMS -> if (selectedDepartment != null)
            filteredParticipants.filter { it.department == selectedDepartment }
        else filteredParticipants
        else -> filteredParticipants
    }

    // For GROUPS: a group is "selected" if its ID is in selectedGroupIds (purely UI tracking).
    // For PEOPLE/TEAMS: "all selected" means every visible member is in participants.
    // Logic for "all selected" is now handled per department in TEAMS view
    // and removed for PEOPLE/GROUPS as requested.

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                .border(
                    width = 0.5.dp,
                    color = Neutral300,
                    shape = RoundedCornerShape(CornerRadius.full)
                )
                .padding(3.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            listOf(
                GroupBy.PEOPLE to "People",
                GroupBy.TEAMS  to "Teams",
                GroupBy.GROUPS to "Group"
            ).forEach { (value, label) ->
                val isActive = groupBy == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(CornerRadius.full))
                        .clickable {
                            onGroupByChange(value)
                            if (value != GroupBy.TEAMS) selectedDepartment = null
                        }
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .padding(horizontal = 20.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.customColors.bookRoomLabel
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(CornerRadius.full))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                        .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.full))
                        .padding(horizontal = Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Neutral400,
                        modifier = Modifier.size(15.dp)
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = "Search name or email\u2026",
                                color = Neutral400,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor   = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor      = Color.Transparent,
                            unfocusedBorderColor    = Color.Transparent,
                            focusedTextColor        = MaterialTheme.customColors.bookRoomLabel,
                            unfocusedTextColor      = MaterialTheme.customColors.bookRoomLabel,
                            cursorColor             = MaterialTheme.colorScheme.primary
                        ),
                        singleLine = true,
                        textStyle  = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp)
                    )
                }
            }

            HorizontalDivider(color = Neutral300, thickness = 0.5.dp)

            if (groupBy == GroupBy.TEAMS) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(CornerRadius.lg))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
                            .clickable { departmentDropdownExpanded = true }
                            .padding(horizontal = Spacing.md, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedDepartment ?: "Select Department",
                            fontSize = 13.sp,
                            color = if (selectedDepartment != null)
                                MaterialTheme.customColors.bookRoomLabel else Neutral400
                        )
                        Icon(
                            imageVector = if (departmentDropdownExpanded)
                                Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Neutral700,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = departmentDropdownExpanded,
                        onDismissRequest = { departmentDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(MaterialTheme.colorScheme.surface)
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "All Departments",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.customColors.bookRoomLabel
                                )
                            },
                            onClick = {
                                selectedDepartment = null
                                departmentDropdownExpanded = false
                            }
                        )
                        HorizontalDivider(color = Neutral300, thickness = 0.5.dp)
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = dept,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.customColors.bookRoomLabel
                                    )
                                },
                                onClick = {
                                    selectedDepartment = dept
                                    departmentDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                HorizontalDivider(color = Neutral300, thickness = 0.5.dp)
            }

            if (isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = Neutral200
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (groupBy) {
                    GroupBy.TEAMS -> {
                        val grouped = displayList.groupBy { it.department }
                        grouped.forEach { (department, members) ->
                            item(key = "dept_$department") {
                                val isDeptSelected = members.isNotEmpty() && members.all { p -> formState.participants.any { it.id == p.id } }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.06f))
                                        .padding(horizontal = Spacing.md, vertical = 5.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = department,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(CornerRadius.sm))
                                            .clickable {
                                                if (isDeptSelected) {
                                                    val removeIds = members.map { it.id }.toSet()
                                                    onFormStateChanged(formState.copy(
                                                        participants = formState.participants.filterNot { it.id in removeIds }
                                                    ))
                                                } else {
                                                    val merged = (formState.participants + members).distinctBy { it.id }
                                                    onFormStateChanged(formState.copy(participants = merged))
                                                }
                                            }
                                            .padding(2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (isDeptSelected) MaterialTheme.colorScheme.primary
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    width = 1.2.dp,
                                                    color = if (isDeptSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                                    shape = RoundedCornerShape(4.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isDeptSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.size(10.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            items(members, key = { it.id }) { participant ->
                                InternalParticipantItem(
                                    participant = participant,
                                    isSelected = formState.participants.any { it.id == participant.id },
                                    onToggleSelection = {
                                        toggleParticipant(formState, participant, onFormStateChanged)
                                    }
                                )
                                if (members.last() != participant) {
                                    HorizontalDivider(
                                        color = Neutral300,
                                        thickness = 0.5.dp,
                                        modifier = Modifier.padding(horizontal = Spacing.md)
                                    )
                                }
                            }
                        }
                    }
                    GroupBy.GROUPS -> {
                        items(filteredGroups, key = { it.id }) { group ->
                            val isGroupSelected = group.id in formState.selectedGroupIds
                            val memberNames = group.memberIds.map { id ->
                                filteredParticipants.find { it.id == id }?.name 
                                    ?: locallyResolvedNames[id] 
                                    ?: "User #$id"
                            }

                            CustomGroupCard(
                                group = group,
                                isExpanded = expandedGroupIds.contains(group.id),
                                memberDisplayNames = memberNames,
                                onMembersRowClick = {
                                    val isExploring = !expandedGroupIds.contains(group.id)
                                    if (isExploring) {
                                        expandedGroupIds = expandedGroupIds + group.id
                                        // Auto-resolve names when expanding if they look like "User #ID"
                                        val missingIds = group.memberIds.filter { id ->
                                            filteredParticipants.none { it.id == id } && !locallyResolvedNames.containsKey(id)
                                        }
                                        if (missingIds.isNotEmpty()) {
                                            scope.launch {
                                                val resolved = viewModel.resolveParticipantsByIds(missingIds)
                                                locallyResolvedNames = locallyResolvedNames + resolved.associate { it.id to it.name }
                                            }
                                        }
                                    } else {
                                        expandedGroupIds = expandedGroupIds - group.id
                                    }
                                },
                                isEditable = true,
                                isSelected = isGroupSelected,
                                onSelectionChange = { selected ->
                                    if (selected) {
                                        scope.launch {
                                            val resolvedGroupMembers = viewModel.resolveParticipantsByIds(group.memberIds)
                                            val merged = (formState.participants + resolvedGroupMembers).distinctBy { it.id }
                                            onFormStateChanged(formState.copy(
                                                participants = merged,
                                                selectedGroupIds = formState.selectedGroupIds + group.id
                                            ))
                                        }
                                    } else {
                                        // Remove group ID from selectedGroupIds.
                                        // Only remove members whose IDs are NOT present in any
                                        // other still-selected group — so shared members are kept.
                                        val remainingGroupIds = formState.selectedGroupIds - group.id
                                        val remainingGroupMemberIds = customGroups
                                            .filter { it.id in remainingGroupIds }
                                            .flatMap { it.memberIds }
                                            .toSet()
                                        onFormStateChanged(formState.copy(
                                            participants = formState.participants.filterNot { p ->
                                                p.id in group.memberIds && p.id !in remainingGroupMemberIds
                                            },
                                            selectedGroupIds = remainingGroupIds
                                        ))
                                    }
                                }
                            )
                        }
                    }
                    else -> {
                        items(displayList, key = { it.id }) { participant ->
                            InternalParticipantItem(
                                participant = participant,
                                isSelected = formState.participants.any { it.id == participant.id },
                                onToggleSelection = {
                                    toggleParticipant(formState, participant, onFormStateChanged)
                                }
                            )
                            if (displayList.last() != participant) {
                                HorizontalDivider(
                                    color = Neutral300,
                                    thickness = 0.5.dp,
                                    modifier = Modifier.padding(horizontal = Spacing.md)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun toggleParticipant(
    formState: RoomBookingFormState,
    participant: InternalMember,
    onFormStateChanged: (RoomBookingFormState) -> Unit
) {
    val current = formState.participants.toMutableList()
    if (current.any { it.id == participant.id }) {
        current.removeAll { it.id == participant.id }
    } else {
        current.add(participant)
    }
    // selectedGroupIds is intentionally left unchanged here.
    onFormStateChanged(formState.copy(participants = current.distinct()))
}

@Composable
private fun InternalParticipantItem(
    participant: InternalMember,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleSelection() }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                )
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Neutral400,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = participant.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.bookRoomLabel,
                lineHeight = 16.sp
            )
            Text(
                text = participant.email,
                fontSize = 11.sp,
                color = Neutral700,
                lineHeight = 14.sp
            )
            Text(
                text = participant.department,
                fontSize = 11.sp,
                color = Info,
                lineHeight = 14.sp
            )
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InternalParticipantsBottomSheetPreview() {
    MeetingRoomBookingTheme {
        InternalParticipantsBottomSheet(
            formState = RoomBookingFormState(
                participants = listOf(
                    InternalMember(1L, "John Carter", "john.carter@fintech.com", "Engineering"),
                    InternalMember(2L, "Emily Davis", "emily.davis@fintech.com", "Product")
                )
            ),
            availableParticipants = listOf(
                InternalMember(1L, "John Carter", "john.carter@fintech.com", "Engineering"),
                InternalMember(2L, "Emily Davis", "emily.davis@fintech.com", "Product"),
                InternalMember(3L, "Sarah Kim",   "sarah.kim@fintech.com",   "Engineering"),
                InternalMember(4L, "Mike Torres", "mike.torres@fintech.com", "Design"),
                InternalMember(5L, "Priya Patel", "priya.patel@fintech.com", "Product")
            ),
            onFormStateChanged = {},
            onClose = {},
            searchQuery = "",
            onSearchQueryChange = {}
        )
    }
}
