package com.swifttechnology.bookingsystem.features.report.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent.ActivityTableCard
import com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent.FilterChipStrip
import com.swifttechnology.bookingsystem.features.report.presentation.ReportComponent.TablePagination


// Local design tokens
 
private val ColorBackground    = Color(0xFFFFFFFF)
private val ColorSurfaceVariant = Color(0xFFE9E7EE)
private val ColorOnSurface     = Color(0xFF1B1B20)
private val ColorOnSurfaceVar  = Color(0xFF5F5C70)

 
// Entry-Point Screen (Hilt-wired)
 
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportScreen(
    searchQuery: String,
    onNavigate: (String) -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(searchQuery) {
        viewModel.onSearchQueryChanged(searchQuery)
    }

    // Analytics sub-ViewModel (needs Hilt injection now)
    val analyticsVm: ReportsAnalyticsViewModel = hiltViewModel()

    ReportsAnalyticsScreen(vm = analyticsVm)
}

 
// Analytics Screen
 
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportsAnalyticsScreen(vm: ReportsAnalyticsViewModel) {
    Scaffold(containerColor = ColorBackground) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader()
            FilterChipStrip(vm)
            Spacer(modifier = Modifier.height(12.dp))
            ActivityTableCard(vm)
            
            TablePagination(
                vm = vm,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}

 
// Screen Header
 
@Composable
private fun ScreenHeader() {
    Column(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp)
    ) {
        Text(
            text = "Reports & Analytics",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp, color = ColorOnSurface)
        )
        Text(
            text = "View and export reservation data",
            style = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = ColorOnSurfaceVar)
        )
    }
}
