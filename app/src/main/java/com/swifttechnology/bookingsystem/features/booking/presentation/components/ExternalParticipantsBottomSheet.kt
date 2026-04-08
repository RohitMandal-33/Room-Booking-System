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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.Neutral300
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.Warning
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.presentation.ExternalMember
import com.swifttechnology.bookingsystem.features.booking.presentation.RoomBookingFormState
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton

//     EXTERNAL bottom-sheet                                                    

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
        DragHandle()

        SheetHeader(title = "External Member", onClose = onClose)

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

    }
}


//     External participants form panel                                         

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
                                name  = externalName.trim(),
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
                        contentColor   = MaterialTheme.colorScheme.primary
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


//     External form field                                                       

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
                focusedContainerColor   = MaterialTheme.customColors.bookRoomInputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                focusedBorderColor      = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                unfocusedBorderColor    = Neutral300,
                focusedTextColor        = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor      = MaterialTheme.customColors.bookRoomLabel,
                cursorColor             = MaterialTheme.colorScheme.primary
            ),
            singleLine = true,
            textStyle  = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp)
        )
    }
}

//     Previews                                                                 

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
