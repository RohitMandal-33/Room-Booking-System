package com.swifttechnology.bookingsystem.features.announcements.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementRequestDTO
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAnnouncementSheet(
    editingAnnouncement: Announcement?,
    authorId: Long,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSave: (AnnouncementRequestDTO) -> Unit
) {
    val isEdit = editingAnnouncement != null
    val colors = MaterialTheme.customColors

    // Pre-fill state from editing announcement or blank defaults
    var title by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.title ?: "")
    }
    var message by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.message ?: "")
    }
    var priorityLevel by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.priorityLevel ?: "NORMAL")
    }
    var pinned by rememberSaveable(editingAnnouncement) {
        mutableStateOf(editingAnnouncement?.pinned ?: false)
    }
    var allUser by rememberSaveable { mutableStateOf(true) }

    val titleError = title.isBlank()
    val messageError = message.isBlank()
    val canSave = !titleError && !messageError

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = colors.neutral100,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(CornerRadius.full))
                    .background(colors.neutral300)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.md)
                .navigationBarsPadding()
                .imePadding()
                .padding(bottom = Spacing.lg)
        ) {
            // Header
            Text(
                text = if (isEdit) "Edit Announcement" else "New Announcement",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = colors.deepBlack
                )
            )
            Spacer(Modifier.height(Spacing.md))

            // Title field
            SheetLabel("Title *")
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length <= 100) title = it },
                placeholder = { Text("Enter announcement title", color = colors.textHint) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isNotEmpty() && title.isBlank(),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = outlinedColors(colors)
            )
            Text(
                text = "${title.length}/100",
                style = MaterialTheme.typography.labelSmall.copy(color = colors.textHint),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(Modifier.height(Spacing.ms))

            // Message field
            SheetLabel("Message *")
            OutlinedTextField(
                value = message,
                onValueChange = { if (it.length <= 500) message = it },
                placeholder = { Text("Enter announcement message", color = colors.textHint) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 6,
                isError = message.isNotEmpty() && message.isBlank(),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = outlinedColors(colors)
            )
            Text(
                text = "${message.length}/500",
                style = MaterialTheme.typography.labelSmall.copy(color = colors.textHint),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(Modifier.height(Spacing.ms))

            // Priority toggle
            SheetLabel("Priority")
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                listOf("NORMAL", "HIGH").forEach { level ->
                    val selected = priorityLevel == level
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(CornerRadius.lg))
                            .background(if (selected) Primary else Color.Transparent)
                            .border(
                                1.dp,
                                if (selected) Primary else colors.neutral300,
                                RoundedCornerShape(CornerRadius.lg)
                            )
                            .clickable { priorityLevel = level }
                            .padding(horizontal = Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level,
                            color = if (selected) Color.White else colors.textSecondary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.ms))

            // Pinned toggle
            SheetLabel("Pin Announcement")
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(true to "Yes", false to "No").forEach { (value, label) ->
                    val selected = pinned == value
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(CornerRadius.lg))
                            .background(if (selected) Primary else Color.Transparent)
                            .border(
                                1.dp,
                                if (selected) Primary else colors.neutral300,
                                RoundedCornerShape(CornerRadius.lg)
                            )
                            .clickable { pinned = value }
                            .padding(horizontal = Spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (selected) Color.White else colors.textSecondary,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xl))

            // Save button
            Button(
                onClick = {
                    if (canSave) {
                        onSave(
                            AnnouncementRequestDTO(
                                title         = title.trim(),
                                message       = message.trim(),
                                priorityLevel = priorityLevel,
                                pinned        = pinned,
                                allUser       = allUser,
                                authorId      = authorId
                            )
                        )
                    }
                },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(CornerRadius.lg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    disabledContainerColor = Primary.copy(alpha = 0.4f)
                )
            ) {
                Text(
                    text = if (isEdit) "Update Announcement" else "Add Announcement",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun SheetLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.customColors.textSecondary
        ),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun outlinedColors(
    colors: com.swifttechnology.bookingsystem.core.designsystem.CustomColors
) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor       = Primary,
    unfocusedBorderColor     = colors.neutral300,
    focusedContainerColor    = colors.neutral100,
    unfocusedContainerColor  = colors.neutral100,
    cursorColor              = Primary,
    focusedTextColor         = colors.deepBlack,
    unfocusedTextColor       = colors.deepBlack
)
