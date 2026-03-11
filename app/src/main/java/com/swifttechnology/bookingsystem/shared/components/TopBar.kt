package com.swifttechnology.bookingsystem.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.R
import com.swifttechnology.bookingsystem.core.designsystem.customColors

@Composable
fun TopBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onMenuClick: () -> Unit,
    searchPlaceholder: String = "Search people, participant",
    onTrailingClick: (() -> Unit)? = null,
    trailingIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.customColors.neutral100)
                .border(1.dp, MaterialTheme.customColors.neutral200, RoundedCornerShape(12.dp))
                .clickable { onMenuClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.dock_to_right_24px),
                contentDescription = "Open navigation",
                modifier = Modifier.size(24.dp).padding(3.dp),
                tint = MaterialTheme.customColors.deepBlack
            )
        }

        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier
                .weight(1f)
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.customColors.neutral100)
                .border(1.dp, MaterialTheme.customColors.neutral200, RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.customColors.deepBlack),
            textStyle = LocalTextStyle.current.copy(
                fontSize = 13.sp,
                color = MaterialTheme.customColors.deepBlack
            ),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = searchPlaceholder,
                                fontSize = 13.sp,
                                color = MaterialTheme.customColors.neutral700,
                                maxLines = 1
                            )
                        }
                        innerTextField()
                    }
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = "Search",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.customColors.neutral700
                    )
                }
            }
        )

        if (onTrailingClick != null && trailingIcon != null) {
            IconButton(onClick = onTrailingClick) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "Action",
                    tint = MaterialTheme.customColors.deepBlack,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.customColors.neutral100)
                    .border(1.dp, MaterialTheme.customColors.neutral200, RoundedCornerShape(12.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit_square_24px),
                    contentDescription = "Edit",
                    modifier = Modifier.size(24.dp).padding(3.dp),
                    tint = MaterialTheme.customColors.deepBlack
                )
            }
        }
    }
}

