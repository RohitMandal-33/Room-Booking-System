package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel
import java.text.SimpleDateFormat
import java.util.*


//     Quick-select presets

private val quickPresets = listOf("Today", "This week", "This month", "Last 30 days", "Custom")


//     Filter Chip Strip

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
                text = "Clear filters",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
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
                icon = Icons.Default.CalendarToday,
                label = "Date",
                selected = vm.selectedDate,
                options = vm.dateOptions,
                onSelect = {
                    if (it == "Custom") showCustomDateSheet = true
                    else vm.onDateSelected(it)
                }
            )
            DropdownFilterChip(
                icon = Icons.Default.MeetingRoom, label = "Room",
                selected = vm.selectedRoom, options = vm.roomOptions,
                onSelect = vm::onRoomSelected
            )
            MeetingTypeFilterChip(vm)
            DropdownFilterChip(
                icon = Icons.Default.Business, label = "User",
                selected = vm.selectedUser, options = vm.departmentOptions,
                onSelect = vm::onUserSelected
            )
            DropdownFilterChip(
                icon = Icons.Default.SwapVert, label = "Sort",
                selected = vm.selectedSort, options = vm.sortOptions,
                onSelect = vm::onSortSelected
            )
        }
    }

    if (showCustomDateSheet) {
        DateRangeSheet(
            initialStartMillis = vm.customStartDate,
            initialEndMillis = vm.customEndDate,
            onDismiss = { showCustomDateSheet = false },
            onApply = { start, end ->
                vm.customStartDate = start
                vm.customEndDate = end
                vm.onDateSelected("Custom")
                showCustomDateSheet = false
            }
        )
    }
}

@Composable
fun MeetingTypeFilterChip(vm: ReportsAnalyticsViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val menuMaxH = LocalConfiguration.current.screenHeightDp.dp * 0.5f
    val menuMaxW =
        minOf(420.dp, LocalConfiguration.current.screenWidthDp.dp - 32.dp).coerceAtLeast(160.dp)
    val selected = vm.selectedMeetingType
    val isActive = selected != null
    val displayText = selected?.name ?: "Type"
    val isDark = isSystemInDarkTheme()

    val idleBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isDark) 0.95f else 1f)

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    if (isActive) 1.dp else 1.dp,
                    if (isActive) MaterialTheme.colorScheme.primary else idleBorder,
                    RoundedCornerShape(20.dp)
                )
                .background(
                    if (isActive) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.28f else 1f)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Category,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = displayText,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 160.dp, max = menuMaxW)
                .heightIn(max = menuMaxH)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("All", style = TextStyle(fontSize = 14.sp)) },
                onClick = { vm.onMeetingTypeSelected(null); expanded = false }
            )
            vm.meetingTypes.forEach { type ->
                val isSelected = type.id == selected?.id
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type.name ?: "",
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    onClick = { vm.onMeetingTypeSelected(type); expanded = false },
                    modifier = Modifier.background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}

//     Individual Dropdown Filter Chip

@Composable
fun DropdownFilterChip(
    icon: ImageVector,
    label: String,
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val menuMaxH = LocalConfiguration.current.screenHeightDp.dp * 0.5f
    val menuMaxW =
        minOf(420.dp, LocalConfiguration.current.screenWidthDp.dp - 32.dp).coerceAtLeast(160.dp)
    val isActive = selected != "All" && selected != "Asc"
    val displayText = if (isActive) selected else label
    val isDark = isSystemInDarkTheme()
    val idleBorder = MaterialTheme.colorScheme.outlineVariant.copy(alpha = if (isDark) 0.95f else 1f)

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .border(
                    1.dp,
                    if (isActive) MaterialTheme.colorScheme.primary else idleBorder,
                    RoundedCornerShape(20.dp)
                )
                .background(
                    if (isActive) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface.copy(alpha = if (isDark) 0.28f else 1f)
                )
                .clickable { expanded = !expanded }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = displayText,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                ),
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 160.dp, max = menuMaxW)
                .heightIn(max = menuMaxH)
                .background(MaterialTheme.colorScheme.surface)
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
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        )
                    },
                    onClick = { onSelect(option); expanded = false },
                    modifier = Modifier.background(
                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
    }
}