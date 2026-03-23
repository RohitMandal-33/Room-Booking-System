package com.swifttechnology.bookingsystem.features.department.data.repository

import com.swifttechnology.bookingsystem.features.department.data.api.DepartmentApiService
import com.swifttechnology.bookingsystem.features.department.data.dtos.AddDepartmentRequestDTO
import com.swifttechnology.bookingsystem.features.department.domain.repository.DepartmentRepository
import javax.inject.Inject

class DepartmentRepositoryImpl @Inject constructor(
    private val api: DepartmentApiService
) : DepartmentRepository {

    override suspend fun addDepartment(departmentName: String, description: String?): Result<Unit> = runCatching {
        val response = api.addDepartment(
            AddDepartmentRequestDTO(departmentName = departmentName, description = description)
        )
        if (!response.success) throw Exception(response.message)
    }
}
