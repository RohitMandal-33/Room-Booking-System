package com.swifttechnology.bookingsystem.app.navigation

sealed class AppRoute(val route: String) {
    data object Login : AppRoute("login")
    data object Dashboard : AppRoute("dashboard")
    data object RoomBooking : AppRoute("room_booking")
}
