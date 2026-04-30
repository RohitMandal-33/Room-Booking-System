package com.swifttechnology.bookingsystem.features.department.domain.model

data class Department(
    val id: Long,
    val departmentName: String,
    val description: String? = null
)
