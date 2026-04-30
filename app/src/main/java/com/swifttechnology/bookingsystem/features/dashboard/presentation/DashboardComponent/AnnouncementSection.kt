package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.*
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    onView: (Announcement) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { announcements.size })

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.ms)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.PushPin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .rotate(-30f)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Announcements",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isLoading && announcements.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 1.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${announcements.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(Spacing.xs))
                    .clickable { onSeeAllClick() }
                    .padding(horizontal = Spacing.xs, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(Spacing.sm))

        //  States
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Primary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            announcements.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(MaterialTheme.customColors.dashboardBg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.PushPin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No announcements right now",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.customColors.textSecondary
                        )
                    }
                }
            }

            else -> {
                // Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth(),
                    pageSpacing = 8.dp
                ) { page ->
                    val item = announcements[page]
                    val accentColor = MaterialTheme.colorScheme.primary

                    Card(
                        onClick = { onView(item) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(CornerRadius.lg),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.customColors.dashboardBg
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = Elevation.none)
                    ) {
                        // Colored top accent bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(accentColor, accentColor.copy(alpha = 0.2f))
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 14.dp, vertical = Spacing.md)
                                .fillMaxWidth()
                        ) {
                            // Date row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = formatDateForCarousel(item.createdAt),
                                    color = MaterialTheme.customColors.textSecondary,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Title with pin icon
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.PushPin,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier
                                        .size(13.dp)
                                        .rotate(-20f)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = item.title,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Message
                            Text(
                                text = item.message,
                                color = MaterialTheme.customColors.textSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp,
                                minLines = 2,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 0.6.dp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Author row
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.authorName,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${page + 1} / ${announcements.size}",
                                    color = MaterialTheme.customColors.textSecondary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                //  Dot indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(announcements.size) { index ->
                        val isActive = index == pagerState.currentPage
                        val width by animateDpAsState(
                            targetValue = if (isActive) 20.dp else 5.dp,
                            label = "dot_width"
                        )
                        Box(
                            modifier = Modifier
                                .height(5.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(
                                    if (isActive) Primary else Color(0xFFD6C9FF)
                                )
                        )
                        if (index < announcements.lastIndex) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
    }
}