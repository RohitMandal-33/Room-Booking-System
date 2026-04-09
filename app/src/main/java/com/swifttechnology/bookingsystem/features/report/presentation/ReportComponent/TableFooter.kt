package com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import com.swifttechnology.bookingsystem.features.report.presentation.ReportsAnalyticsViewModel

// ─────────────────────────────────────────────
// Pagination Footer
// ─────────────────────────────────────────────
@Composable
fun TableFooter(vm: ReportsAnalyticsViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = if (vm.totalResults == 0) "No results"
            else "Showing ${vm.showingStart} of ${vm.totalResults} results",
            style = TextStyle(fontSize = 13.sp, color = ColorOnSurfaceVar)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prev
            PaginationButton(
                label = "Prev",
                enabled = vm.canGoPrevious,
                active = false,
                onClick = vm::onPreviousPage
            )

            // Page numbers
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

            // Next
            PaginationButton(
                label = "Next",
                enabled = vm.canGoNext,
                active = false,
                onClick = vm::onNextPage
            )
        }
    }
}

// ─────────────────────────────────────────────
// Pagination Button
// ─────────────────────────────────────────────
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
