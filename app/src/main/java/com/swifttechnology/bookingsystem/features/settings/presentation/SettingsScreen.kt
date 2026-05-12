package com.swifttechnology.bookingsystem.features.settings.presentation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
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
import com.swifttechnology.bookingsystem.core.designsystem.ThemeMode
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO

private val BrandPurple = Color(0xFF6109C0)

fun parseColor(colorString: String?): Color {
    if (colorString == null) return BrandPurple
    return try {
        when {
            colorString.startsWith("#") ->
                Color(android.graphics.Color.parseColor(colorString))
            colorString.startsWith("rgb") || colorString.startsWith("(") -> {
                val nums = colorString
                    .removePrefix("rgb").trim()
                    .removePrefix("(").removeSuffix(")")
                    .split(",").map { it.trim().toInt() }
                Color(nums[0] / 255f, nums[1] / 255f, nums[2] / 255f)
            }
            else -> BrandPurple
        }
    } catch (e: Exception) { BrandPurple }
}

fun colorToRgbString(color: Color): String {
    val r = (color.red   * 255).toInt()
    val g = (color.green * 255).toInt()
    val b = (color.blue  * 255).toInt()
    return "($r,$g,$b)"
}

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

fun getColorName(color: Color): String {
    val rgb = colorToRgbString(color)
    return ColorPalette.find { colorToRgbString(it.first) == rgb }?.second ?: rgb
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
private fun SettingsSurface(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
}

@Composable
private fun SettingsRow(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val base = modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
        .padding(horizontal = 12.dp, vertical = 10.dp)

    Row(
        modifier = base,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 12.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
        thickness = 0.5.dp
    )
}

@Composable
private fun SettingsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true
    )
}

@Composable
private fun LabeledField(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp
        )
        content()
    }
}

@Composable
private fun ThemeControlCard(
    selected: ThemeMode,
    onSelected: (ThemeMode) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("Appearance")
        SettingsSurface {
            listOf(
                Triple(ThemeMode.SYSTEM, "Device default",  "Follow system setting"),
                Triple(ThemeMode.LIGHT,  "Light mode",      "Always use light theme"),
                Triple(ThemeMode.DARK,   "Dark mode",       "Always use dark theme")
            ).forEachIndexed { index, (mode, title, subtitle) ->
                ThemeOptionRow(
                    title    = title,
                    subtitle = subtitle,
                    selected = selected == mode,
                    onClick  = { onSelected(mode) }
                )
                if (index < 2) RowDivider()
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    SettingsRow(onClick = onClick) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = subtitle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor   = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun MeetingVisualsCard(
    meetingTypes: List<MeetingTypeDTO>,
    onAddMeetingType: (String, String) -> Unit,
    onUpdateMeetingType: (Long, String, String) -> Unit,
    onDeleteMeetingType: (Long) -> Unit
) {
    var showDialog     by remember { mutableStateOf(false) }
    var editingMeeting by remember { mutableStateOf<MeetingTypeDTO?>(null) }
    var newName        by remember { mutableStateOf("") }
    var newColor       by remember { mutableStateOf(colorToRgbString(BrandPurple)) }

    if (showDialog) {
        val selectedColor = parseColor(newColor)

        AlertDialog(
            onDismissRequest = { showDialog = false },
            containerColor   = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            title = {
                Text(
                    text = if (editingMeeting != null) "Edit Meeting Type" else "Add Meeting Type",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Meeting name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            cursorColor          = MaterialTheme.colorScheme.primary,
                            focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor   = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor    = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor  = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )

                    val animatedColor by animateColorAsState(
                        targetValue = selectedColor,
                        animationSpec = tween(200),
                        label = "colorPreview"
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(animatedColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getColorName(animatedColor),
                            color = if (animatedColor.luminance() > 0.45f) Color(0xFF1A1A1A) else Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = "Choose a colour",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    val takenRgbs = meetingTypes
                        .filter { it.id != editingMeeting?.id }
                        .map { colorToRgbString(parseColor(it.colorCode)) }
                        .toSet()

                    ColorPalette.chunked(6).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            row.forEach { (swatch, _) ->
                                val swatchRgb  = colorToRgbString(swatch)
                                val isSelected = colorToRgbString(selectedColor) == swatchRgb
                                val isTaken    = swatchRgb in takenRgbs

                                ColorSwatch(
                                    color      = swatch,
                                    isSelected = isSelected,
                                    isTaken    = isTaken,
                                    onClick    = { if (!isTaken) newColor = swatchRgb }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editingMeeting != null) {
                            onUpdateMeetingType(editingMeeting!!.id!!, newName, newColor)
                        } else {
                            onAddMeetingType(newName, newColor)
                        }
                        showDialog = false
                    },
                    enabled = newName.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Meeting Types")
            FilledTonalIconButton(
                onClick = {
                    editingMeeting = null
                    newName  = ""
                    newColor = colorToRgbString(BrandPurple)
                    showDialog = true
                },
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor   = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Meeting Type",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        SettingsSurface {
            if (meetingTypes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No meeting types yet. Tap + to add one.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                meetingTypes.forEachIndexed { index, meeting ->
                    MeetingTypeRow(
                        meeting  = meeting,
                        onEdit   = {
                            editingMeeting = meeting
                            newName  = meeting.name ?: ""
                            newColor = colorToRgbString(parseColor(meeting.colorCode))
                            showDialog = true
                        },
                        onDelete = { onDeleteMeetingType(meeting.id!!) }
                    )
                    if (index < meetingTypes.lastIndex) RowDivider()
                }
            }
        }
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    isTaken: Boolean,
    onClick: () -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline

    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isTaken) color.copy(alpha = 0.25f) else color)
            .then(
                if (isSelected)
                    Modifier
                        .border(2.5.dp, Color.White, CircleShape)
                        .border(4.5.dp, borderColor, CircleShape)
                else Modifier
            )
            .clickable(enabled = !isTaken, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when {
            isSelected -> Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = if (color.luminance() > 0.45f) Color(0xFF1A1A1A) else Color.White,
                modifier = Modifier.size(16.dp)
            )
            isTaken -> Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Taken",
                tint = color.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
private fun MeetingTypeRow(
    meeting: MeetingTypeDTO,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showConfirm by remember { mutableStateOf(false) }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            containerColor   = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Delete Meeting Type?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "\"${meeting.name}\" will be permanently removed.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    SettingsRow {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(parseColor(meeting.colorCode))
        )
        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = meeting.name ?: "Unknown",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = getColorName(parseColor(meeting.colorCode)),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }

        IconButton(
            onClick = onEdit,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Edit ${meeting.name}",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }

        IconButton(
            onClick = { showConfirm = true },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete ${meeting.name}",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun SecurityCard(onChangePassword: (String, String, String) -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val passwordsMatch = newPassword == confirmPassword || confirmPassword.isEmpty()
    val canSubmit = currentPassword.isNotBlank()
            && newPassword.isNotBlank()
            && confirmPassword.isNotBlank()
            && passwordsMatch

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader("Security")
        SettingsSurface {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PasswordField(
                    label         = "Current Password",
                    value         = currentPassword,
                    onValueChange = { currentPassword = it }
                )
                PasswordField(
                    label         = "New Password",
                    value         = newPassword,
                    onValueChange = { newPassword = it }
                )
                PasswordField(
                    label         = "Confirm Password",
                    value         = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    isError       = !passwordsMatch,
                    errorMessage  = if (!passwordsMatch) "Passwords do not match" else null
                )

                HorizontalDivider(
                    color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    thickness = 0.5.dp
                )

                Button(
                    onClick = {
                        onChangePassword(currentPassword, newPassword, confirmPassword)
                        currentPassword = ""
                        newPassword     = ""
                        confirmPassword = ""
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = canSubmit,
                    shape  = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor  = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                ) {
                    Text(
                        text = "Update Password",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("••••••••", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                errorBorderColor     = MaterialTheme.colorScheme.error,
                cursorColor          = MaterialTheme.colorScheme.primary,
                focusedTextColor     = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor   = MaterialTheme.colorScheme.onSurface
            ),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SettingsScreen(
    searchQuery: String,
    currentUser: UserDetailsDTO? = null,
    onNavigate: (String) -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(searchQuery) { viewModel.onSearchQueryChanged(searchQuery) }

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
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier            = Modifier.fillMaxSize(),
                contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item {
                    ProfileCard(
                        user        = currentUser,
                        departments = uiState.departments,
                        onUpdateProfile = { fn, ln, pos, ph, depId ->
                            viewModel.updateProfile(fn, ln, pos, ph, depId)
                        }
                    )
                }
                item {
                    ThemeControlCard(
                        selected   = uiState.themeMode,
                        onSelected = viewModel::setThemeMode
                    )
                }
                item {
                    MeetingVisualsCard(
                        meetingTypes        = uiState.meetingTypes,
                        onAddMeetingType    = { name, color -> viewModel.addMeetingType(name, color) },
                        onUpdateMeetingType = { id, name, color -> viewModel.updateMeetingType(id, name, color) },
                        onDeleteMeetingType = { id -> viewModel.deleteMeetingType(id) }
                    )
                }
                item {
                    SecurityCard(
                        onChangePassword = { old, new, confirm ->
                            viewModel.changePassword(old, new, confirm)
                        }
                    )
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color    = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileCard(
    user: UserDetailsDTO?,
    departments: List<com.swifttechnology.bookingsystem.features.department.domain.model.Department>,
    onUpdateProfile: (String, String, String, String, Long) -> Unit
) {
    var showEditSheet by remember { mutableStateOf(false) }

    if (showEditSheet && user != null) {
        EditProfileSheet(
            user        = user,
            departments = departments,
            onDismiss   = { showEditSheet = false },
            onSave      = { fn, ln, pos, ph, depId ->
                onUpdateProfile(fn, ln, pos, ph, depId)
                showEditSheet = false
            }
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader("Profile Details")
            IconButton(
                onClick  = { showEditSheet = true },
                modifier = Modifier.size(32.dp),
                enabled  = user != null
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Edit,
                    contentDescription = "Edit Profile",
                    tint               = MaterialTheme.colorScheme.primary,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }
        SettingsSurface {
            if (user == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(24.dp),
                        color       = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileDetailRow(
                        label = "Name",
                        value = "${user.firstname.orEmpty()} ${user.lastname.orEmpty()}".trim().ifBlank { "N/A" }
                    )
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    ProfileDetailRow(label = "Email", value = user.email)
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    ProfileDetailRow(
                        label = "Phone Number",
                        value = user.phoneNo.takeIf { !it.isNullOrBlank() } ?: "N/A"
                    )
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    ProfileDetailRow(
                        label = "Department",
                        value = user.department.takeIf { !it.isNullOrBlank() } ?: "N/A"
                    )
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    ProfileDetailRow(
                        label = "Role",
                        value = user.role.takeIf { !it.isNullOrBlank() } ?: "N/A"
                    )
                    HorizontalDivider(
                        color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 0.5.dp
                    )
                    ProfileDetailRow(
                        label = "Position",
                        value = user.position.takeIf { !it.isNullOrBlank() } ?: "N/A"
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            color      = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text       = value,
            color      = MaterialTheme.colorScheme.onSurface,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileSheet(
    user: UserDetailsDTO,
    departments: List<com.swifttechnology.bookingsystem.features.department.domain.model.Department>,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Long) -> Unit
) {
    var firstname        by remember { mutableStateOf(user.firstname.orEmpty()) }
    var lastname         by remember { mutableStateOf(user.lastname.orEmpty()) }
    var position         by remember { mutableStateOf(user.position.orEmpty()) }
    var phoneNo          by remember { mutableStateOf(user.phoneNo.orEmpty()) }
    var selectedDeptId   by remember { mutableStateOf(user.departmentId ?: 0L) }
    var showDeptDropdown by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor   = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text       = "Edit Profile",
                    style      = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text  = "Update your personal information below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(
                color     = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp
            )

            Row(
                modifier                = Modifier.fillMaxWidth(),
                horizontalArrangement   = Arrangement.spacedBy(12.dp)
            ) {
                LabeledField(label = "First Name", modifier = Modifier.weight(1f)) {
                    SettingsTextField(
                        value         = firstname,
                        onValueChange = { firstname = it },
                        placeholder   = "e.g. Jane"
                    )
                }
                LabeledField(label = "Last Name", modifier = Modifier.weight(1f)) {
                    SettingsTextField(
                        value         = lastname,
                        onValueChange = { lastname = it },
                        placeholder   = "e.g. Smith"
                    )
                }
            }

            LabeledField(label = "Position") {
                SettingsTextField(
                    value         = position,
                    onValueChange = { position = it },
                    placeholder   = "e.g. Senior Engineer"
                )
            }

            LabeledField(label = "Phone Number") {
                SettingsTextField(
                    value         = phoneNo,
                    onValueChange = { phoneNo = it },
                    placeholder   = "e.g. +977 98XXXXXXXX",
                    keyboardType  = KeyboardType.Phone
                )
            }

            LabeledField(label = "Department") {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick  = { showDeptDropdown = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(10.dp),
                        colors   = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
                        border   = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 14.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text  = departments.find { it.id == selectedDeptId }?.departmentName
                                    ?: "Select Department",
                                color = if (selectedDeptId == 0L)
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp
                            )
                            Icon(
                                imageVector        = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier           = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded          = showDeptDropdown,
                        onDismissRequest  = { showDeptDropdown = false },
                        modifier          = Modifier.fillMaxWidth(0.9f)
                    ) {
                        departments.forEach { dept ->
                            DropdownMenuItem(
                                text = { Text(dept.departmentName) },
                                onClick = {
                                    selectedDeptId   = dept.id
                                    showDeptDropdown = false
                                },
                                leadingIcon = if (dept.id == selectedDeptId) ({
                                    Icon(
                                        imageVector        = Icons.Default.Check,
                                        contentDescription = null,
                                        tint               = MaterialTheme.colorScheme.primary,
                                        modifier           = Modifier.size(16.dp)
                                    )
                                }) else null
                            )
                        }
                    }
                }
            }

            Button(
                onClick  = { onSave(firstname, lastname, position, phoneNo, selectedDeptId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                enabled  = firstname.isNotBlank() || lastname.isNotBlank(),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text       = "Save Changes",
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}