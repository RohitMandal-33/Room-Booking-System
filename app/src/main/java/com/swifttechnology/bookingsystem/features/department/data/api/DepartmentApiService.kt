package com.swifttechnology.bookingsystem.features.department.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.department.data.dtos.AddDepartmentRequestDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface DepartmentApiService {

    @POST(APIEndpoint.DEPARTMENT_ADD)
    suspend fun addDepartment(@Body request: AddDepartmentRequestDTO): GlobalResponse<Unit>
}
