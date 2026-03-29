package com.swifttechnology.bookingsystem.features.participants.domain.model

data class Participant(
    val id: Long,
    val name: String,
    val role: String,
    val email: String,
    val phone: String,
    val meetingCount: Int,
    val department: String
)
