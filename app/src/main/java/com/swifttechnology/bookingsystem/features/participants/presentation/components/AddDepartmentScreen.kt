package com.swifttechnology.bookingsystem.features.participants.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.booking.presentation.components.BookingTextField
import com.swifttechnology.bookingsystem.features.participants.presentation.AddDepartmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDepartmentScreen(
    initialDepartment: com.swifttechnology.bookingsystem.features.department.domain.model.Department? = null,
    onClose: () -> Unit,
    onContinue: (String) -> Unit,
    viewModel: AddDepartmentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    var name by remember { mutableStateOf(initialDepartment?.departmentName ?: "") }
    var description by remember { mutableStateOf(initialDepartment?.description ?: "") }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onContinue(if (initialDepartment != null) "Department updated successfully" else "Department added successfully")
            viewModel.resetState()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
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
                Spacer(modifier = Modifier.height(statusBarPadding + Spacing.lg))

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
                    text = if (initialDepartment != null) "Edit Department" else "Add New Department",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.customColors.deepBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Please Fill all the details",
                    fontSize = 14.sp,
                    color = MaterialTheme.customColors.neutral700
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Department Name
                BookingTextField(
                    label = "Department Name",
                    value = name,
                    placeholder = "e.g. Engineering",
                    isRequired = true,
                    onValueChange = { name = it }
                )

                Spacer(modifier = Modifier.height(Spacing.md))

                // Description
                BookingTextField(
                    label = "Description",
                    value = description,
                    placeholder = "Enter department description",
                    isRequired = false,
                    onValueChange = { description = it }
                )

                Spacer(modifier = Modifier.height(120.dp)) // space for button
            }

            // Continue Button at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = {
                            if (initialDepartment != null) {
                                viewModel.updateDepartment(initialDepartment.id, name, description)
                            } else {
                                viewModel.addDepartment(name, description)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = !uiState.isLoading && name.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = if (initialDepartment != null) "Update" else "Continue",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
