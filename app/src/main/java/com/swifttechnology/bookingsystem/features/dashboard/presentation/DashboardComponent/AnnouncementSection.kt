package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors

import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.font.FontWeight

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDateForCarousel(raw: String): String {
    return runCatching {
        val odt = OffsetDateTime.parse(raw)
        odt.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
    }.getOrDefault(raw)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AnnouncementSection(
    announcements: List<Announcement>,
    isLoading: Boolean,
    onView: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { announcements.size })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.ms)
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (announcements.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                Text(
                    text = "No current announcements",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.customColors.textSecondary)
                )
            }
        } else {
            HorizontalPager(
            state    = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val item = announcements[page]

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(CornerRadius.lg),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.customColors.dashboardBg),
                elevation = CardDefaults.cardElevation(Elevation.none)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 14.dp, vertical = Spacing.md)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier          = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier         = Modifier
                                .size(Spacing.lg)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector        = if (item.priorityLevel == "HIGH") Icons.Outlined.Error else Icons.Outlined.NotificationsNone,
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(Spacing.ml)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.fillMaxWidth(0.92f)) {
                            Text(
                                text     = item.title,
                                color    = MaterialTheme.colorScheme.onSurface,
                                style    = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text     = item.message,
                                color    = MaterialTheme.customColors.textSecondary,
                                style    = MaterialTheme.typography.bodySmall,
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text     = item.authorName,
                            color    = MaterialTheme.colorScheme.onSurface,
                            style    = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text     = formatDateForCarousel(item.createdAt),
                            color    = MaterialTheme.colorScheme.onSurface,
                            style    = MaterialTheme.typography.labelLarge,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.ms))

                    OutlinedButton(
                        onClick  = onView,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        shape  = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, MaterialTheme.customColors.divider),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
                    ) {
                        Text(
                            text  = "View",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        } // ends HorizontalPager lambda

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            repeat(announcements.size) { index ->
                val isActive = index == pagerState.currentPage
                Box(
                    modifier = Modifier
                        .size(if (isActive) 7.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Primary else Color(0xFFD6C9FF))
                )
                if (index < announcements.lastIndex) Spacer(modifier = Modifier.width(6.dp))
            }
        }
    } // ends else block
} // ends Column
} // ends AnnouncementSection
