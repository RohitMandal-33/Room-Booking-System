package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Button
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel


// Dropdown Filter Strip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipStrip(vm: ReportsAnalyticsViewModel) {
    var showCustomDateSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Clear Filters",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = ColorPrimary),
                modifier = Modifier
                    .clickable { vm.clearFilters() }
                    .padding(bottom = 10.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownFilterChip(
                icon = Icons.Default.CalendarToday, label = "Date",
                selected = vm.selectedDate, options = vm.dateOptions,
                onSelect = { 
                    if (it == "Custom") {
                        showCustomDateSheet = true
                    } else {
                        vm.onDateSelected(it) 
                    }
                }
            )
            DropdownFilterChip(
                icon = Icons.Default.MeetingRoom, label = "Room",
                selected = vm.selectedRoom, options = vm.roomOptions,
                onSelect = vm::onRoomSelected
            )
            DropdownFilterChip(
                icon = Icons.Default.Business, label = "Department",
                selected = vm.selectedDepartment, options = vm.departmentOptions,
                onSelect = vm::onDepartmentSelected
            )
            DropdownFilterChip(
                icon = Icons.Default.SwapVert, label = "Sort",
                selected = vm.selectedSort, options = vm.sortOptions,
                onSelect = vm::onSortSelected
            )
        }
    }

    if (showCustomDateSheet) {
        val dateRangePickerState = rememberDateRangePickerState()

        ModalBottomSheet(
            onDismissRequest = { showCustomDateSheet = false },
            modifier = Modifier.fillMaxHeight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = { showCustomDateSheet = false },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            vm.customStartDate = dateRangePickerState.selectedStartDateMillis
                            vm.customEndDate = dateRangePickerState.selectedEndDateMillis
                            vm.onDateSelected("Custom")
                            showCustomDateSheet = false
                        },
                        enabled = dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}


// Individual Dropdown Filter Chip

@Composable
fun DropdownFilterChip(
    icon: ImageVector,
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isActive = selected != "All" && selected != "Asc"
    val displayText = if (isActive) selected else label

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    1.dp,
                    if (isActive) ColorPrimaryMedium else ColorChipBorder,
                    RoundedCornerShape(20.dp)
                )
                .background(if (isActive) ColorPrimaryLight else Color.White)
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = if (isActive) ColorPrimaryMedium else ColorOnSurfaceVar
            )
            Text(
                text = displayText,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) ColorPrimaryMedium else ColorOnSurface
                ),
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = if (isActive) ColorPrimaryMedium else ColorOnSurfaceVar
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
                .widthIn(min = 160.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) ColorPrimary else ColorOnSurface
                            )
                        )
                    },
                    onClick = { onSelect(option); expanded = false },
                    modifier = Modifier.background(if (isSelected) ColorPrimaryLight else Color.White)
                )
            }
        }
    }
}
