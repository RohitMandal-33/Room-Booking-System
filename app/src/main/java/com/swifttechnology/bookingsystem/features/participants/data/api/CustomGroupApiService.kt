package com.swifttechnology.bookingsystem.features.participants.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.participants.data.dtos.CustomGroupRequestDTO
import com.swifttechnology.bookingsystem.features.participants.data.dtos.CustomGroupResponseDTO
import retrofit2.http.*

interface CustomGroupApiService {

    @POST(APIEndpoint.GROUP_ADD)
    suspend fun addCustomGroup(@Body request: CustomGroupRequestDTO): GlobalResponse<CustomGroupResponseDTO>

    @GET(APIEndpoint.GROUP_LIST)
    suspend fun getAllCustomGroups(): GlobalResponse<List<CustomGroupResponseDTO>>

    @PUT(APIEndpoint.GROUP_UPDATE)
    suspend fun updateCustomGroup(
        @Path("id") id: Long,
        @Body request: CustomGroupRequestDTO
    ): GlobalResponse<CustomGroupResponseDTO>

    @DELETE(APIEndpoint.GROUP_DELETE)
    suspend fun deleteCustomGroup(@Path("id") id: Long): GlobalResponse<Unit>
}
