package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.report.presentation.ActivityEntry
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel
import com.swifttechnology.bookingsystem.R
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.LocalContext

 
// Design Tokens (local to this component)
 
internal val ColorPrimary        = Color(0xFF4A2D9C)
internal val ColorPrimaryLight   = Color(0xFFEDE9F8)
internal val ColorPrimaryMedium  = Color(0xFF7C5CBF)
internal val ColorSurface        = Color(0xFFF0EDF8)
internal val ColorOnSurface      = Color(0xFF1B1B20)
internal val ColorOnSurfaceVar   = Color(0xFF5F5C70)
internal val ColorDivider        = Color(0xFFE4E0F0)
internal val ColorChipBorder     = Color(0xFFCFC2D7)

 
// Column Definitions
 
internal data class ColDef(val label: String, val width: Dp, val isFixed: Boolean = false)

internal val allColumns = listOf(
    ColDef("Meeting Title", 220.dp, isFixed = true),
    ColDef("Date",          120.dp),
    ColDef("Start Time",    120.dp),
    ColDef("End Time",      120.dp),
    ColDef("Room",          160.dp),
    ColDef("Created By",    160.dp),
)

 
// Activity Table Card
 


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityTableCard(vm: ReportsAnalyticsViewModel) {
    var showColumnsSheet by remember { mutableStateOf(false) }
    var visibleColumns by remember { mutableStateOf(allColumns.toSet()) }
    val activeColumns = allColumns.filter { visibleColumns.contains(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurface)
    ) {
        // Date Filter Text and Columns 
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val today = java.time.LocalDate.now()
            var startDate: java.time.LocalDate? = null
            var endDate: java.time.LocalDate? = null

            when (vm.selectedDate) {
                "Today" -> { startDate = today; endDate = today }
                "7 Days" -> { startDate = today.minusDays(7); endDate = today }
                "30 Days" -> { startDate = today.minusDays(30); endDate = today }
                "Custom" -> {
                    if (vm.customStartDate != null && vm.customEndDate != null) {
                        startDate = java.time.Instant.ofEpochMilli(vm.customStartDate!!).atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                        endDate = java.time.Instant.ofEpochMilli(vm.customEndDate!!).atZone(java.time.ZoneId.of("UTC")).toLocalDate()
                    }
                }
            }

            if (startDate != null && endDate != null) {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                Column(
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    Text(
                        text = "Date From",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ColorOnSurfaceVar.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "${startDate.format(formatter)} to ${endDate.format(formatter)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorOnSurfaceVar
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, ColorChipBorder, RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable { showColumnsSheet = true }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = null,
                    tint = ColorPrimaryMedium,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Columns", style = TextStyle(
                        fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorPrimaryMedium
                    )
                )
            }
        }

        HorizontalDivider(color = ColorDivider)

        // Scrollable table
        val scrollState = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (activeColumns.size > 2) Modifier.horizontalScroll(scrollState)
                    else Modifier
                )
        ) {
            Column(
                modifier = if (activeColumns.size == 2) {
                    Modifier.fillMaxWidth()
                } else {
                    Modifier.width(IntrinsicSize.Max)
                }
            ) {
                TableHeaderRow(activeColumns)
                HorizontalDivider(color = ColorDivider)
                val entries = vm.visibleEntries
                if (entries.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No results found",
                            style = TextStyle(fontSize = 14.sp, color = ColorOnSurfaceVar)
                        )
                    }
                } else {
                    entries.forEachIndexed { i, entry ->
                        TableBodyRow(entry, isEven = i % 2 == 0, activeColumns)
                        if (i < entries.lastIndex) HorizontalDivider(color = ColorDivider)
                    }
                }
            }
        }

        HorizontalDivider(color = ColorDivider)
        TableFooter(vm)
    }

    if (showColumnsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showColumnsSheet = false },
            containerColor = ColorSurface
        ) {
            Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
                Text("Select Columns", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColorOnSurface))
                Spacer(modifier = Modifier.height(16.dp))
                
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    allColumns.forEach { col ->
                        val isSelected = visibleColumns.contains(col)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                if (col.isFixed) return@FilterChip
                                if (isSelected) {
                                    if (visibleColumns.size > 2) {
                                        visibleColumns = visibleColumns - col
                                    }
                                } else {
                                    visibleColumns = visibleColumns + col
                                }
                            },
                            label = { Text(col.label) },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isSelected) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = if (col.isFixed) {
                                FilterChipDefaults.filterChipColors(
                                    containerColor = ColorDivider
                                )
                            } else FilterChipDefaults.filterChipColors()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


// Table Header Row
 
@Composable
internal fun TableHeaderRow(activeColumns: List<ColDef>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 14.dp)
    ) {
        activeColumns.forEach { col ->
            Text(
                text = col.label,
                modifier = if (activeColumns.size == 2) {
                    Modifier.weight(1f).padding(horizontal = 16.dp)
                } else {
                    Modifier.width(col.width).padding(horizontal = 16.dp)
                },
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorOnSurface),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

 
// Table Body Row
 
@Composable
internal fun TableBodyRow(entry: ActivityEntry, isEven: Boolean, activeColumns: List<ColDef>) {
    val cellsMap = mapOf(
        "Meeting Title" to entry.meetingTitle,
        "Date" to entry.date,
        "Start Time" to entry.startTime,
        "End Time" to entry.endTime,
        "Room" to entry.roomName,
        "Created By" to entry.createdBy
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEven) Color.White else ColorSurface)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        activeColumns.forEach { col ->
            val text = cellsMap[col.label] ?: ""
            Text(
                text = text,
                modifier = if (activeColumns.size == 2) {
                    Modifier.weight(1f).padding(horizontal = 16.dp)
                } else {
                    Modifier.width(col.width).padding(horizontal = 16.dp)
                },
                style = TextStyle(
                    fontWeight = if (col.isFixed) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (col.isFixed) ColorOnSurface else ColorOnSurfaceVar
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
