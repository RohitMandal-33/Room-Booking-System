package com.swifttechnology.bookingsystem.features.department.domain.repository

// Department domain repository contract.

import com.swifttechnology.bookingsystem.features.department.domain.model.Department

interface DepartmentRepository {
    suspend fun addDepartment(departmentName: String, description: String?): Result<Unit>
    suspend fun getDepartments(): Result<List<Department>>
}
