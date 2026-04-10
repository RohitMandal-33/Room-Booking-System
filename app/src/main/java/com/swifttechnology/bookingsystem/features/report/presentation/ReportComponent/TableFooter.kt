package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel


// Pagination Footer

@Composable
fun TableFooter(vm: ReportsAnalyticsViewModel) {
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            vm.saveExportToUri(context, uri)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Result count — left anchor
        Text(
            text = if (vm.totalResults == 0) "No results"
            else "Showing ${vm.showingStart} of ${vm.totalResults}",
            style = TextStyle(
                fontSize = 12.sp,
                color = ColorOnSurfaceVar.copy(alpha = 0.6f)
            ),
            modifier = Modifier.width(120.dp)
        )

        // Export button — right anchor
        Row(
            modifier = Modifier
                .width(120.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorPrimary)
                    .clickable { exportLauncher.launch("Reports_Export.csv") }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_download_24),
                    contentDescription = "Export",
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "Export",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun TablePagination(vm: ReportsAnalyticsViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PaginationButton(
                label = "Prev",
                enabled = vm.canGoPrevious,
                active = false,
                onClick = vm::onPreviousPage
            )

            val visiblePages = buildList {
                val start = maxOf(0, vm.currentPage - 1)
                val end = minOf(vm.totalPages - 1, vm.currentPage + 1)
                for (p in start..end) add(p)
            }
            visiblePages.forEach { page ->
                PaginationButton(
                    label = "${page + 1}",
                    enabled = true,
                    active = page == vm.currentPage,
                    onClick = { vm.goToPage(page) }
                )
            }

            PaginationButton(
                label = "Next",
                enabled = vm.canGoNext,
                active = false,
                onClick = vm::onNextPage
            )
        }
    }
}


// Pagination Button

@Composable
fun PaginationButton(
    label: String,
    enabled: Boolean,
    active: Boolean,
    onClick: () -> Unit
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (active) ColorPrimary else ColorPrimaryLight)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    active  -> Color.White
                    enabled -> ColorPrimaryMedium
                    else    -> ColorOnSurfaceVar.copy(alpha = 0.4f)
                }
            )
        )
    }
}
