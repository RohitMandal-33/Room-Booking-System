package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    viewModel: ParticipantsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Sync outer searchQuery with ViewModel
    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Tab Row
            ParticipantsTabRow(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected,
                customGroupsCount = 2 // Keeping as provided in mock
            )

            // Edit Mode Button
            EditModeButton(
                isEditMode = uiState.isEditMode,
                onClick = viewModel::toggleEditMode
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content
            if (uiState.isLoading) {
                LoadingContent()
            } else if (uiState.error != null) {
                ErrorContent(uiState.error!!)
            } else {
                DirectoryContent(
                    participants = uiState.participants,
                    searchQuery = uiState.searchQuery,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged
                )
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = viewModel::onAddParticipantClicked,
            shape = CircleShape,
            containerColor = Color(0xFF1C1C1E),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Participant")
        }
    }
}


@Composable
fun ParticipantsTabRow(
    selectedTab: ParticipantsTab,
    onTabSelected: (ParticipantsTab) -> Unit,
    customGroupsCount: Int
) {
    Surface(color = Color.Transparent) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        ) {
            // All Participants Tab
            ParticipantsTabItem(
                label = "All Participants",
                icon = Icons.Outlined.People,
                isSelected = selectedTab == ParticipantsTab.ALL_PARTICIPANTS,
                badge = null,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(ParticipantsTab.ALL_PARTICIPANTS) }
            )
            // Custom Groups Tab
            ParticipantsTabItem(
                label = "Custom Groups",
                icon = Icons.Outlined.FolderOpen,
                isSelected = selectedTab == ParticipantsTab.CUSTOM_GROUPS,
                badge = customGroupsCount,
                modifier = Modifier.weight(1f),
                onClick = { onTabSelected(ParticipantsTab.CUSTOM_GROUPS) }
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
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFF007AFF) else Color(0xFF3C3C43).copy(alpha = 0.6f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = if (isSelected) Color(0xFF007AFF) else Color(0xFF3C3C43).copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            if (badge != null) {
                Spacer(modifier = Modifier.width(5.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFF007AFF) else Color(0xFF3C3C43).copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = badge.toString(),
                        color = if (isSelected) Color.White else Color(0xFF3C3C43),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Active indicator
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp))
                .background(if (isSelected) Color(0xFF007AFF) else Color.Transparent)
        )
    }
}


@Composable
fun EditModeButton(isEditMode: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        border = BorderStroke(0.5.dp, Color(0xFF3C3C43).copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (isEditMode) Icons.Outlined.CheckCircle else Icons.Outlined.Edit,
                contentDescription = null,
                tint = Color(0xFF1C1C1E),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isEditMode) "Done" else "Edit Mode",
                color = Color(0xFF1C1C1E),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}


@Composable
fun DirectoryContent(
    participants: List<Participant>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    Column {
        Text(
            text = "Participants Directory",
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
            items(participants, key = { it.id }) { participant ->
                ParticipantCard(participant = participant)
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
        value = query,
        onValueChange = onQueryChanged,
        placeholder = {
            Text(
                "Search by name, email, or department...",
                color = Color(0xFF3C3C43).copy(alpha = 0.4f),
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = Color(0xFF3C3C43).copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF007AFF).copy(alpha = 0.5f),
            unfocusedBorderColor = Color(0xFF3C3C43).copy(alpha = 0.15f),
            focusedContainerColor = Color(0xFFF2F2F7),
            unfocusedContainerColor = Color(0xFFF2F2F7)
        ),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
    )
}


@Composable
fun ParticipantCard(participant: Participant) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.2.dp, Color(0xFF3C3C43).copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier
                .clickable { /* Navigate to detail */ }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Name + Department badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = participant.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF1C1C1E)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = participant.role,
                        fontSize = 13.sp,
                        color = Color(0xFF3C3C43).copy(alpha = 0.55f)
                    )
                }
                DepartmentBadge(department = participant.department)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact info
            ContactRow(
                icon = Icons.Outlined.Email,
                text = participant.email
            )
            Spacer(modifier = Modifier.height(4.dp))
            ContactRow(
                icon = Icons.Outlined.Phone,
                text = participant.phone
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${participant.meetingCount} meetings",
                fontSize = 12.sp,
                color = Color(0xFF3C3C43).copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun DepartmentBadge(department: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF3C3C43).copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = department,
            fontSize = 11.sp,
            color = Color(0xFF3C3C43).copy(alpha = 0.7f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF3C3C43).copy(alpha = 0.45f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color(0xFF3C3C43).copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

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
            text = "Error: $message",
            color = Color(0xFFFF3B30),
            fontSize = 14.sp
        )
    }
}
