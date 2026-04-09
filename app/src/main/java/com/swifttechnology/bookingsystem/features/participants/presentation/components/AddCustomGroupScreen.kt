package com.swifttechnology.bookingsystem.features.participants.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Neutral700
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingClickableField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingTextField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.InternalParticipantsBottomSheet
import com.swifttechnology.bookingsystem.features.booking.presentation.components.SelectedParticipantChip
import com.swifttechnology.bookingsystem.shared.layout.BottomSheetView
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.participants.presentation.AddCustomGroupViewModel

private val AddFlowPurple = Color(0xFF4A3496)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddCustomGroupScreen(
    initialGroup: CustomGroup? = null,
    onClose: () -> Unit,
    // Passes the success message back so it can be shown on the previous screen
    onContinue: (successMessage: String) -> Unit,
    viewModel: AddCustomGroupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val formState = uiState.formState
    val isEditMode = uiState.editGroupId != null

    var showInternalSheet by remember { mutableStateOf(false) }
    val internalSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(initialGroup) {
        if (initialGroup != null) {
            viewModel.initForEdit(initialGroup)
        } else {
            // Fresh "Add" session — clear any leftover state from a previous visit
            viewModel.resetState()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            val message = if (uiState.editGroupId != null)
                "Group updated successfully."
            else
                "Group created successfully."
            onContinue(message)
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.lg)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(Spacing.lg))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            viewModel.resetState()
                            onClose()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF2F2F7), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isEditMode) "Edit Custom Group" else "Add Custom Group",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Please Fill all the details",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                BookingTextField(
                    label = "Name",
                    value = formState.meetingTitle,
                    placeholder = "e.g. Project Alpha",
                    isRequired = true,
                    onValueChange = {
                        viewModel.onFormStateChanged(formState.copy(meetingTitle = it))
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                CustomGroupDescriptionField(
                    value = formState.description,
                    onValueChange = {
                        viewModel.onFormStateChanged(formState.copy(description = it))
                    }
                )

                if (uiState.error != null) {
                    Spacer(modifier = Modifier.height(Spacing.sm))
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = Spacing.xs)
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Member",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.customColors.bookRoomLabel
                    )
                    Text(
                        text = " *",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                val hasMembers = formState.participants.isNotEmpty()
                if (hasMembers) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                    ) {
                        formState.participants.forEach { member ->
                            SelectedParticipantChip(
                                name = member.name,
                                onRemove = {
                                    viewModel.onFormStateChanged(
                                        formState.copy(participants = formState.participants - member)
                                    )
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(Spacing.sm))
                }

                BookingClickableField(
                    label = "",
                    value = "",
                    placeholder = "Add Member",
                    isRequired = false,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.outline_groups_24),
                            contentDescription = null,
                            tint = Neutral700,
                            modifier = Modifier.size(Spacing.ml)
                        )
                    },
                    onClick = { showInternalSheet = true }
                )

                Spacer(modifier = Modifier.height(120.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
                    .background(Color.White)
            ) {
                Button(
                    onClick = {
                        viewModel.submit()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AddFlowPurple
                    ),
                    enabled = !uiState.isLoading &&
                            formState.meetingTitle.isNotBlank() &&
                            formState.participants.isNotEmpty()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = if (isEditMode) "Save Changes" else "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showInternalSheet) {
        BottomSheetView(
            onDismiss = { showInternalSheet = false },
            sheetState = internalSheetState,
            title = null
        ) {
            InternalParticipantsBottomSheet(
                formState = formState,
                availableParticipants = uiState.availableParticipants,
                onFormStateChanged = viewModel::onFormStateChanged,
                onClose = { showInternalSheet = false },
                searchQuery = uiState.participantSearchQuery,
                onSearchQueryChange = viewModel::onParticipantSearchQueryChanged,
                isSearching = uiState.isSearchingParticipants
            )
        }
    }
}

@Composable
private fun CustomGroupDescriptionField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = "Description",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.customColors.bookRoomLabel
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = "Enter Details (Optional)",
                    color = MaterialTheme.customColors.bookRoomPlaceholder,
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 80.dp),  // reduced from 120.dp
            minLines = 2,               // reduced from 3
            shape = RoundedCornerShape(CornerRadius.lg),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                unfocusedContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                disabledContainerColor = MaterialTheme.customColors.bookRoomInputBackground,
                focusedIndicatorColor = primaryColor,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                unfocusedTextColor = MaterialTheme.customColors.bookRoomLabel,
                cursorColor = primaryColor
            )
        )
    }
}
