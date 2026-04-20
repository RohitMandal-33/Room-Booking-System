package com.swifttechnology.bookingsystem.features.announcements.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Formats an ISO date string to "Feb 7, 2026" */
private fun formatDate(raw: String): String {
    return runCatching {
        val odt = OffsetDateTime.parse(raw)
        odt.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
    }.getOrDefault(raw)
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    isEditMode: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    showAccentBar: Boolean = true,         // true on Pinned tab, false on All tab
    onTap: () -> Unit,
    onLongPress: () -> Unit
) {
    val colors = MaterialTheme.customColors

    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Primary.copy(alpha = 0.07f) else colors.neutral100,
        animationSpec = tween(200),
        label = "card_bg"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onTap,
                onLongClick = onLongPress
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isEditMode,
            enter = fadeIn(animationSpec = tween(250)) + expandHorizontally(
                expandFrom = Alignment.Start,
                animationSpec = tween(250)
            ) + slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = tween(250)
            ),
            exit = fadeOut(animationSpec = tween(250)) + shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = tween(250)
            ) + slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(250)
            )
        ) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onTap() },
                colors = CheckboxDefaults.colors(
                    checkedColor = Primary
                ),
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.lg))
                .background(bgColor)
                .border(
                    width = 1.dp,
                    color = if (isSelected) Primary.copy(alpha = 0.4f) else colors.neutral200,
                    shape = RoundedCornerShape(CornerRadius.lg)
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left accent bar
                if (showAccentBar) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(80.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = CornerRadius.lg,
                                    bottomStart = CornerRadius.lg
                                )
                            )
                            .background(Primary)
                    )
                }

                // Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(
                            start = if (!showAccentBar) Spacing.md else Spacing.ms,
                            end = Spacing.md,
                            top = Spacing.ms,
                            bottom = Spacing.ms
                        )
                ) {
                    // Author + date row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = buildString {
                                append(announcement.authorName)
                                if (announcement.authorPosition.isNotBlank()) {
                                    append(" (${announcement.authorPosition})")
                                }
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(Spacing.sm))
                        Text(
                            text = formatDate(announcement.createdAt),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    // Title + pin icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (announcement.pinned) {
                            Icon(
                                imageVector = Icons.Outlined.PushPin,
                                contentDescription = "Pinned",
                                tint = Primary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .padding(end = 2.dp)
                            )
                        }
                        Text(
                            text = announcement.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = colors.deepBlack,
                                fontSize = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(Modifier.height(2.dp))

                    // Message preview
                    Text(
                        text = announcement.message,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = colors.textSecondary,
                            fontSize = 12.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
