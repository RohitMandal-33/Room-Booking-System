package com.swifttechnology.bookingsystem.features.report.presentation

 
// Data Model
 
data class ActivityEntry(
    val meetingTitle: String,
    val date: String,
    val startTime: String,
    val EndTime: String,
    val roomName: String,
    val createdBy: String
)
