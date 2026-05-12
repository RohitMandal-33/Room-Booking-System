package com.swifttechnology.bookingsystem.features.department.data.dtos

// Request body for POST /api/v1/department.

data class AddDepartmentRequestDTO(
    val departmentName: String,
    val description: String? = null
)
