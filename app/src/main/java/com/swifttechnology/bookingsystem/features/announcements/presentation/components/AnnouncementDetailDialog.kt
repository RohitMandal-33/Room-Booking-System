package com.swifttechnology.bookingsystem.features.announcements.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun formatDateString(raw: String): String {
    return runCatching {
        val odt = OffsetDateTime.parse(raw)
        odt.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
    }.getOrDefault(raw)
}

@Composable
fun AnnouncementDetailDialog(
    announcement: Announcement,
    onDismiss: () -> Unit = {},
    onMoreOptions: () -> Unit = {}
) {
    val colors = MaterialTheme.customColors

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {

                // Close button row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.deepBlack
                        )
                    }
                }

                // Pinned Badge
                if (announcement.pinned) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 24.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PushPin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Pinned",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Title + overflow menu
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = announcement.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.deepBlack,
                        lineHeight = 30.sp,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onMoreOptions,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Author name + department tag
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = announcement.authorName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.deepBlack
                    )
                    if (announcement.authorPosition.isNotBlank()) {
                        Text(
                            text = "• ${announcement.authorPosition}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Date
                Text(
                    text = formatDateString(announcement.createdAt),
                    fontSize = 13.sp,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Body card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = colors.dashboardBg,
                    tonalElevation = 0.dp
                ) {
                    Text(
                        text = announcement.message,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = colors.deepBlack,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}
