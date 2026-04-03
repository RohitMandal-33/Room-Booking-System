package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import kotlinx.coroutines.launch

private val PurplePrimary = Color(0xFF6C3EE8)

@Composable
fun TimePickerBottomSheet(
    title: String,
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
) {
    val initHour12 = if (initialHour % 12 == 0) 12 else initialHour % 12
    val initMinute = (initialMinute / 5) * 5
    var isPm by remember { mutableStateOf(initialHour >= 12) }

    val hours   = remember { (1..12).toList() }
    val minutes = remember { (0..55 step 5).toList() }
    val ampmOpts = remember { listOf("AM", "PM") }

    // No offset needed, 2 padding items means items.indexOf(...) directly aligns it with the center
    val hourState   = rememberLazyListState(initialFirstVisibleItemIndex = hours.indexOf(initHour12).coerceAtLeast(0))
    val minuteState = rememberLazyListState(initialFirstVisibleItemIndex = minutes.indexOf(initMinute).coerceAtLeast(0))
    val ampmState   = rememberLazyListState(initialFirstVisibleItemIndex = ampmOpts.indexOf(if (isPm) "PM" else "AM").coerceAtLeast(0))

    // Derive selected values from scroll position (center item)
    val selectedHour by remember { derivedStateOf {
        val idx = (hourState.firstVisibleItemIndex +
                if (hourState.firstVisibleItemScrollOffset > 22) 1 else 0)
            .coerceIn(0, hours.lastIndex)
        hours[idx]
    }}
    val selectedMinute by remember { derivedStateOf {
        val idx = (minuteState.firstVisibleItemIndex +
                if (minuteState.firstVisibleItemScrollOffset > 22) 1 else 0)
            .coerceIn(0, minutes.lastIndex)
        minutes[idx]
    }}
    val selectedAmPm by remember { derivedStateOf {
        val idx = (ampmState.firstVisibleItemIndex +
                if (ampmState.firstVisibleItemScrollOffset > 22) 1 else 0)
            .coerceIn(0, ampmOpts.lastIndex)
        ampmOpts[idx]
    }}

    val surface = MaterialTheme.customColors.whitePure
    val onSurface = MaterialTheme.customColors.textPrimary

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(surface, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .padding(bottom = 32.dp)
            ) {
                // Drag handle
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 10.dp, bottom = 4.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .background(
                            MaterialTheme.customColors.neutral300,
                            RoundedCornerShape(2.dp)
                        )
                )

                // Header Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = onSurface,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(32.dp)
                            .background(MaterialTheme.customColors.neutral100, RoundedCornerShape(50))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Wheel picker row
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    // Selection highlight band
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .height(44.dp)
                            .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            RoundedCornerShape(10.dp)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        WheelColumn(
                            items = hours,
                            state = hourState,
                            displayText = { it.toString() },
                            modifier = Modifier.weight(1f)
                        )
                        WheelColumn(
                            items = minutes,
                            state = minuteState,
                            displayText = { String.format("%02d", it) },
                            modifier = Modifier.weight(1f)
                        )
                        WheelColumn(
                            items = ampmOpts,
                            state = ampmState,
                            displayText = { it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel", fontSize = 15.sp)
                    }
                    Button(
                        onClick = {
                            val isSelectedPm = selectedAmPm == "PM"
                            // Ensure we have definite integers for the callback
                            val hVal: Int = selectedHour
                            val mVal: Int = selectedMinute
                            
                            val hour24 = when {
                                isSelectedPm && hVal != 12  -> hVal + 12
                                !isSelectedPm && hVal == 12 -> 0
                                else                        -> hVal
                            }
                            onConfirm(hour24, mVal)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Text("Confirm", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> WheelColumn(
    items: List<T>,
    state: androidx.compose.foundation.lazy.LazyListState,
    displayText: (T) -> String,
    modifier: Modifier = Modifier
) {
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = state)
    val textPrimary  = MaterialTheme.customColors.textPrimary
    val ITEM_HEIGHT  = 44.dp

    LazyColumn(
        state = state,
        flingBehavior = flingBehavior,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()
                val fadePercent = 0.32f
                
                // Mask that is opaque in the center and transparent at top/bottom
                val maskBrush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    fadePercent to Color.Black,
                    (1f - fadePercent) to Color.Black,
                    1f to Color.Transparent
                )

                drawRect(
                    brush = maskBrush,
                    blendMode = BlendMode.DstIn
                )
            }
    ) {
        // Leading padding items
        items(2) {
            Box(Modifier.height(ITEM_HEIGHT).fillMaxWidth())
        }
        items(items) { item ->
            val itemIdx = items.indexOf(item)
            val visibleCenter = state.firstVisibleItemIndex +
                    if (state.firstVisibleItemScrollOffset > 22) 1 else 0
            val isSelected = itemIdx == visibleCenter + 0

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(ITEM_HEIGHT).fillMaxWidth()
            ) {
                Text(
                    text = displayText(item),
                    fontSize = 22.sp,
                    fontWeight = if (isSelected) FontWeight.Normal else FontWeight.Light,
                    color = if (isSelected)
                        textPrimary
                    else
                        textPrimary.copy(alpha = 0.35f)
                )
            }
        }
        // Trailing padding items
        items(2) {
            Box(Modifier.height(ITEM_HEIGHT).fillMaxWidth())
        }
    }
}