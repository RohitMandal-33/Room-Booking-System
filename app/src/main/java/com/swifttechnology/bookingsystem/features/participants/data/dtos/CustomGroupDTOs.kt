package com.swifttechnology.bookingsystem.features.participants.data.dtos

import com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO

data class CustomGroupRequestDTO(
    val groupName: String,
    val description: String?,
    val member: List<Long>
)

data class CustomGroupResponseDTO(
    val id: Long? = null,
    val groupName: String? = null,
    val description: String? = null,
    val member: List<Long>? = null // Often APIs return lists of IDs or embedded objects
)
