package com.swifttechnology.bookingsystem.features.announcements.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.customColors
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

// ─── Colour tokens ───────────────────────────────────────────────────────────
private val PinColor   = Color(0xFF7C3AED)   // purple  – "pin it"
private val UnpinColor = Color(0xFFDC2626)   // red     – "unpin"

// ─── Fraction of card width the swipe travels before triggering ─────────────
private const val MAX_DRAG_FRACTION    = 0.50f   // card slides at most 50 % of its width
private const val COMMIT_FRACTION      = 0.35f   // commit fires at 35 % (= 70 % of the 50 % max)

private fun formatDate(raw: String): String {
    return runCatching {
        val odt = OffsetDateTime.parse(raw)
        odt.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
    }.getOrDefault(raw)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnnouncementCard(
    announcement: Announcement,
    isEditMode: Boolean,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    showAccentBar: Boolean = true,
    onTap: () -> Unit,
    onLongPress: () -> Unit,
    onTogglePin: (() -> Unit)? = null          // null = feature disabled
) {
    val colors = MaterialTheme.customColors
    val scope  = rememberCoroutineScope()

    // ── Swipe state ──────────────────────────────────────────────────────────
    val offsetX    = remember { Animatable(0f) }
    var dragTotal  by remember { mutableFloatStateOf(0f) }

    // revealProgress: 0 → 1 as the card slides from 0 → half-width
    // (half-width is unknown at this scope, so we use a placeholder dp value
    //  that approximates half a typical card; the actual clamp is done in pointerInput)
    val revealProgress = (offsetX.value / 300f).coerceIn(0f, 1f)

    // ── Colours ──────────────────────────────────────────────────────────────
    val actionColor by animateColorAsState(
        targetValue = if (announcement.pinned) UnpinColor else PinColor,
        animationSpec = tween(250),
        label = "pin_color"
    )
    val bgColor by animateColorAsState(
        targetValue = when {
            isSelected -> Primary.copy(alpha = 0.15f)
            !announcement.isRead -> Primary.copy(alpha = 0.08f)
            else -> colors.neutral100
        },
        animationSpec = tween(200),
        label = "card_bg"
    )

    // ── Icon scale pops at threshold ─────────────────────────────────────────
    val iconScale by animateFloatAsState(
        targetValue = if (revealProgress >= 0.6f) 1.2f else 0.85f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "icon_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Animated checkbox in edit mode ───────────────────────────────────
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
                colors = CheckboxDefaults.colors(checkedColor = Primary),
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        // ── Swipe container ──────────────────────────────────────────────────
        Box(modifier = Modifier.weight(1f)) {

            // ── Reveal background (shown behind the card while swiping) ──────
            if (onTogglePin != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(CornerRadius.lg))
                        .background(actionColor.copy(alpha = (revealProgress * 1.5f).coerceIn(0f, 1f))),
                    contentAlignment = Alignment.CenterStart
                ) {
                    // Icon container
                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .size(40.dp)
                            .scale(iconScale)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.20f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector       = Icons.Outlined.PushPin,
                            contentDescription = if (announcement.pinned) "Unpin" else "Pin",
                            tint              = Color.White,
                            modifier          = Modifier.size(20.dp)
                        )
                    }

                    // Label
                    Text(
                        text      = if (announcement.pinned) "Unpin" else "Pin",
                        color     = Color.White,
                        fontSize  = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier  = Modifier.padding(start = 68.dp)
                    )
                }
            }

            // ── Card surface (slides right) ──────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .clip(RoundedCornerShape(CornerRadius.lg))
                    .background(bgColor)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Primary.copy(alpha = 0.4f) else colors.neutral200,
                        shape = RoundedCornerShape(CornerRadius.lg)
                    )
                    .combinedClickable(
                        onClick    = onTap,
                        onLongClick = onLongPress
                    )
                    .then(
                        if (onTogglePin != null && !isEditMode) {
                            Modifier.pointerInput(announcement.id) {
                                val halfWidth = { size.width * MAX_DRAG_FRACTION }
                                val commitAt  = { size.width * COMMIT_FRACTION }
                                detectHorizontalDragGestures(
                                    onDragStart = { dragTotal = 0f },
                                    onDragEnd = {
                                        scope.launch {
                                            if (offsetX.value >= commitAt()) {
                                                // Commit: bounce to half-width, fire, spring back
                                                offsetX.animateTo(
                                                    targetValue   = halfWidth(),
                                                    animationSpec = spring(
                                                        stiffness    = Spring.StiffnessMedium,
                                                        dampingRatio = Spring.DampingRatioNoBouncy
                                                    )
                                                )
                                                onTogglePin()
                                                kotlinx.coroutines.delay(120)
                                                offsetX.animateTo(
                                                    targetValue   = 0f,
                                                    animationSpec = spring(
                                                        stiffness    = Spring.StiffnessMediumLow,
                                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                                    )
                                                )
                                            } else {
                                                // Cancel: spring back with bounce
                                                offsetX.animateTo(
                                                    targetValue   = 0f,
                                                    animationSpec = spring(
                                                        stiffness    = Spring.StiffnessMedium,
                                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                                    )
                                                )
                                            }
                                            dragTotal = 0f
                                        }
                                    },
                                    onDragCancel = {
                                        dragTotal = 0f
                                        scope.launch {
                                            offsetX.animateTo(
                                                targetValue   = 0f,
                                                animationSpec = spring(
                                                    stiffness    = Spring.StiffnessMedium,
                                                    dampingRatio = Spring.DampingRatioMediumBouncy
                                                )
                                            )
                                        }
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        if (dragAmount > 0 || offsetX.value > 0f) {
                                            // Clamp to half card width
                                            val newOffset = (offsetX.value + dragAmount)
                                                .coerceIn(0f, halfWidth())
                                            dragTotal += dragAmount.coerceAtLeast(0f)
                                            scope.launch { offsetX.snapTo(newOffset) }
                                        }
                                    }
                                )
                            }
                        } else Modifier
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
                                        topStart    = CornerRadius.lg,
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
                                start  = if (!showAccentBar) Spacing.md else Spacing.ms,
                                end    = 0.dp,
                                top    = Spacing.ms,
                                bottom = Spacing.ms
                            )
                    ) {
                        // Author
                        Text(
                            text = buildString {
                                append(announcement.authorName)
                                if (announcement.authorPosition.isNotBlank()) {
                                    append(" (${announcement.authorPosition})")
                                }
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color    = colors.textSecondary,
                                fontSize = 11.sp
                            ),
                            maxLines  = 1,
                            overflow  = TextOverflow.Ellipsis,
                            modifier  = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(2.dp))

                        // Title
                        Text(
                            text  = announcement.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color      = colors.deepBlack,
                                fontSize   = 14.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(Modifier.height(2.dp))

                        // Message preview
                        Text(
                            text  = announcement.message,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color    = colors.textSecondary,
                                fontSize = 12.sp
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Date and Pin column
                    Column(
                        modifier = Modifier
                            .padding(start = Spacing.sm, end = Spacing.md, top = Spacing.ms)
                            .align(Alignment.Top),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text  = formatDate(announcement.startDate ?: announcement.createdAt),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color    = colors.textSecondary,
                                fontSize = 11.sp
                            )
                        )

                        if (announcement.pinned) {
                            Spacer(Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Primary.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector       = Icons.Outlined.PushPin,
                                    contentDescription = "Pinned",
                                    tint              = Primary,
                                    modifier          = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}