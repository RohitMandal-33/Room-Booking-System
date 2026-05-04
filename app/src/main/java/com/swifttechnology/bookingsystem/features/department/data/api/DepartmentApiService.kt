package com.swifttechnology.bookingsystem.features.department.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.department.data.dtos.AddDepartmentRequestDTO
import com.swifttechnology.bookingsystem.features.department.data.dtos.DepartmentDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.PATCH

interface DepartmentApiService {

    @POST(APIEndpoint.DEPARTMENT_ADD)
    suspend fun addDepartment(@Body request: AddDepartmentRequestDTO): GlobalResponse<Unit>

    @PUT(APIEndpoint.DEPARTMENT_UPDATE)
    suspend fun updateDepartment(
        @retrofit2.http.Path("id") id: Long,
        @Body request: AddDepartmentRequestDTO
    ): GlobalResponse<Unit>

    @PATCH(APIEndpoint.DEPARTMENT_CHANGE_STATUS)
    suspend fun changeDepartmentStatus(
        @retrofit2.http.Path("id") id: Long,
        @Body request: com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO
    ): GlobalResponse<Unit>

    @GET(APIEndpoint.DEPARTMENT_LIST)
    suspend fun getDepartments(): GlobalResponse<List<DepartmentDTO>>

    @GET(APIEndpoint.DEPARTMENT_ACTIVE_LIST)
    suspend fun getActiveDepartments(): GlobalResponse<List<DepartmentDTO>>
}
