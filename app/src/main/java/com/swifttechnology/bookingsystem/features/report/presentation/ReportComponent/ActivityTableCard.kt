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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.report.presentation.ActivityEntry
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel
import com.swifttechnology.bookingsystem.R
import androidx.annotation.RequiresApi


// ─── Design Tokens ────────────────────────────────────────────────────────────

internal val ColorPrimary       = Color(0xFF4A2D9C)
internal val ColorPrimaryLight  = Color(0xFFEDE9F8)
internal val ColorPrimaryMedium = Color(0xFF7C5CBF)
internal val ColorSurface       = Color(0xFFF0EDF8)
internal val ColorOnSurface     = Color(0xFF1B1B20)
internal val ColorOnSurfaceVar  = Color(0xFF5F5C70)
internal val ColorDivider       = Color(0xFFE4E0F0)
internal val ColorChipBorder    = Color(0xFFCFC2D7)


// ─── Column Definitions ───────────────────────────────────────────────────────

/**
 * [minWidth] is the minimum column width used when the table must scroll horizontally.
 * [weight]   is the proportional weight used when all columns fit on screen.
 */
internal data class ColDef(
    val label: String,
    val minWidth: Dp,
    val weight: Float = 1f,
    val isFixed: Boolean = false
)

internal val allColumns = listOf(
    ColDef("Meeting Title", minWidth = 180.dp, weight = 2f, isFixed = true),
    ColDef("Date",          minWidth = 110.dp, weight = 1.2f),
    ColDef("Start Time",    minWidth = 110.dp, weight = 1.2f),
    ColDef("End Time",      minWidth = 110.dp, weight = 1.2f),
    ColDef("Room",          minWidth = 140.dp, weight = 1.5f),
    ColDef("Created By",    minWidth = 140.dp, weight = 1.5f),
)


// ─── Activity Table Card ──────────────────────────────────────────────────────

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ActivityTableCard(vm: ReportsAnalyticsViewModel) {

    var showColumnsSheet by remember { mutableStateOf(false) }
    var visibleColumns   by remember { mutableStateOf(allColumns.toSet()) }

    // Preserve original declaration order
    val activeColumns = allColumns.filter { visibleColumns.contains(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurface)
    ) {

        // ── Header bar: date range + Columns button ──────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment    = Alignment.CenterVertically
        ) {
            val today = java.time.LocalDate.now()
            var startDate: java.time.LocalDate? = null
            var endDate:   java.time.LocalDate? = null

            when (vm.selectedDate) {
                "Today"   -> { startDate = today; endDate = today }
                "7 Days"  -> { startDate = today.minusDays(7);  endDate = today }
                "30 Days" -> { startDate = today.minusDays(30); endDate = today }
                "Custom"  -> {
                    if (vm.customStartDate != null && vm.customEndDate != null) {
                        val zone = java.time.ZoneId.of("UTC")
                        startDate = java.time.Instant.ofEpochMilli(vm.customStartDate!!).atZone(zone).toLocalDate()
                        endDate   = java.time.Instant.ofEpochMilli(vm.customEndDate!!).atZone(zone).toLocalDate()
                    }
                }
            }

            if (startDate != null && endDate != null) {
                val fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text       = "Date From",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color      = ColorOnSurfaceVar.copy(alpha = 0.7f)
                    )
                    Text(
                        text       = "${startDate.format(fmt)} to ${endDate.format(fmt)}",
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color      = ColorOnSurfaceVar
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
                verticalAlignment    = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector  = Icons.Default.SwapVert,
                    contentDescription = null,
                    tint         = ColorPrimaryMedium,
                    modifier     = Modifier.size(18.dp)
                )
                Text(
                    "Columns",
                    style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorPrimaryMedium)
                )
            }
        }

        HorizontalDivider(color = ColorDivider)

        // ── Scrollable table body ────────────────────────────────────────────
        /**
         * Strategy:
         *  1. Measure the available container width via BoxWithConstraints.
         *  2. Calculate the total minimum width needed by all active columns.
         *  3. If the columns fit  → distribute remaining space via weight (fills screen).
         *  4. If they don't fit  → fall back to minWidth per column + horizontal scroll.
         */
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val availableWidth   = maxWidth
            val totalMinWidth    = activeColumns.fold(0.dp) { acc, col -> acc + col.minWidth }
            val needsScroll      = totalMinWidth > availableWidth
            val totalWeight      = activeColumns.sumOf { it.weight.toDouble() }.toFloat()

            // Pre-compute each column's resolved width for non-scroll mode
            val resolvedWidths: List<Dp> = if (!needsScroll) {
                activeColumns.map { col ->
                    availableWidth * (col.weight / totalWeight)
                }
            } else emptyList()

            val scrollState = rememberScrollState()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(if (needsScroll) Modifier.horizontalScroll(scrollState) else Modifier)
            ) {
                Column(
                    modifier = if (needsScroll) Modifier.width(IntrinsicSize.Max)
                    else             Modifier.fillMaxWidth()
                ) {
                    TableHeaderRow(activeColumns, needsScroll, resolvedWidths)
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
                            TableBodyRow(
                                entry         = entry,
                                isEven        = i % 2 == 0,
                                activeColumns = activeColumns,
                                needsScroll   = needsScroll,
                                resolvedWidths = resolvedWidths
                            )
                            if (i < entries.lastIndex) HorizontalDivider(color = ColorDivider)
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = ColorDivider)
        TableFooter(vm)
    }

    // ── Column-picker bottom sheet ───────────────────────────────────────────
    if (showColumnsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showColumnsSheet = false },
            containerColor   = ColorSurface
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Select Columns",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = ColorOnSurface)
                )
                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    allColumns.forEach { col ->
                        val isSelected = visibleColumns.contains(col)
                        FilterChip(
                            selected = isSelected,
                            onClick  = {
                                if (col.isFixed) return@FilterChip          // fixed columns cannot be hidden
                                if (isSelected) {
                                    if (visibleColumns.size > 2) {          // keep at least 2 visible
                                        visibleColumns = visibleColumns - col
                                    }
                                } else {
                                    visibleColumns = visibleColumns + col
                                }
                            },
                            label       = { Text(col.label) },
                            leadingIcon = {
                                Icon(
                                    imageVector  = if (isSelected) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    modifier     = Modifier.size(16.dp)
                                )
                            },
                            colors = if (col.isFixed) {
                                FilterChipDefaults.filterChipColors(containerColor = ColorDivider)
                            } else FilterChipDefaults.filterChipColors()
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


// ─── Table Header Row ─────────────────────────────────────────────────────────

/**
 * @param activeColumns   the ordered list of columns currently visible.
 * @param needsScroll     true  → each column uses its [ColDef.minWidth] (table scrolls).
 *                        false → each column uses its pre-computed [resolvedWidths] entry,
 *                                which together sum to exactly the container width.
 * @param resolvedWidths  proportional widths (only meaningful when !needsScroll).
 */
@Composable
internal fun TableHeaderRow(
    activeColumns:  List<ColDef>,
    needsScroll:    Boolean,
    resolvedWidths: List<Dp>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 14.dp)
    ) {
        activeColumns.forEachIndexed { index, col ->
            Text(
                text     = col.label,
                modifier = if (needsScroll) {
                    Modifier
                        .width(col.minWidth)
                        .padding(horizontal = 16.dp)
                } else {
                    Modifier
                        .width(resolvedWidths[index])
                        .padding(horizontal = 16.dp)
                },
                style    = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    color      = ColorOnSurface
                ),
                maxLines  = 1,
                overflow  = TextOverflow.Ellipsis
            )
        }
    }
}


// ─── Table Body Row ───────────────────────────────────────────────────────────

@Composable
internal fun TableBodyRow(
    entry:          ActivityEntry,
    isEven:         Boolean,
    activeColumns:  List<ColDef>,
    needsScroll:    Boolean,
    resolvedWidths: List<Dp>
) {
    val cellsMap = mapOf(
        "Meeting Title" to entry.meetingTitle,
        "Date"          to entry.date,
        "Start Time"    to entry.startTime,
        "End Time"      to entry.endTime,
        "Room"          to entry.roomName,
        "Created By"    to entry.createdBy
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isEven) Color.White else ColorSurface)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        activeColumns.forEachIndexed { index, col ->
            Text(
                text     = cellsMap[col.label] ?: "",
                modifier = if (needsScroll) {
                    Modifier
                        .width(col.minWidth)
                        .padding(horizontal = 16.dp)
                } else {
                    Modifier
                        .width(resolvedWidths[index])
                        .padding(horizontal = 16.dp)
                },
                style    = TextStyle(
                    fontWeight = if (col.isFixed) FontWeight.Medium else FontWeight.Normal,
                    fontSize   = 14.sp,
                    color      = if (col.isFixed) ColorOnSurface else ColorOnSurfaceVar
                ),
                maxLines  = 2,
                overflow  = TextOverflow.Ellipsis
            )
        }
    }
}