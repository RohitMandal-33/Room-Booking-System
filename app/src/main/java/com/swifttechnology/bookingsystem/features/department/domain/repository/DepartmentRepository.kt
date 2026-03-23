package com.swifttechnology.bookingsystem.features.department.domain.repository

/**
 * Department domain repository contract.
 */
interface DepartmentRepository {
    suspend fun addDepartment(departmentName: String, description: String?): Result<Unit>
}
