package com.swifttechnology.bookingsystem.features.participants.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingDropdownField
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingTextField
import com.swifttechnology.bookingsystem.shared.components.PrimaryButton
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddParticipantScreen(
    initialParticipant: Participant? = null,
    onClose: () -> Unit,
    onContinue: () -> Unit,
    viewModel: AddParticipantViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onContinue()
            viewModel.resetState()
        }
    }
    var firstName by remember(initialParticipant) { mutableStateOf(initialParticipant?.name?.split(" ")?.firstOrNull() ?: "") }
    var lastName by remember(initialParticipant) { mutableStateOf(initialParticipant?.name?.split(" ")?.drop(1)?.joinToString(" ") ?: "") }
    var email by remember(initialParticipant) { mutableStateOf(initialParticipant?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phoneNumber by remember(initialParticipant) { mutableStateOf(initialParticipant?.phone ?: "") }
    var position by remember(initialParticipant) { mutableStateOf(initialParticipant?.position ?: "") }

    var role by remember(initialParticipant) { mutableStateOf(initialParticipant?.role ?: "") }
    var roleExpanded by remember { mutableStateOf(false) }
    val roleOptions = listOf("Admin", "User", "Manager")

    var department by remember(initialParticipant) { mutableStateOf(initialParticipant?.department ?: "") }
    var departmentExpanded by remember { mutableStateOf(false) }
    val departmentOptions = listOf("Mobile App", "Web Development", "HR", "Design")

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

                // Top right close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onClose,
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

                // Header
                Text(
                    text = if (initialParticipant != null) "Edit member" else "Add New Participant",
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

                // Name Row (First Name and Last Name)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        BookingTextField(
                            label = "First Name",
                            value = firstName,
                            placeholder = "e.g. John",
                            isRequired = true,
                            onValueChange = { firstName = it }
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        BookingTextField(
                            label = "Last Name",
                            value = lastName,
                            placeholder = "e.g. Doe",
                            isRequired = true,
                            onValueChange = { lastName = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.md))

                // Email
                BookingTextField(
                    label = "Email",
                    value = email,
                    placeholder = "e.g. john.doe@example.com",
                    isRequired = true,
                    onValueChange = { email = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Password
                BookingTextField(
                    label = "Password",
                    value = password,
                    placeholder = "Enter new password",
                    isRequired = true,
                    onValueChange = { password = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Phone Number
                BookingTextField(
                    label = "Phone Number",
                    value = phoneNumber,
                    placeholder = "Enter your phone number",
                    isRequired = true,
                    onValueChange = { phoneNumber = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Position
                BookingTextField(
                    label = "Position",
                    value = position,
                    placeholder = "e.g. Mobile App Developer",
                    isRequired = true,
                    onValueChange = { position = it }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Role Dropdown
                BookingDropdownField(
                    label = "Role ",
                    value = role,
                    placeholder = "Select user's role",
                    isRequired = true,
                    expanded = roleExpanded,
                    options = roleOptions,
                    onExpandChange = { roleExpanded = it },
                    onOptionSelected = { role = it }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Department Dropdown
                BookingDropdownField(
                    label = "Department ",
                    value = department,
                    placeholder = "Select user's department",
                    isRequired = true,
                    expanded = departmentExpanded,
                    options = departmentOptions,
                    onExpandChange = { departmentExpanded = it },
                    onOptionSelected = { department = it }
                )

                Spacer(modifier = Modifier.height(120.dp)) // space for button
            }

            // Continue Button at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
                    .background(Color.White)
            ) {
                if (uiState.error != null) {
                    Text(
                        text = uiState.error ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.TopCenter).padding(bottom = 8.dp)
                    )
                }
                
                Button(
                    onClick = {
                        viewModel.createParticipant(
                            firstname = firstName,
                            lastname = lastName,
                            email = email,
                            position = position,
                            phoneNo = phoneNumber,
                            password = password,
                            role = role,
                            department = department
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4A3496) // The purple color from screenshot
                    ),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
