package com.swifttechnology.bookingsystem.navigation

object ScreenRoutes {
    const val LOGIN = "login"
    const val MAIN_APP = "main_app"

    const val DASHBOARD = "dashboard"
    const val CALENDAR = "calendar"
    const val BOOK_ROOM = "book_room"
    const val MEETING_ROOMS = "meeting_rooms"
    const val MEETING_ROOMS_EDITABLE_QUERY = "editable"
    const val MEETING_ROOM_EDIT = "meeting_room_edit"
    const val MEETING_ROOM_NAME = "roomName"
    const val ANNOUNCEMENTS = "announcements"
    const val REPORT = "report"
    const val PARTICIPANTS = "participants"
    const val SETTINGS = "settings"

    fun meetingRooms(editable: Boolean = false): String {
        return if (editable) "$MEETING_ROOMS?$MEETING_ROOMS_EDITABLE_QUERY=true" else MEETING_ROOMS
    }

    fun meetingRoomEdit(roomName: String): String {
        return "$MEETING_ROOM_EDIT/$roomName"
    }
}

