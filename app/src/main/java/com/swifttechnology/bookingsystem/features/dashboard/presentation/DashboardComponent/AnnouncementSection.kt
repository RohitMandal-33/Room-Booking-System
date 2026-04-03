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

private data class AnnouncementItem(
    val message: String,
    val author: String,
    val date: String,
    val icon: ImageVector
)

private val announcements = listOf(
    AnnouncementItem(
        message = "Extremely Important Discussion that rivals the lord and all, my goodness very important and bla bla bla i gge ni...",
        author  = "Oshin Joshi",
        date    = "Mar 18",
        icon    = Icons.Outlined.Error
    ),
    AnnouncementItem(
        message = "System maintenance scheduled for this weekend. Please plan accordingly.",
        author  = "Oshin Joshi",
        date    = "Mar 19",
        icon    = Icons.Outlined.NotificationsNone
    ),
    AnnouncementItem(
        message = "New room booking policy effective next Monday.",
        author  = "Oshin Joshi",
        date    = "Mar 20",
        icon    = Icons.Outlined.PushPin
    )
)

@Composable
fun AnnouncementSection(onView: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { announcements.size })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.ms)
    ) {
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
                                imageVector        = item.icon,
                                contentDescription = null,
                                tint               = Color.White,
                                modifier           = Modifier.size(Spacing.ml)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text     = item.message,
                            color    = MaterialTheme.colorScheme.onSurface,
                            style    = MaterialTheme.typography.bodyMedium,
                            minLines = 3,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.92f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text     = item.author,
                            color    = MaterialTheme.colorScheme.onSurface,
                            style    = MaterialTheme.typography.labelLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(Spacing.sm))
                        Text(
                            text     = item.date,
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
        }

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
    }
}
