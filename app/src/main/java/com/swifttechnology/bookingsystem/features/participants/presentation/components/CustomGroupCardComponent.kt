package com.swifttechnology.bookingsystem.features.participants.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant

fun memberDisplayNamesForGroup(
    group: CustomGroup,
    participants: List<Participant>,
    resolvedUserNames: Map<Long, String>
): List<String> =
    group.memberIds.map { id ->
        participants.find { it.id == id }?.name
            ?: resolvedUserNames[id]
            ?: "User #$id"
    }

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CustomGroupCard(
    group: CustomGroup,
    isExpanded: Boolean,
    memberDisplayNames: List<String>,
    onMembersRowClick: () -> Unit,
    isEditable: Boolean = false,
    isSelected: Boolean = false,
    onSelectionChange: (Boolean) -> Unit = {},
    onLongPress: () -> Unit = {}
) {
    val chevronRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(250),
        label = "chevron"
    )

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
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFF3C3C43).copy(alpha = 0.15f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = group.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1C1C1E)
                )
                if (group.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = group.description,
                        fontSize = 14.sp,
                        color = Color(0xFF3C3C43).copy(alpha = 0.65f),
                        fontStyle = FontStyle.Italic
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onMembersRowClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = Color(0xFF3C3C43).copy(alpha = 0.55f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Members : ${group.memberCount}",
                        fontSize = 14.sp,
                        color = Color(0xFF3C3C43).copy(alpha = 0.75f),
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Outlined.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = Color(0xFF3C3C43).copy(alpha = 0.55f),
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(chevronRotation)
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            memberDisplayNames.forEach { name ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFF2F2F7),
                                    border = BorderStroke(
                                        0.5.dp,
                                        Color(0xFF3C3C43).copy(alpha = 0.12f)
                                    )
                                ) {
                                    Text(
                                        text = name,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        fontSize = 13.sp,
                                        color = Color(0xFF1C1C1E),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
