package com.swifttechnology.bookingsystem.features.dashboard.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swifttechnology.bookingsystem.core.designsystem.MeetingRoomBookingTheme


private val BackgroundGray = Color(0xFFF3F4F8)
private val CardWhite = Color(0xFFFFFFFF)
private val PrimaryPurple = Color(0xFF5C4EFA)
private val PrimaryPurpleLight = Color(0xFFEEECFF)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val GreenAccent = Color(0xFF22C55E)
private val OrangeAccent = Color(0xFFF97316)
private val BlueAccent = Color(0xFF3B82F6)
private val PurpleAccent = Color(0xFF8B5CF6)
private val ChartBlue = Color(0xFF93C5FD)
private val ChartOrange = Color(0xFFFBD5A8)
private val ChartPurple = Color(0xFFD8B4FE)
private val DividerColor = Color(0xFFE5E7EB)
private val InsightBg = Color(0xFFF8F7FF)
private val AnnouncementRed = Color(0xFFEF4444)
private val ButtonWhite = Color.White



private data class StatTile(
    val icon: @Composable () -> Unit,
    val label: String,
    val value: String,
    val subtitle: String,
    val subtitleColor: Color = GreenAccent
)

private data class DayItem(val num: String, val day: String)

private data class MeetingCard(
    val title: String,
    val type: String,
    val typeColor: Color,
    val typeBg: Color,
    val time: String,
    val room: String,
    val by: String,
    val participants: Int
)



private val statTiles = listOf(
    StatTile(
        icon = { Icon(Icons.Outlined.MeetingRoom, null, tint = PrimaryPurple, modifier = Modifier.size(20.dp)) },
        label = "Total Rooms",
        value = "12",
        subtitle = "↑ 8 available now"
    ),
    StatTile(
        icon = { Icon(Icons.Outlined.CalendarToday, null, tint = OrangeAccent, modifier = Modifier.size(20.dp)) },
        label = "Today's Meetings",
        value = "24",
        subtitle = "4 in progress",
        subtitleColor = OrangeAccent
    ),
    StatTile(
        icon = { Icon(Icons.Outlined.BarChart, null, tint = BlueAccent, modifier = Modifier.size(20.dp)) },
        label = "Utilization Rate",
        value = "76%",
        subtitle = "↑ 5% from last week"
    ),
    StatTile(
        icon = { Icon(Icons.Outlined.Group, null, tint = PurpleAccent, modifier = Modifier.size(20.dp)) },
        label = "Active Users",
        value = "156",
        subtitle = "Across all departments",
        subtitleColor = TextSecondary
    )
)

private val calendarDays = listOf(
    DayItem("09", "Mon"), DayItem("10", "Tue"), DayItem("10", "Tue"),
    DayItem("11", "Wed"), DayItem("12", "Thu"), DayItem("13", "Fri"),
    DayItem("14", "Sat"), DayItem("15", "Sun")
)

private val rooms = listOf(
    "Conference Room 1A", "Conference Room 2A", "Conference Room 3A",
    "Board Room 5A", "Meeting Room 1B", "Executive Suite"
)

private val meetingCards = listOf(
    MeetingCard(
        "Finance Committee",
        "Executive",
        Color(0xFF7C3AED),
        Color(0xFFF3EEFF),
        "Tomorrow, 2:00 PM - 3:00 PM",
        "Board Room 5A",
        "Lorem Ipsum",
        10
    ),
    MeetingCard(
        "Product Sync",
        "Internal",
        BlueAccent,
        Color(0xFFEFF6FF),
        "Tomorrow, 4:00 PM - 5:00 PM",
        "Conference Room 1A",
        "Robert Smith",
        6
    ),
    MeetingCard(
        "Client Review",
        "Client",
        OrangeAccent,
        Color(0xFFFFF7ED),
        "Friday, 10:00 AM - 11:00 AM",
        "Board Room 5A",
        "Jane Doe",
        8
    )
)

private val statRows = listOf(
    Triple("Internal Meetings", "14", BlueAccent),
    Triple("Client Meetings", "8", OrangeAccent),
    Triple("Executive Meetings", "5", PurpleAccent),
    Triple("Peak Hour", "10:00", GreenAccent)
)

private val insights = listOf(
    Pair(BlueAccent, "14 Internal meetings"),
    Pair(OrangeAccent, "8.4 Client meetings"),
    Pair(PurpleAccent, "5.69 Executive meetings"),
    Pair(TextSecondary, "Peak activity at 10:00 with 2.9 total bookings"),
    Pair(TextSecondary, "Average of 1.7 bookings per hour")
)


private val chartDataInternal = listOf(0.4f, 0.5f, 0.7f, 1.0f, 0.8f, 0.9f, 0.6f, 0.5f, 0.3f)
private val chartDataClient = listOf(0.2f, 0.3f, 0.5f, 0.75f, 0.6f, 0.7f, 0.5f, 0.35f, 0.2f)
private val chartDataExec = listOf(0.1f, 0.2f, 0.35f, 0.5f, 0.45f, 0.55f, 0.4f, 0.3f, 0.15f)
private val chartLabels = listOf("7:00", "8:00", "9:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00")



@Composable
fun RoomBookingDashboardContent(onNavigate: (String) -> Unit) {
    var selectedRoom by remember { mutableStateOf(rooms.first()) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedPeriod by remember { mutableStateOf("Days") }
    var selectedDay by remember { mutableIntStateOf(3) } // Wed = index 3
    var dateSearchQuery by remember { mutableStateOf("") }

    // Anchor bounds for the dropdown so we don't rely on fixed top/start padding.
    var roomDropdownAnchorPos by remember { mutableStateOf<IntOffset?>(null) }
    var roomDropdownAnchorSize by remember { mutableStateOf<IntSize?>(null) }
    var rootWindowPos by remember { mutableStateOf<IntOffset?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInWindow()
                rootWindowPos = IntOffset(pos.x.toInt(), pos.y.toInt())
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            StatTilesSection()

            Spacer(modifier = Modifier.height(8.dp))


            AnnouncementSection(onView = { onNavigate(com.swifttechnology.bookingsystem.navigation.ScreenRoutes.ANNOUNCEMENTS) })

            Spacer(modifier = Modifier.height(8.dp))


            RoomBookingHeader(
                selectedRoom = selectedRoom,
                dropdownExpanded = dropdownExpanded,
                onDropdownToggle = { dropdownExpanded = !dropdownExpanded },
                onDropdownAnchorPositioned = { pos, size ->
                    roomDropdownAnchorPos = pos
                    roomDropdownAnchorSize = size
                }
            )

            Spacer(modifier = Modifier.height(8.dp))


            PeriodAndCalendarSection(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = { selectedPeriod = it },
                selectedDay = selectedDay,
                onDaySelected = { selectedDay = it },
                onPrevDay = {
                    selectedDay = kotlin.math.max(0, selectedDay - 1)
                },
                onNextDay = {
                    selectedDay = kotlin.math.min(calendarDays.lastIndex, selectedDay + 1)
                },
                dateSearchQuery = dateSearchQuery,
                onDateSearchQueryChanged = { dateSearchQuery = it }
            )

            Spacer(modifier = Modifier.height(8.dp))


            AreaChartCard(roomLabel = selectedRoom)

            Spacer(modifier = Modifier.height(8.dp))


            StatsRowsCard()

            Spacer(modifier = Modifier.height(8.dp))


            InsightsCard()

            Spacer(modifier = Modifier.height(8.dp))


            TotalBookingsRow(room = selectedRoom)

            Spacer(modifier = Modifier.height(8.dp))


            MeetingCardsSection()

            Spacer(modifier = Modifier.height(24.dp))
        }


        if (dropdownExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { dropdownExpanded = false }
            ) {
                val anchorPos = roomDropdownAnchorPos
                val anchorSize = roomDropdownAnchorSize
                val rootPos = rootWindowPos
                if (anchorPos != null && anchorSize != null && rootPos != null) {
                    val localX = anchorPos.x - rootPos.x
                    val localY = anchorPos.y - rootPos.y + anchorSize.height
                    RoomDropdownOverlay(
                        rooms = rooms,
                        selectedRoom = selectedRoom,
                        onRoomSelected = {
                            selectedRoom = it
                            dropdownExpanded = false
                        },
                        modifier = Modifier.offset { IntOffset(localX, localY) }
                    )
                }
            }
        }
    }
}



@Composable
private fun StatTilesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            statTiles.forEach { tile ->
                StatTileCard(tile)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        ViewAllButton(onClick = {})
    }
}

@Composable
private fun StatTileCard(tile: StatTile) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundGray),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                tile.icon()
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    tile.label,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(tile.value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                tile.subtitle,
                fontSize = 11.sp,
                color = tile.subtitleColor,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



@Composable
private fun AnnouncementSection(onView: () -> Unit) {
    data class AnnouncementCard(
        val message: String,
        val author: String,
        val date: String,
        val icon: androidx.compose.ui.graphics.vector.ImageVector
    )

    val announcements = listOf(
        AnnouncementCard(
            message = "Extremely Important Discussion that rivals the lord and all, my goodness very important and bla bla bla i gge ni... ",
            author = "Oshin Joshi",
            date = "Mar 18",
            icon = Icons.Outlined.Error
        ),
        AnnouncementCard(
            message = "System maintenance scheduled for this weekend. Please plan accordingly.",
            author = "Oshin Joshi",
            date = "Mar 19",
            icon = Icons.Outlined.NotificationsNone
        ),
        AnnouncementCard(
            message = "New room booking policy effective next Monday.",
            author = "Oshin Joshi",
            date = "Mar 20",
            icon = Icons.Outlined.PushPin
        ),
    )
    var currentPage by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BackgroundGray),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(AnnouncementRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = announcements[currentPage].icon,
                            contentDescription = null,
                            tint = ButtonWhite,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = announcements[currentPage].message,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.92f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = announcements[currentPage].author,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = announcements[currentPage].date,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = ButtonWhite)
                ) {
                    Text(
                        text = "View",
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Dots for multiple announcement cards (shown below the card)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(announcements.size) { i ->
                Box(
                    modifier = Modifier
                        .size(if (i == currentPage) 7.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (i == currentPage) PrimaryPurple else Color(0xFFD6C9FF))
                        .clickable { currentPage = i }
                )
                if (i < announcements.size - 1) Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}



@Composable
private fun RoomBookingHeader(
    selectedRoom: String,
    dropdownExpanded: Boolean,
    onDropdownToggle: () -> Unit,
    onDropdownAnchorPositioned: (IntOffset, IntSize) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.BarChart, null, tint = TextPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Room Booking Statistics",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Room :", fontSize = 14.sp, color = TextSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onDropdownToggle() }
                    .onGloballyPositioned { coordinates ->
                        val pos = coordinates.positionInWindow()
                        onDropdownAnchorPositioned(
                            IntOffset(pos.x.toInt(), pos.y.toInt()),
                            coordinates.size
                        )
                    },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = CardWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        selectedRoom,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        if (dropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
@Composable
private fun RoomDropdownOverlay(
    rooms: List<String>,
    selectedRoom: String,
    onRoomSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(260.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            rooms.forEach { room ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRoomSelected(room) }
                        .background(if (room == selectedRoom) PrimaryPurpleLight else Color.Transparent)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(room, fontSize = 13.sp, color = TextPrimary)
                }
            }
        }
    }
}



@Composable
private fun PeriodAndCalendarSection(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit,
    onPrevDay: () -> Unit,
    onNextDay: () -> Unit,
    dateSearchQuery: String,
    onDateSearchQueryChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(BackgroundGray)
                .padding(4.dp)
        ) {
            listOf("Days", "Weeks", "Months").forEach { period ->
                val isSelected = period == selectedPeriod
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) PrimaryPurple else Color.Transparent)
                        .clickable { onPeriodSelected(period) }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        period,
                        fontSize = 13.sp,
                        color = if (isSelected) Color.White else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = dateSearchQuery,
            onValueChange = onDateSearchQueryChanged,
            placeholder = { Text("Search dates...", fontSize = 13.sp, color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(18.dp)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = BackgroundGray,
                focusedContainerColor = BackgroundGray,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryPurple
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = onPrevDay, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ChevronLeft, null, tint = TextSecondary)
            }
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                calendarDays.forEachIndexed { index, day ->
                    val isSelected = index == selectedDay
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) PrimaryPurple else Color.Transparent)
                            .border(
                                width = if (!isSelected) 1.dp else 0.dp,
                                color = if (!isSelected) DividerColor else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onDaySelected(index) }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            day.num,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else TextPrimary
                        )
                        Text(
                            day.day,
                            fontSize = 11.sp,
                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextSecondary
                        )
                    }
                }
            }
            IconButton(onClick = onNextDay, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ChevronRight, null, tint = TextSecondary)
            }
        }
    }
}



@Composable
private fun AreaChartCard(roomLabel: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Canvas(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                drawAreaChart(data = chartDataExec, color = ChartPurple, width = size.width, height = size.height)
                drawAreaChart(data = chartDataClient, color = ChartOrange, width = size.width, height = size.height)
                drawAreaChart(data = chartDataInternal, color = ChartBlue, width = size.width, height = size.height)

                // Grid lines
                val gridCount = 4
                for (i in 0..gridCount) {
                    val y = size.height * i / gridCount
                    drawLine(
                        color = Color(0xFFE5E7EB),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1f
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                chartLabels.forEach { label ->
                    Text(label, fontSize = 10.sp, color = TextSecondary, textAlign = TextAlign.Center)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                roomLabel,
                fontSize = 12.sp,
                color = PrimaryPurple,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawAreaChart(
    data: List<Float>,
    color: Color,
    width: Float,
    height: Float
) {
    if (data.isEmpty()) return
    if (data.size == 1) {
        val y = height - data[0] * height
        drawLine(
            color = color,
            start = Offset(0f, y),
            end = Offset(width, y),
            strokeWidth = 2.5f
        )
        return
    }

    val stepX = width / (data.size - 1)
    val path = Path()
    val linePath = Path()

    path.moveTo(0f, height)
    for (i in data.indices) {
        val x = i * stepX
        val y = height - data[i] * height
        if (i == 0) {
            path.lineTo(x, y)
            linePath.moveTo(x, y)
        } else {
            path.lineTo(x, y)
            linePath.lineTo(x, y)
        }
    }
    path.lineTo(width, height)
    path.close()

    drawPath(path, color.copy(alpha = 0.4f))
    drawPath(linePath, color, style = Stroke(width = 2.5f))
}



@Composable
private fun StatsRowsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column {
            statRows.forEachIndexed { index, (label, value, color) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color.copy(alpha = 0.07f))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, fontSize = 14.sp, color = color, fontWeight = FontWeight.Medium)
                    Text(value, fontSize = 22.sp, color = color, fontWeight = FontWeight.Bold)
                }
                if (index < statRows.size - 1) HorizontalDivider(color = DividerColor, thickness = 0.5.dp)
            }
        }
    }
}



@Composable
private fun InsightsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = InsightBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Lightbulb, null, tint = PrimaryPurple, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Insights", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Spacer(modifier = Modifier.height(10.dp))
            insights.forEach { (color, text) ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text, fontSize = 13.sp, color = TextPrimary, lineHeight = 18.sp)
                }
            }
        }
    }
}



@Composable
private fun TotalBookingsRow(room: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardWhite)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total Bookings", fontSize = 14.sp, color = TextSecondary)
            Text("27.6", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        }
        HorizontalDivider(color = DividerColor)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
        ) {
            Text("Book $room", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}



@Composable
private fun MeetingCardsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(meetingCards) { meeting ->
                    MeetingCardItem(meeting)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            ViewAllButton(onClick = {}, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
private fun MeetingCardItem(card: MeetingCard) {
    Card(
        modifier = Modifier.width(260.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    card.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(card.typeBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(card.type, fontSize = 11.sp, color = card.typeColor, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(card.time, fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Room: ${card.room}", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text("By: ${card.by}", fontSize = 12.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.People, null, tint = TextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${card.participants} participants", fontSize = 11.sp, color = TextSecondary)
                }
                OutlinedButton(
                    onClick = {},
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryPurple),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("View Details", fontSize = 11.sp, color = PrimaryPurple, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}



@Composable
private fun ViewAllButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DividerColor),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(containerColor = Color.Transparent)
    ) {
        Text("View All", fontSize = 14.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
    }
}



@Preview(showBackground = true, widthDp = 393)
@Composable
private fun RoomBookingDashboardPreview() {
    MeetingRoomBookingTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            RoomBookingDashboardContent(onNavigate = {})
        }
    }
}
