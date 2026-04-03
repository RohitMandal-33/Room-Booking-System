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
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
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
import com.swifttechnology.bookingsystem.core.designsystem.Warning
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.presentation.ExternalMember
import com.swifttechnology.bookingsystem.features.booking.presentation.InternalMember
import com.swifttechnology.bookingsystem.features.booking.presentation.RoomBookingFormState
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton


// Keep for backward-compat if anything still imports it

enum class ParticipantSheetMode { INTERNAL, EXTERNAL }


//  INTERNAL bottom-sheet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InternalParticipantsBottomSheet(
    formState: RoomBookingFormState,
    availableParticipants: List<InternalMember>,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    onClose: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean = false,
    modifier: Modifier = Modifier
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var groupBy by remember { mutableStateOf(GroupBy.PEOPLE) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = statusBarPadding)
    ) {
        // Drag handle
        DragHandle()

        // Header — title centred, close on right
        SheetHeader(title = "Internal Member", onClose = onClose)

        // Selected chips
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

        // Scrollable list
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

        // Done button
        PrimaryButton(
            text = "Done",
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        )
    }
}


//  EXTERNAL bottom-sheet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExternalParticipantsBottomSheet(
    formState: RoomBookingFormState,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var externalName by remember { mutableStateOf("") }
    var externalEmail by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = statusBarPadding)
    ) {
        // Drag handle
        DragHandle()

        // Header — title centred, close on right
        SheetHeader(title = "External Member", onClose = onClose)

        // Selected chips
        if (formState.externalMembers.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                formState.externalMembers.forEach { member ->
                    SelectedParticipantChip(
                        name = member.name,
                        onRemove = {
                            onFormStateChanged(
                                formState.copy(externalMembers = formState.externalMembers - member)
                            )
                        }
                    )
                }
            }
        }

        // Scrollable form
        Box(modifier = Modifier.weight(1f).imePadding()) {
            ExternalParticipantsContent(
                formState = formState,
                onFormStateChanged = onFormStateChanged,
                externalName = externalName,
                externalEmail = externalEmail,
                onExternalNameChange = { externalName = it },
                onExternalEmailChange = { externalEmail = it },
                onClearForm = {
                    externalName = ""
                    externalEmail = ""
                }
            )
        }

        // Done button
        PrimaryButton(
            text = "Done",
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        )
    }
}


//  Shared small composables


@Composable
private fun DragHandle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xs),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 4.dp)
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(Neutral300)
        )
    }
}

/** Title centred, X button pinned to the right. */
@Composable
private fun SheetHeader(title: String, onClose: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.customColors.bookRoomLabel,
            modifier = Modifier.align(Alignment.Center)
        )
        IconButton(
            onClick = onClose,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Neutral700
            )
        }
    }
}


//  Selected chip

@Composable
fun SelectedParticipantChip(name: String, onRemove: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(CornerRadius.full)
            )
            .padding(start = 6.dp, end = 2.dp, top = 2.dp, bottom = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $name",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(8.dp)
            )
        }
    }
}


//  GroupBy enum

private enum class GroupBy { PEOPLE, TEAMS, ALL }


//  Internal participants panel  (People / Teams / All tabs + search + list)

@Composable
private fun InternalParticipantsContent(
    formState: RoomBookingFormState,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    groupBy: GroupBy,
    onGroupByChange: (GroupBy) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    filteredParticipants: List<InternalMember>,
    isSearching: Boolean = false,
    modifier: Modifier = Modifier
) {
    // Department dropdown state (Teams tab only)
    var departmentDropdownExpanded by remember { mutableStateOf(false) }
    var selectedDepartment by remember { mutableStateOf<String?>(null) }
    val departments = remember(filteredParticipants) {
        filteredParticipants.map { it.department }.distinct().sorted()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
    ) {
        // Segmented control: People / Teams / All
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
        ) {
            listOf(
                GroupBy.PEOPLE to "People",
                GroupBy.TEAMS to "Teams",
                GroupBy.ALL to "All"
            ).forEach { (value, label) ->
                val isActive = groupBy == value
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onGroupByChange(value)
                            // Reset department filter when switching tabs
                            if (value != GroupBy.TEAMS) selectedDepartment = null
                        }
                        .background(
                            if (isActive) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isActive) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.customColors.bookRoomLabel
                    )
                }
            }
        }

        HorizontalDivider(color = Neutral300, thickness = 0.5.dp)

        //    Department dropdown (Teams tab only)                               
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
                        color = if (selectedDepartment != null) MaterialTheme.customColors.bookRoomLabel
                        else Neutral400
                    )
                    Icon(
                        imageVector = if (departmentDropdownExpanded) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
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
                    // "All departments" option
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

        //    Search bar                                                         
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Neutral400,
                modifier = Modifier.size(16.dp)
            )
            // Use OutlinedTextField (not TextField) so the placeholder always renders correctly
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search by name or email…",
                        color = Neutral400,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                    unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )
        }

        // Progress or divider
        if (isSearching) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Neutral200
            )
        } else {
            HorizontalDivider(color = Neutral300, thickness = 0.5.dp)
        }

        //    Participant list                                                   
        val displayList = when (groupBy) {
            GroupBy.TEAMS -> if (selectedDepartment != null)
                filteredParticipants.filter { it.department == selectedDepartment }
            else filteredParticipants
            else -> filteredParticipants
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
                            Text(
                                text = department,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
                                    )
                                    .padding(horizontal = Spacing.md, vertical = 5.dp)
                            )
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
    onFormStateChanged(formState.copy(participants = current.distinct()))
}


//  Single internal participant row

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
        // Checkbox
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

        // Avatar
//        Box(
//            modifier = Modifier
//                .size(34.dp)
//                .clip(CircleShape)
//                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = participant.name
//                    .split(" ")
//                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
//                    .take(2)
//                    .joinToString(""),
//                fontSize = 12.sp,
//                fontWeight = FontWeight.SemiBold,
//                color = MaterialTheme.colorScheme.primary
//            )
//        }

        // Name / email / department
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


//  External participants form panel

@Composable
private fun ExternalParticipantsContent(
    formState: RoomBookingFormState,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    externalName: String,
    externalEmail: String,
    onExternalNameChange: (String) -> Unit,
    onExternalEmailChange: (String) -> Unit,
    onClearForm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
    ) {
        // Add-form card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Add External Member",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.customColors.bookRoomLabel
                )
            }

            HorizontalDivider(color = Neutral300, thickness = 0.5.dp)

            ExternalFormField(
                label = "Name",
                value = externalName,
                placeholder = "Enter external member name",
                isRequired = true,
                onValueChange = onExternalNameChange
            )

            ExternalFormField(
                label = "Email",
                value = externalEmail,
                placeholder = "Enter external member email",
                isRequired = true,
                onValueChange = onExternalEmailChange
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(Color(0xFFFFF8E1))
                    .border(0.5.dp, Warning, RoundedCornerShape(CornerRadius.md))
                    .padding(Spacing.sm)
            ) {
                Text(
                    text = "External members are tracked separately in reports and won't affect internal dashboard statistics.",
                    fontSize = 11.sp,
                    color = Warning,
                    lineHeight = 16.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                OutlinedButton(
                    onClick = {
                        if (externalName.isNotBlank() && externalEmail.isNotBlank()) {
                            val newMember = ExternalMember(
                                name = externalName.trim(),
                                email = externalEmail.trim()
                            )
                            onFormStateChanged(
                                formState.copy(
                                    externalMembers = formState.externalMembers + newMember
                                )
                            )
                            onClearForm()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Add Member", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }

                OutlinedButton(
                    onClick = onClearForm,
                    modifier = Modifier
                        .weight(0.55f)
                        .height(44.dp),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Neutral700),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Neutral300)
                ) {
                    Text("Clear", fontSize = 13.sp)
                }
            }
        }

        // Added members list
        if (formState.externalMembers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.md))

            Text(
                text = "Added external members",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.bookRoomLabel,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
            ) {
                formState.externalMembers.forEachIndexed { index, member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Warning.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.name
                                        .split(" ")
                                        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                        .take(2)
                                        .joinToString(""),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Warning
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                                Text(
                                    text = member.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.customColors.bookRoomLabel,
                                    lineHeight = 16.sp
                                )
                                Text(
                                    text = member.email,
                                    fontSize = 11.sp,
                                    color = Neutral700,
                                    lineHeight = 14.sp
                                )
                            }
                        }

                        Text(
                            text = "Remove",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable {
                                    onFormStateChanged(
                                        formState.copy(
                                            externalMembers = formState.externalMembers - member
                                        )
                                    )
                                }
                                .padding(start = Spacing.sm)
                        )
                    }

                    if (index != formState.externalMembers.lastIndex) {
                        HorizontalDivider(
                            color = Neutral300,
                            thickness = 0.5.dp,
                            modifier = Modifier.padding(horizontal = Spacing.md)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.md))
    }
}


//  External form field

@Composable
private fun ExternalFormField(
    label: String,
    value: String,
    placeholder: String,
    isRequired: Boolean,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.bookRoomLabel
            )
            if (isRequired) {
                Text(
                    text = " *",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.customColors.bookRoomPlaceholder,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .onFocusChanged { isFocused = it.isFocused },
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                unfocusedBorderColor = Neutral300,
                focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
        )
    }
}


//  Preview

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
                InternalMember(3L, "Sarah Kim", "sarah.kim@fintech.com", "Engineering"),
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

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ExternalParticipantsBottomSheetPreview() {
    MeetingRoomBookingTheme {
        ExternalParticipantsBottomSheet(
            formState = RoomBookingFormState(
                externalMembers = listOf(
                    ExternalMember("Alice Smith", "alice.smith@example.com")
                )
            ),
            onFormStateChanged = {},
            onClose = {}
        )
    }
}