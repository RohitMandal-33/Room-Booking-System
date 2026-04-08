package com.swifttechnology.bookingsystem.features.participants.domain.model

data class CustomGroup(
    val id: Long,
    val name: String,
    val description: String,
    val memberCount: Int,
    val memberIds: List<Long> = emptyList()
)
