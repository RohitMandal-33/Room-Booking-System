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
import androidx.compose.ui.graphics.Color
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

                // Top bar: Close + More options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.deepBlack
                        )
                    }
                    IconButton(onClick = onMoreOptions) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = colors.textSecondary
                        )
                    }
                }

                // Title + optional Pinned badge on same row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = announcement.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.deepBlack,
                        lineHeight = 30.sp,
                        modifier = Modifier.weight(1f)
                    )
                    if (announcement.pinned) {
                        Row(
                            modifier = Modifier
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
                                contentDescription = "Pinned",
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
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dates Section
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Posted: ${formatDateString(announcement.createdAt)}",
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )

                    if (!announcement.startDate.isNullOrBlank()) {
                        Text(
                            text = buildString {
                                append("Valid: ")
                                append(formatDateString(announcement.startDate))
                                if (!announcement.endDate.isNullOrBlank()) {
                                    append(" - ")
                                    append(formatDateString(announcement.endDate))
                                }
                            },
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Author row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = announcement.authorName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.deepBlack
                    )

                    if (announcement.authorPosition.isNotBlank()) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = announcement.authorPosition,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF2E7D32),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

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