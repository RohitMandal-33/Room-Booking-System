package com.swifttechnology.bookingsystem.features.department.domain.repository

// Department domain repository contract.

import com.swifttechnology.bookingsystem.features.department.domain.model.Department

interface DepartmentRepository {
    suspend fun addDepartment(departmentName: String, description: String?): Result<Unit>
    suspend fun updateDepartment(id: Long, departmentName: String, description: String?): Result<Unit>
    suspend fun changeDepartmentStatus(id: Long, status: String): Result<Unit>
    suspend fun getDepartments(): Result<List<Department>>
    suspend fun getActiveDepartments(): Result<List<Department>>
}
