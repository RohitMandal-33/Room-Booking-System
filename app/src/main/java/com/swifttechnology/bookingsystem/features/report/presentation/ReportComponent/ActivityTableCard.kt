package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.report.presentation.ActivityEntry
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel

 
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
 
internal data class ColDef(val label: String, val width: Dp)

internal val columns = listOf(
    ColDef("Meeting Title", 220.dp),
    ColDef("Date",          120.dp),
    ColDef("Start Time",    120.dp),
    ColDef("End Time",      120.dp),
    ColDef("Room",          160.dp),
    ColDef("Created By",    160.dp),
)

 
// Activity Table Card
 
@Composable
fun ActivityTableCard(vm: ReportsAnalyticsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(ColorSurface)
    ) {
        // Columns + Export toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, ColorChipBorder, RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .clickable {}
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SwapVert, null,
                    tint = ColorPrimaryMedium, modifier = Modifier.size(18.dp)
                )
                Text(
                    "Columns", style = TextStyle(
                        fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ColorPrimaryMedium
                    )
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ColorPrimary)
                    .clickable { vm.onExport() }
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.SwapVert, null,
                    tint = Color.White, modifier = Modifier.size(18.dp)
                )
                Text(
                    "Export", style = TextStyle(
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White
                    )
                )
            }
        }

        HorizontalDivider(color = ColorDivider)

        // Scrollable table
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                TableHeaderRow()
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
                        TableBodyRow(entry, isEven = i % 2 == 0)
                        if (i < entries.lastIndex) HorizontalDivider(color = ColorDivider)
                    }
                }
            }
        }

        HorizontalDivider(color = ColorDivider)
        TableFooter(vm)
    }
}

 
// Table Header Row
 
@Composable
internal fun TableHeaderRow() {
    Row(
        modifier = Modifier
            .background(Color.White)
            .padding(vertical = 14.dp)
    ) {
        columns.forEach { col ->
            Text(
                text = col.label,
                modifier = Modifier
                    .width(col.width)
                    .padding(horizontal = 16.dp),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp, color = ColorOnSurface),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

 
// Table Body Row
 
@Composable
internal fun TableBodyRow(entry: ActivityEntry, isEven: Boolean) {
    val cells = listOf(
        entry.meetingTitle, entry.date, entry.startTime, entry.endTime,
        entry.roomName, entry.createdBy
    )
    Row(
        modifier = Modifier
            .background(if (isEven) Color.White else ColorSurface)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        cells.forEachIndexed { i, text ->
            Text(
                text = text,
                modifier = Modifier
                    .width(columns[i].width)
                    .padding(horizontal = 16.dp),
                style = TextStyle(
                    fontWeight = if (i == 0) FontWeight.Medium else FontWeight.Normal,
                    fontSize = 14.sp,
                    color = if (i == 0) ColorOnSurface else ColorOnSurfaceVar
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
