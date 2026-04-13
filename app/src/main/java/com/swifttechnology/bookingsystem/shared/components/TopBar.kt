package com.swifttechnology.bookingsystem.shared.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TopBar(
    title: String,
    onMenuClick: () -> Unit,
    onTrailingClick: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null,
    showEditIcon: Boolean = false,
    isEditMode: Boolean = false,
    showTodayIcon: Boolean = false,
    onTodayClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onBackClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left Menu Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.customColors.neutral100)
                .border(1.dp, MaterialTheme.customColors.neutral200, RoundedCornerShape(12.dp))
                .clickable { 
                    if (onBackClick != null) onBackClick() else onMenuClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (onBackClick != null) Icons.AutoMirrored.Filled.ArrowBack else ImageVector.vectorResource(id = R.drawable.dock_to_right_24px),
                contentDescription = if (onBackClick != null) "Back" else "Open navigation",
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.customColors.deepBlack
            )
        }

        // Title
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = 16.sp,
                color = MaterialTheme.customColors.deepBlack
            ),
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        // Right Action Icon
        val rightActionModifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.customColors.neutral100)
            .border(1.dp, MaterialTheme.customColors.neutral200, RoundedCornerShape(12.dp))

        if (onTrailingClick != null && trailingIcon != null) {
            Box(
                modifier = rightActionModifier.clickable { onTrailingClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "Trailing action",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.customColors.deepBlack
                )
            }
        } else if (showEditIcon) {
            Box(
                modifier = rightActionModifier.clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                if (isEditMode) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Exit edit mode",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.customColors.deepBlack
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.edit_square_24px),
                        contentDescription = "Edit",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.customColors.deepBlack
                    )
                }
            }
        } else if (showTodayIcon) {
            val today = LocalDate.now()
            Box(
                modifier = rightActionModifier.clickable { onTodayClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .size(24.dp)
                        .border(1.5.dp, MaterialTheme.customColors.deepBlack, RoundedCornerShape(4.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Muted "tab" at the top of the calendar icon
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(MaterialTheme.customColors.deepBlack)
                    )
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = today.dayOfMonth.toString(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.customColors.deepBlack,
                            lineHeight = 11.sp
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = rightActionModifier.clickable { /* Profile action */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "Profile",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.customColors.deepBlack
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun TopBarPreview() {
    MeetingRoomBookingTheme {
        Column {
            TopBar(
                title = "Meeting Rooms",
                onMenuClick = {}
            )
            TopBar(
                title = "Room Details",
                showEditIcon = true,
                onMenuClick = {}
            )
        }
    }
}
