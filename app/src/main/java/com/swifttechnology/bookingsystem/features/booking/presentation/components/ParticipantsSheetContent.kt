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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.swifttechnology.bookingsystem.core.designsystem.Neutral300
import com.swifttechnology.bookingsystem.core.designsystem.Neutral400
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.Warning
import com.swifttechnology.bookingsystem.features.booking.presentation.ExternalMember
import com.swifttechnology.bookingsystem.features.booking.presentation.RoomBookingFormState
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton

private data class InternalParticipant(
    val name: String,
    val email: String,
    val department: String
)

private val sampleInternalParticipants = listOf(
    InternalParticipant("John Carter", "john.carter@fintech.com", "Engineering"),
    InternalParticipant("Emily Davis", "emily.davis@fintech.com", "Product"),
    InternalParticipant("Michael Brown", "michael.brown@fintech.com", "Design"),
    InternalParticipant("Sophia Wilson", "sophia.wilson@fintech.com", "Engineering"),
    InternalParticipant("Daniel Taylor", "daniel.taylor@fintech.com", "Product"),
    InternalParticipant("Olivia Martinez", "olivia.martinez@fintech.com", "Design"),
    InternalParticipant("James Anderson", "james.anderson@fintech.com", "Engineering"),
    InternalParticipant("Isabella Thomas", "isabella.thomas@fintech.com", "Product"),
    InternalParticipant("William Jackson", "william.jackson@fintech.com", "Design"),
    InternalParticipant("Ava Harris", "ava.harris@fintech.com", "Engineering"),
    InternalParticipant("Benjamin Clark", "benjamin.clark@fintech.com", "Product"),
    InternalParticipant("Mia Rodriguez", "mia.rodriguez@fintech.com", "Design")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ParticipantsSheetContent(
    formState: RoomBookingFormState,
    onFormStateChanged: (RoomBookingFormState) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var participantType by remember { mutableStateOf(ParticipantType.INTERNAL) }
    var groupBy by remember { mutableStateOf(GroupBy.PEOPLE) }
    var internalSearchQuery by remember { mutableStateOf("") }
    var externalName by remember { mutableStateOf("") }
    var externalEmail by remember { mutableStateOf("") }

    Column(modifier = modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .background(MaterialTheme.colorScheme.surface)) {
        // Header with Back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.sm),
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

        // Search bar - "Search people, participants..."
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                .height(48.dp)
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(MaterialTheme.customColors.bookRoomInputBackground)
                .border(0.5.dp, Neutral300, RoundedCornerShape(CornerRadius.full))
                .padding(horizontal = Spacing.md),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Search people, participants...",
                    fontSize = 14.sp,
                    color = MaterialTheme.customColors.bookRoomPlaceholder
                )
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Neutral700,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Selected participants chips — shown only when at least one participant is selected
        val selectedInternalKeys = formState.participants
        val selectedExternalMembers = formState.externalMembers
        val hasAnySelected = selectedInternalKeys.isNotEmpty() || selectedExternalMembers.isNotEmpty()

        if (hasAnySelected) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md, vertical = Spacing.xs),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                verticalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                selectedInternalKeys.forEach { key ->
                    val name = key.substringBefore("|")
                    SelectedParticipantChip(
                        name = name,
                        onRemove = {
                            onFormStateChanged(
                                formState.copy(participants = formState.participants - key)
                            )
                        }
                    )
                }
                selectedExternalMembers.forEach { member ->
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

        Text(
            text = "Participants",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.customColors.bookRoomLabel,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm)
        )

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

        Box(modifier = Modifier.weight(1f)) {
            when (participantType) {
                ParticipantType.INTERNAL -> InternalParticipantsContent(
                    formState = formState,
                    onFormStateChanged = onFormStateChanged,
                    groupBy = groupBy,
                    onGroupByChange = { groupBy = it },
                    searchQuery = internalSearchQuery,
                    onSearchQueryChange = { internalSearchQuery = it },
                    filteredParticipants = sampleInternalParticipants
                        .filter { p ->
                            internalSearchQuery.isEmpty() ||
                                    p.name.contains(internalSearchQuery, ignoreCase = true) ||
                                    p.email.contains(internalSearchQuery, ignoreCase = true)
                        }
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

        PrimaryButton(
            text = "Done",
            onClick = onClose,
            modifier = Modifier.padding(Spacing.md)
        )
    }
}

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
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                shape = RoundedCornerShape(CornerRadius.full)
            )
            .padding(start = Spacing.sm, end = Spacing.xs, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(CornerRadius.full))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove $name",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(11.dp)
            )
        }
    }
}

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
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Neutral300,
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
            tint = Neutral700,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.customColors.bookRoomLabel
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Neutral700,
            modifier = Modifier.size(20.dp)
        )
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
    filteredParticipants: List<InternalParticipant>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Spacing.md)
            .clip(RoundedCornerShape(CornerRadius.lg))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
            .padding(Spacing.md)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            listOf(
                GroupBy.PEOPLE to "People",
                GroupBy.TEAMS to "Teams",
                GroupBy.ALL to "All"
            ).forEach { (value, label) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(
                            RoundedCornerShape(
                                topStart = if (value == GroupBy.PEOPLE) CornerRadius.md else 0.dp,
                                topEnd = if (value == GroupBy.ALL) CornerRadius.md else 0.dp,
                                bottomStart = if (value == GroupBy.PEOPLE) CornerRadius.md else 0.dp,
                                bottomEnd = if (value == GroupBy.ALL) CornerRadius.md else 0.dp
                            )
                        )
                        .background(
                            if (groupBy == value) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { onGroupByChange(value) }
                        .padding(vertical = Spacing.sm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (groupBy == value) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.customColors.bookRoomLabel
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        // Search by name or email
        TextField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = {
                Text(
                    text = "Search by name or email...",
                    color = Neutral400,
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Neutral700,
                    modifier = Modifier.size(20.dp)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(CornerRadius.md),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Spacing.md))

        // Participant list (store "name|email" for unique selection when names duplicate)
        val selectedKeys = formState.participants.toSet()
        fun key(p: InternalParticipant) = "${p.name}|${p.email}"
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            items(filteredParticipants) { participant ->
                val participantKey = key(participant)
                val isSelected = selectedKeys.contains(participantKey)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val current = formState.participants.toMutableList()
                            if (isSelected) {
                                current.remove(participantKey)
                            } else {
                                current.add(participantKey)
                            }
                            onFormStateChanged(formState.copy(participants = current.distinct()))
                        }
                        .padding(vertical = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { checked ->
                            val current = formState.participants.toMutableList()
                            if (checked) {
                                if (!current.contains(participantKey)) current.add(participantKey)
                            } else {
                                current.remove(participantKey)
                            }
                            onFormStateChanged(formState.copy(participants = current.distinct()))
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = MaterialTheme.colorScheme.primary,
                            uncheckedColor = Neutral700
                        )
                    )
                    Column(modifier = Modifier.padding(start = Spacing.xs)) {
                        Text(
                            text = participant.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.customColors.bookRoomLabel
                        )
                        Text(
                            text = participant.email,
                            fontSize = 12.sp,
                            color = Neutral700
                        )
                        Text(
                            text = participant.department,
                            fontSize = 12.sp,
                            color = Info
                        )
                    }
                }
            }
        }
    }
}

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
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(Spacing.md)
    ) {
        // Add External Member card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, Neutral300, RoundedCornerShape(CornerRadius.lg))
                .padding(Spacing.md)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Add External Member",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.customColors.bookRoomLabel
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            ParticipantsSheetTextField(
                label = "Name",
                value = externalName,
                placeholder = "Enter external member name",
                isRequired = true,
                onValueChange = onExternalNameChange
            )

            Spacer(modifier = Modifier.height(Spacing.sm))

            ParticipantsSheetTextField(
                label = "Email",
                value = externalEmail,
                placeholder = "Enter external member email",
                isRequired = true,
                onValueChange = onExternalEmailChange
            )

            // Note box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Spacing.md)
                    .clip(RoundedCornerShape(CornerRadius.md))
                    .background(Color(0xFFFFF8E1))
                    .border(1.dp, Warning, RoundedCornerShape(CornerRadius.md))
                    .padding(Spacing.sm)
            ) {
                Text(
                    text = "Note: External members will be tracked separately in reports and won't affect internal dashboard statistics.",
                    fontSize = 12.sp,
                    color = Warning
                )
            }

            Spacer(modifier = Modifier.height(Spacing.md))

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
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.customColors.bookRoomLabel
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.customColors.bookRoomLabel
                    )
                    Spacer(modifier = Modifier.size(Spacing.xs))
                    Text("Add External Member", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onClearForm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(CornerRadius.lg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Neutral700
                    )
                ) {
                    Text("Clear", fontSize = 14.sp)
                }
            }
        }

        // List of added external members
        if (formState.externalMembers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(Spacing.md))
            Text(
                text = "Added external members",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.bookRoomLabel,
                modifier = Modifier.padding(bottom = Spacing.xs)
            )
            formState.externalMembers.forEach { member ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = member.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.customColors.bookRoomLabel
                        )
                        Text(
                            text = member.email,
                            fontSize = 12.sp,
                            color = Neutral700
                        )
                    }
                    Text(
                        text = "Remove",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            onFormStateChanged(
                                formState.copy(
                                    externalMembers = formState.externalMembers - member
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ParticipantsSheetTextField(
    label: String,
    value: String,
    placeholder: String,
    isRequired: Boolean,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Row {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.customColors.bookRoomLabel
            )
            if (isRequired) {
                Text(
                    text = " *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.customColors.bookRoomPlaceholder,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .onFocusChanged { isFocused = it.isFocused }
                .shadow(
                    elevation = if (isFocused) 8.dp else 0.dp,
                    shape = RoundedCornerShape(CornerRadius.lg),
                    clip = false
                ),
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                cursorColor = MaterialTheme.colorScheme.primary
            ),
            singleLine = true
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParticipantsSheetContentPreview() {
    MeetingRoomBookingTheme {
        ParticipantsSheetContent(
            formState = RoomBookingFormState(
                participants = listOf("John Carter|john.carter@fintech.com", "Emily Davis|emily.davis@fintech.com"),
                externalMembers = listOf(
                    ExternalMember("Alice Smith", "alice.smith@example.com")
                )
            ),
            onFormStateChanged = {},
            onClose = {}
        )
    }
}