package com.swifttechnology.bookingsystem.features.department.data.repository

import com.swifttechnology.bookingsystem.features.department.data.api.DepartmentApiService
import com.swifttechnology.bookingsystem.features.department.data.dtos.AddDepartmentRequestDTO
import com.swifttechnology.bookingsystem.features.department.domain.model.Department
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

    override suspend fun getDepartments(): Result<List<Department>> = runCatching {
        val response = api.getDepartments()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data.map {
            Department(
                id = it.id,
                departmentName = it.departmentName,
                description = it.description
            )
        }
    }
}
