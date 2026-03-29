package com.swifttechnology.bookingsystem.features.dashboard.presentation.DashboardComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.swifttechnology.bookingsystem.core.designsystem.CornerRadius
import com.swifttechnology.bookingsystem.core.designsystem.Elevation
import com.swifttechnology.bookingsystem.core.designsystem.Primary
import com.swifttechnology.bookingsystem.core.designsystem.Spacing
import com.swifttechnology.bookingsystem.core.designsystem.TextSecondary
import com.swifttechnology.bookingsystem.core.designsystem.customColors

// Local accent colors for stat tiles
private val GreenAccent  = Color(0xFF22C55E)
private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent   = Color(0xFF3B82F6)
private val PurpleAccent = Color(0xFF8B5CF6)

private data class StatTile(
    val icon: @Composable () -> Unit,
    val label: String,
    val value: String,
    val subtitle: String,
    val subtitleColor: Color = GreenAccent
)

@Composable
fun StatTilesSection() {
    val statTiles = listOf(
        StatTile(
            icon = { Icon(Icons.Outlined.MeetingRoom, null, tint = Primary, modifier = Modifier.size(Spacing.ml)) },
            label = "Total Rooms", value = "12", subtitle = "↑ 8 available now"
        ),
        StatTile(
            icon = { Icon(Icons.Outlined.CalendarToday, null, tint = OrangeAccent, modifier = Modifier.size(Spacing.ml)) },
            label = "Today's Meetings", value = "24", subtitle = "4 in progress",
            subtitleColor = OrangeAccent
        ),
        StatTile(
            icon = { Icon(Icons.Outlined.BarChart, null, tint = BlueAccent, modifier = Modifier.size(Spacing.ml)) },
            label = "Utilization Rate", value = "76%", subtitle = "↑ 5% from last week"
        ),
        StatTile(
            icon = { Icon(Icons.Outlined.Group, null, tint = PurpleAccent, modifier = Modifier.size(Spacing.ml)) },
            label = "Active Users", value = "156", subtitle = "Across all departments",
            subtitleColor = MaterialTheme.customColors.textBody
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(Spacing.ms)
        ) {
            statTiles.forEach { tile -> StatTileCard(tile) }
        }

        Spacer(modifier = Modifier.height(Spacing.ms))
        ViewAllButton(onClick = {})
    }
}

@Composable
private fun StatTileCard(tile: StatTile) {
    Card(
        modifier  = Modifier.width(160.dp),
        shape     = RoundedCornerShape(CornerRadius.lg),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.customColors.dashboardBg),
        elevation = CardDefaults.cardElevation(Elevation.none)
    ) {
        Column(modifier = Modifier.padding(Spacing.ms)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                tile.icon()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    tile.label,
                    style    = MaterialTheme.typography.labelMedium,
                    color    = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(Spacing.sm))
            Text(tile.value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(Spacing.xxs))
            Text(tile.subtitle, style = MaterialTheme.typography.labelSmall, color = tile.subtitleColor)
        }
    }
}
