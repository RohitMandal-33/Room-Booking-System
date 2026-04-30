package com.swifttechnology.bookingsystem.features.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO

// ── Brand colors ──────────────────────────────────────────────
private val Purple = Color(0xFF6109C0) // Updated to match palette
private val Background = Color(0xFFF9FAFB) // Adjusted for light theme consistency
private val CardBackground = Color.White
private val SectionLabel = Color(0xFF9CA3AF)
private val InputBackground = Color(0xFFF3F4F6)

// ── Color helpers ────────────────────────────────────────────

/** Parses "#RRGGBB", "rgb(R,G,B)" and bare "(R,G,B)" strings into a Compose Color. */
fun parseColor(colorString: String?): Color {
    if (colorString == null) return Purple
    return try {
        when {
            colorString.startsWith("#") ->
                Color(android.graphics.Color.parseColor(colorString))
            // handles both "rgb(R,G,B)" and bare "(R,G,B)" from the backend
            colorString.startsWith("rgb") || colorString.startsWith("(") -> {
                val nums = colorString
                    .removePrefix("rgb")
                    .trim()
                    .removePrefix("(")
                    .removeSuffix(")")
                    .split(",")
                    .map { it.trim().toInt() }
                Color(nums[0] / 255f, nums[1] / 255f, nums[2] / 255f)
            }
            else -> Purple
        }
    } catch (e: Exception) { Purple }
}

/** Serialises a Compose Color into the "rgb(R,G,B)" string the backend expects. */
fun colorToRgbString(color: Color): String {
    val r = (color.red   * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue  * 255).toInt()
    return "rgb($r,$g,$b)"
}

/** Curated palette shown in the colour picker. Pair of Color and its Name. */
private val ColorPalette = listOf(
    Color(0xFF406020) to "Forest Green",
    Color(0xFF15A14D) to "Green",
    Color(0xFF28D7A0) to "Teal",
    Color(0xFFEFC903) to "Gold",
    Color(0xFF725F6B) to "Mauve",
    Color(0xFFF6FE1C) to "Lemon",
    Color(0xFF9F6EF4) to "Lavender",
    Color(0xFF906670) to "Rose-brown",
    Color(0xFFB0F4B1) to "Mint",
    Color(0xFF291FBE) to "Navy Blue",
    Color(0xFFF5916C) to "Coral",
    Color(0xFF7684E1) to "Sky Blue",
    Color(0xFFC9728A) to "Dusty Rose",
    Color(0xFF6109C0) to "Deep Purple",
    Color(0xFFF5C95D) to "Sand",
    Color(0xFFCBAB8A) to "Tan",
    Color(0xFF2BE818) to "Neon Green",
    Color(0xFFF0C90B) to "Yellow",
    Color(0xFFE4931A) to "Orange",
    Color(0xFFD545D6) to "Magenta",
    Color(0xFFB70DBB) to "Violet",
    Color(0xFF22FB96) to "Seafoam",
    Color(0xFFA86B29) to "Brown"
)

/** Returns a user-friendly name for a color if it's in our curated palette. */
fun getColorName(color: Color): String {
    val rgb = colorToRgbString(color)
    return ColorPalette.find { colorToRgbString(it.first) == rgb }?.second ?: rgb
}

// ── Reusable card shell ───────────────────────────────────────
@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

// Section header 
@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = SectionLabel,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.5.sp
    )
}

// Styled text field 
@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = Color(0xFFBDBDBD), fontSize = 14.sp)
        },
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = InputBackground,
            focusedContainerColor = InputBackground,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            cursorColor = Purple,
            focusedTextColor = Color(0xFF1F2937),
            unfocusedTextColor = Color(0xFF1F2937)
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else
            androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

//  2. MEETING VISUALS

@Composable
fun MeetingVisualsCard(
    meetingTypes: List<MeetingTypeDTO>,
    onAddMeetingType: (String, String) -> Unit,
    onUpdateMeetingType: (Long, String, String) -> Unit,
    onChangeStatus: (Long, String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var editingMeeting by remember { mutableStateOf<MeetingTypeDTO?>(null) }
    var newName by remember { mutableStateOf("") }
    // stored as rgb(R,G,B) — what the backend expects
    var newColor by remember { mutableStateOf(colorToRgbString(Purple)) }

    if (showDialog) {
        val selectedColor = parseColor(newColor)
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = if (editingMeeting != null) "Edit Meeting Type" else "Add Meeting Type",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Name field
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Large colour preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectedColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getColorName(selectedColor),
                            color = if (selectedColor.luminance() > 0.5f) Color.Black else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Colour palette swatches
                    Text(
                        text = "Choose a colour",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp
                    )

                    // Calculate taken colors from other meeting types
                    val takenColorRgbStrings = meetingTypes
                        .filter { it.id != editingMeeting?.id }
                        .map { colorToRgbString(parseColor(it.colorCode)) }
                        .toSet()

                    val rows = ColorPalette.chunked(6)
                    rows.forEach { rowColors ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowColors.forEach { (swatch, name) ->
                                val swatchRgb = colorToRgbString(swatch)
                                val isSelected = selectedColor == swatch
                                val isTaken = takenColorRgbStrings.contains(swatchRgb)

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isTaken) swatch.copy(alpha = 0.2f) else swatch)
                                        .then(
                                            if (isSelected)
                                                Modifier.border(3.dp, Color.White, CircleShape)
                                                    .border(5.dp, Color(0xFF374151), CircleShape)
                                            else Modifier
                                        )
                                        .clickable(enabled = !isTaken) { 
                                            newColor = swatchRgb 
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isTaken) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Taken",
                                            tint = Color.White.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editingMeeting != null) {
                            onUpdateMeetingType(editingMeeting!!.id!!, newName, newColor)
                        } else {
                            onAddMeetingType(newName, newColor)
                        }
                        showDialog = false
                    },
                    enabled = newName.isNotBlank()
                ) {
                    Text("Save", color = Purple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Meeting Visuals")
            IconButton(
                onClick = {
                    editingMeeting = null
                    newName = ""
                    newColor = colorToRgbString(Purple)
                    showDialog = true
                },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Meeting Type",
                    tint = Purple
                )
            }
        }
        SettingsCard {
            if (meetingTypes.isEmpty()) {
                Text(
                    text = "No meeting types found.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                meetingTypes.forEachIndexed { index, meeting ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(parseColor(meeting.colorCode))
                        )
                        Spacer(Modifier.width(14.dp))
                        Text(
                            text = meeting.name ?: "Unknown",
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF1F2937),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal
                        )
                        val isActive = meeting.status == "ACTIVE"
                        Switch(
                            checked = isActive,
                            onCheckedChange = { onChangeStatus(meeting.id!!, meeting.status ?: "INACTIVE") },
                            modifier = Modifier.padding(end = 8.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Purple,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color(0xFFD1D5DB)
                            )
                        )
                        IconButton(
                            onClick = {
                                editingMeeting = meeting
                                newName = meeting.name ?: ""
                                // normalise: always store as rgb(...) so the palette highlights correctly
                                val parsed = parseColor(meeting.colorCode)
                                newColor = colorToRgbString(parsed)
                                showDialog = true
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = "Edit ${meeting.name}",
                                tint = Purple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    if (index < meetingTypes.lastIndex) {
                        HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                    }
                }
            }
        }
    }
}

//  3. GLOBAL RULES
@Composable
fun GlobalRulesCard(
    duration: String,
    buffer: String,
    smartConflict: Boolean,
    onRulesChanged: (String, String, Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Global Rules")
        SettingsCard {
            // Duration + Buffer row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DURATION (MIN)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = duration,
                        onValueChange = { onRulesChanged(it, buffer, smartConflict) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = InputBackground,
                            focusedContainerColor = InputBackground,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Purple,
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "BUFFER (MIN)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    TextField(
                        value = buffer,
                        onValueChange = { onRulesChanged(duration, it, smartConflict) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp)),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = InputBackground,
                            focusedContainerColor = InputBackground,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Purple,
                            focusedTextColor = Color(0xFF1F2937),
                            unfocusedTextColor = Color(0xFF1F2937)
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }

            // Smart Conflict Resolution toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Smart Conflict Resolution",
                        color = Color(0xFF1F2937),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Auto-decline double bookings",
                        color = Color(0xFF9CA3AF),
                        fontSize = 13.sp
                    )
                }
                Switch(
                    checked = smartConflict,
                    onCheckedChange = { onRulesChanged(duration, buffer, it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Purple,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFD1D5DB)
                    )
                )
            }
        }
    }
}


//  4. SECURITY

@Composable
fun SecurityCard(
    onChangePassword: (String, String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader("Security")
        SettingsCard {
            // Current Password
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Current Password", color = Color(0xFF374151), fontSize = 13.sp)
                SettingsTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    placeholder = "••••••••",
                    isPassword = true
                )
            }
            // New Password
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("New Password", color = Color(0xFF374151), fontSize = 13.sp)
                SettingsTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = "••••••••",
                    isPassword = true
                )
            }
            // Confirm Password
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Confirm Password", color = Color(0xFF374151), fontSize = 13.sp)
                SettingsTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "••••••••",
                    isPassword = true
                )
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            TextButton(
                onClick = { 
                    onChangePassword(currentPassword, newPassword, confirmPassword)
                    currentPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank()
            ) {
                Text(
                    text = "Update Password",
                    color = Purple,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }
        }
    }
}


//  ROOT SCREEN — composes all four cards
@Composable
fun SettingsScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(message = it, duration = SnackbarDuration.Short)
            viewModel.clearMessages()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                MeetingVisualsCard(
                    meetingTypes = uiState.meetingTypes,
                    onAddMeetingType = { name, color -> viewModel.addMeetingType(name, color) },
                    onUpdateMeetingType = { id, name, color -> viewModel.updateMeetingType(id, name, color) },
                    onChangeStatus = { id, currentStatus -> viewModel.changeMeetingTypeStatus(id, currentStatus) }
                )
                
                GlobalRulesCard(
                    duration = uiState.durationMin,
                    buffer = uiState.bufferMin,
                    smartConflict = uiState.smartConflict,
                    onRulesChanged = { dur, buf, conflict ->
                        viewModel.updateGlobalRules(dur, buf, conflict)
                    }
                )
                
                SecurityCard(
                    onChangePassword = { old, new, confirm ->
                        viewModel.changePassword(old, new, confirm)
                    }
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
