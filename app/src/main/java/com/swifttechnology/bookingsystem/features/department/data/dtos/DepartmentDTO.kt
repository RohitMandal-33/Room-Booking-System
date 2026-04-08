package com.swifttechnology.bookingsystem.features.department.data.dtos

data class DepartmentDTO(
    val id: Long,
    val departmentName: String,
    val description: String? = null
)
