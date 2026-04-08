package com.swifttechnology.bookingsystem.features.participants.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant

@Composable
fun ParticipantCard(
    participant: Participant,
    isEditable: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .pointerInput(isEditable) {
                detectTapGestures(
                    onTap = {
                        if (isEditable) {
                            onSelectionChange(!isSelected)
                        }
                    },
                    onLongPress = {
                        if (!isEditable) {
                            onLongPress()
                        }
                    }
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = isEditable,
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
                onCheckedChange = { onSelectionChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF007AFF)
                ),
                modifier = Modifier.padding(end = 12.dp)
            )
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(20.dp),
            color  = Color.White,
            border = BorderStroke(1.dp, Color(0xFF3C3C43).copy(alpha = 0.15f))
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
            //    Top row: name block + role badge               
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text       = participant.name,
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 16.sp,
                        color      = Color(0xFF1C1C1E)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = participant.position,
                        fontSize = 13.sp,
                        color    = Color(0xFF3C3C43).copy(alpha = 0.55f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text     = participant.department,
                        fontSize = 13.sp,
                        color    = Color(0xFF3C3C43).copy(alpha = 0.55f)
                    )
                }
                if (participant.status.equals("active", ignoreCase = true)) {
                    RoleBadge(role = participant.role)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            //    Divider                                        
            HorizontalDivider(
                color     = Color(0xFF3C3C43).copy(alpha = 0.08f),
                thickness = 0.5.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            //    Bottom row: contact info + status pill         
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    ContactRow(icon = Icons.Outlined.Email, text = participant.email)
                    Spacer(modifier = Modifier.height(5.dp))
                    ContactRow(icon = Icons.Outlined.Phone, text = participant.phone)
                }
            }
        }
    }
}
}

// Status styling data class
data class StatusStyle(
    val label: String,
    val textColor: Color,
    val bgColor: Color,
    val borderColor: Color,
    val dotColor: Color
)

fun resolveStatusStyle(status: String): StatusStyle = when (status.lowercase()) {
    "active" -> StatusStyle(
        label       = "Active",
        textColor   = Color(0xFF1A7F4B),
        bgColor     = Color(0xFFEAF7F0),
        borderColor = Color(0xFFA3D9BC),
        dotColor    = Color(0xFF34C759)
    )
    "away" -> StatusStyle(
        label       = "Away",
        textColor   = Color(0xFF7A5000),
        bgColor     = Color(0xFFFFF8E1),
        borderColor = Color(0xFFFFD580),
        dotColor    = Color(0xFFFF9F0A)
    )
    "busy" -> StatusStyle(
        label       = "Busy",
        textColor   = Color(0xFF8B1A1A),
        bgColor     = Color(0xFFFFF0F0),
        borderColor = Color(0xFFF5BABA),
        dotColor    = Color(0xFFFF6B6B)
    )
    else -> StatusStyle(
        label       = "Inactive",
        textColor   = Color(0xFFB02020),
        bgColor     = Color(0xFFFFF0F0),
        borderColor = Color(0xFFF5BABA),
        dotColor    = Color(0xFFFF3B30)
    )
}

@Composable
fun StatusPill(
    status: String,
    modifier: Modifier = Modifier
) {
    val style = resolveStatusStyle(status)

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(style.bgColor)
            .border(
                width  = 0.5.dp,
                color  = style.borderColor,
                shape  = RoundedCornerShape(20.dp)
            )
            .padding(start = 7.dp, end = 10.dp, top = 3.dp, bottom = 3.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(style.dotColor)
        )
        Text(
            text       = style.label,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium,
            color      = style.textColor
        )
    }
}

@Composable
fun RoleBadge(role: String) {
    val uppercaseRole = role.uppercase()

    val (textColor, bgColor, borderColor) = when (uppercaseRole) {
        "ADMIN"   -> Triple(
            Color(0xFF185FA5),
            Color(0xFFEEF4FF),
            Color(0xFFB5D4F4)
        )
        "MANAGER" -> Triple(
            Color(0xFF6A3BAF),
            Color(0xFFF5EEFF),
            Color(0xFFD9BEFF)
        )
        "STAFF"   -> Triple(
            Color(0xFF3B6D11),
            Color(0xFFEAF3DE),
            Color(0xFFC0DD97)
        )
        else      -> Triple(
            Color(0xFF3C3C43).copy(alpha = 0.7f),
            Color.Transparent,
            Color(0xFF3C3C43).copy(alpha = 0.2f)
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(
                width  = 0.5.dp,
                color  = borderColor,
                shape  = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text       = uppercaseRole,
            fontSize   = 11.sp,
            color      = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = Color(0xFF3C3C43).copy(alpha = 0.45f),
            modifier           = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text     = text,
            fontSize = 13.sp,
            color    = Color(0xFF3C3C43).copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
