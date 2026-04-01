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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
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


  
// Root sheet — call this inside ModalBottomSheet with windowInsets = WindowInsets(0)
// so it stretches edge-to-edge up to the status bar.
  
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParticipantsSheetContent(
    formState: RoomBookingFormState,
    availableParticipants: List<InternalMember>,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    onClose: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearching: Boolean = false,
    modifier: Modifier = Modifier
) {
    var participantType by remember { mutableStateOf(ParticipantType.INTERNAL) }
    var groupBy by remember { mutableStateOf(GroupBy.PEOPLE) }
    var externalName by remember { mutableStateOf("") }
    var externalEmail by remember { mutableStateOf("") }

    // Status bar height so content starts right below notification bar
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = statusBarPadding)   // push below notification bar
    ) {

        //      Drag handle                                                                                                             
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

        //      Header                                                                                                                       
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xs, vertical = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Neutral700
                )
            }
            Text(
                text = "Add Participants",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.customColors.bookRoomLabel
            )
        }


        //      Selected participant chips                                                                                 
        val hasAnySelected =
            formState.participants.isNotEmpty() || formState.externalMembers.isNotEmpty()

        if (hasAnySelected) {
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

        //      Section label                                                                                                         
        Text(
            text = "Participants",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.customColors.bookRoomLabel,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
        )

        //      Internal / External pills                                                                                 
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.xs),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            ParticipantTypePill(
                label = "Internal",
                isSelected = participantType == ParticipantType.INTERNAL,
                isExpanded = participantType == ParticipantType.INTERNAL,
                onClick = { participantType = ParticipantType.INTERNAL }
            )
            ParticipantTypePill(
                label = "External",
                isSelected = participantType == ParticipantType.EXTERNAL,
                isExpanded = participantType == ParticipantType.EXTERNAL,
                onClick = { participantType = ParticipantType.EXTERNAL }
            )
        }

        //      Scrollable content                                                                                               
        Box(modifier = Modifier.weight(1f).imePadding()) {
            when (participantType) {
                ParticipantType.INTERNAL -> InternalParticipantsContent(
                    formState = formState,
                    onFormStateChanged = onFormStateChanged,
                    groupBy = groupBy,
                    onGroupByChange = { groupBy = it },
                    searchQuery = searchQuery,
                    onSearchQueryChange = onSearchQueryChange,
                    isSearching = isSearching,
                    filteredParticipants = availableParticipants
                )

                ParticipantType.EXTERNAL -> ExternalParticipantsContent(
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
        }

        //      Done button                                                                                                               
        PrimaryButton(
            text = "Done",
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm)
        )
    }
}


  
// Selected chip
  
@Composable
fun SelectedParticipantChip(
    name: String,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.full))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(CornerRadius.full)
            )
            .padding(start = Spacing.sm, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $name",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}


  
// Internal / External pill toggle
  
private enum class ParticipantType { INTERNAL, EXTERNAL }
private enum class GroupBy { PEOPLE, TEAMS, ALL }

@Composable
private fun ParticipantTypePill(
    label: String,
    isSelected: Boolean,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(
                if (isSelected) MaterialTheme.customColors.bookRoomInputBackground
                else MaterialTheme.colorScheme.surface
            )
            .border(
                0.5.dp,
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else Neutral300,
                RoundedCornerShape(CornerRadius.lg)
            )
            .clickable { onClick() }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        Icon(
            imageVector = Icons.Default.PersonAdd,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Neutral700,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) MaterialTheme.colorScheme.primary
            else MaterialTheme.customColors.bookRoomLabel
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
            else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else Neutral700,
            modifier = Modifier.size(18.dp)
        )
    }
}


  
// Internal participants panel
  
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
    ) {
        //      Segmented People / Teams / All                                                                       
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
                        .clickable { onGroupByChange(value) }
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

        //      Inner search                                                                                                           
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
            TextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = "Search by name or email...",
                        color = Neutral400,
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                    unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
            )
        }

        //      Search progress indicator                                                                                 
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

        //      Participant list                                                                                                   
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (groupBy == GroupBy.TEAMS) {
                val grouped = filteredParticipants.groupBy { it.department }
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
                            onToggleSelection = { toggleParticipant(formState, participant, onFormStateChanged) }
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
            } else {
                items(filteredParticipants, key = { it.id }) { participant ->
                    InternalParticipantItem(
                        participant = participant,
                        isSelected = formState.participants.any { it.id == participant.id },
                        onToggleSelection = { toggleParticipant(formState, participant, onFormStateChanged) }
                    )
                    if (filteredParticipants.last() != participant) {
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


  
// Single internal participant row
  
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
        //      Checkbox (left-most)                                                                                           
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

        // Avatar circle
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = participant.name
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .take(2)
                    .joinToString(""),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        //      Name / email / department (tightly grouped)                                               
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)   // tight spacing
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


  
// External participants panel
  
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
        //      Add form card                                                                                                         
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(MaterialTheme.colorScheme.surface)
                .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            // Card title
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

            // Name field
            ExternalFormField(
                label = "Name",
                value = externalName,
                placeholder = "Enter external member name",
                isRequired = true,
                onValueChange = onExternalNameChange
            )

            // Email field
            ExternalFormField(
                label = "Email",
                value = externalEmail,
                placeholder = "Enter external member email",
                isRequired = true,
                onValueChange = onExternalEmailChange
            )

            // Warning note
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

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                // Add button
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

                // Clear button
                OutlinedButton(
                    onClick = onClearForm,
                    modifier = Modifier
                        .weight(0.55f)
                        .height(44.dp),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Neutral700
                    ),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, Neutral300)
                ) {
                    Text("Clear", fontSize = 13.sp)
                }
            }
        }

        //      Added external members list                                                                               
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
                        // Avatar + info
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


  
// External form field
  
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
// Preview
  
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ParticipantsSheetContentPreview() {
    MeetingRoomBookingTheme {
        ParticipantsSheetContent(
            formState = RoomBookingFormState(
                participants = listOf(
                    InternalMember(1L, "John Carter", "john.carter@fintech.com", "Engineering"),
                    InternalMember(2L, "Emily Davis", "emily.davis@fintech.com", "Product")
                ),
                externalMembers = listOf(
                    ExternalMember("Alice Smith", "alice.smith@example.com")
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