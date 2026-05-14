package com.swifttechnology.bookingsystem.features.calendar.presentation.calendarComponents.shared

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import kotlinx.coroutines.launch
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.Dp


private val YEARS = (2020..2050).toList()
private val MONTHS = Month.values().toList()

// Visible rows in the drum roll; the middle row is the selected one
private const val VISIBLE_ITEMS = 5
private const val PADDING_ITEMS = VISIBLE_ITEMS / 2   // 2 blank rows above & below

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearMonthPickerBottomSheet(
    currentYearMonth: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (YearMonth) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var selectedYear  by remember { mutableStateOf(currentYearMonth.year) }
    var selectedMonth by remember { mutableStateOf(currentYearMonth.month) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.customColors.divider)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = Spacing.lg)
        ) {
            // Title
            Text(
                text = "Select Month & Year",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg, vertical = Spacing.md),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(color = MaterialTheme.customColors.divider)

            Spacer(modifier = Modifier.height(Spacing.md))

            // Drum roll picker
            val itemHeightDp = 48.dp

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                contentAlignment = Alignment.Center
            ) {
                // Selection highlight bar (behind both columns)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeightDp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f))
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.30f),
                            RoundedCornerShape(14.dp)
                        )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Month column
                    DrumRollColumn(
                        modifier = Modifier.weight(1.2f),
                        items = MONTHS.map { it.getDisplayName(TextStyle.FULL, Locale.getDefault()) },
                        selectedIndex = selectedMonth.ordinal,
                        itemHeight = itemHeightDp,
                        onItemSelected = { idx -> selectedMonth = MONTHS[idx] }
                    )

                    // Year column
                    DrumRollColumn(
                        modifier = Modifier.weight(0.8f),
                        items = YEARS.map { it.toString() },
                        selectedIndex = YEARS.indexOf(selectedYear).coerceAtLeast(0),
                        itemHeight = itemHeightDp,
                        onItemSelected = { idx -> selectedYear = YEARS[idx] }
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.lg))

            HorizontalDivider(color = MaterialTheme.customColors.divider)

            Spacer(modifier = Modifier.height(Spacing.md))

            // Confirm button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, MaterialTheme.customColors.divider
                    )
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            onConfirm(YearMonth.of(selectedYear, selectedMonth))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        "Go to date",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun DrumRollColumn(
    modifier: Modifier = Modifier,
    items: List<String>,
    selectedIndex: Int,
    itemHeight: Dp,
    onItemSelected: (Int) -> Unit
) {
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val itemHeightPx = with(density) { itemHeight.toPx() }

    // Initialize state with the provided selectedIndex
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Derived state for the currently "centered" item
    val currentCenterIndex by remember {
        derivedStateOf {
            val firstVisible = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            (if (offset > itemHeightPx / 2) firstVisible + 1 else firstVisible)
                .coerceIn(0, items.lastIndex)
        }
    }

    // Haptic feedback on every index change
    LaunchedEffect(currentCenterIndex) {
        if (listState.isScrollInProgress) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Notify parent only when scroll settles to avoid infinite loops and stuttering
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            onItemSelected(currentCenterIndex)
        }
    }

    // Ensure picker stays in sync if parent state changes externally, but don't fight active scroll
    LaunchedEffect(selectedIndex) {
        if (!listState.isScrollInProgress && selectedIndex != currentCenterIndex) {
            listState.scrollToItem(selectedIndex)
        }
    }

    val totalHeightDp = itemHeight * VISIBLE_ITEMS

    Box(modifier = modifier.height(totalHeightDp)) {
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp)
        ) {
            // Padding items at the top to allow center alignment
            items(PADDING_ITEMS) {
                Box(modifier = Modifier.height(itemHeight).fillMaxWidth())
            }

            itemsIndexed(items) { index, label ->
                val isSelected = index == currentCenterIndex

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = if (isSelected) 16.sp else 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Padding items at the bottom
            items(PADDING_ITEMS) {
                Box(modifier = Modifier.height(itemHeight).fillMaxWidth())
            }
        }

        // Top fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * PADDING_ITEMS)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.surface,
                            Color.Transparent
                        )
                    )
                )
        )
        // Bottom fade
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight * PADDING_ITEMS)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
        )
    }
}
