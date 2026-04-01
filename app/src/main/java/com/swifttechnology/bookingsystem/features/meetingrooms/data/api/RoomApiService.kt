package com.swifttechnology.bookingsystem.features.meetingrooms.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.*
import retrofit2.http.*

interface RoomApiService {

    @POST(APIEndpoint.ROOM_ADD)
    suspend fun addRoom(@Body request: RoomRequestDTO): GlobalResponse<Unit>

    @POST(APIEndpoint.ROOM_LIST)
    suspend fun listAllRooms(@Body request: PaginatedDataRequestDTO): GlobalResponse<RoomPageDTO>

    @POST(APIEndpoint.ROOM_ACTIVE_LIST)
    suspend fun listActiveRooms(@Body request: RoomDataRequestDTO): GlobalResponse<RoomPageDTO>

    @PUT(APIEndpoint.ROOM_UPDATE)
    suspend fun updateRoom(
        @Path("id") id: Long,
        @Body request: RoomRequestDTO
    ): GlobalResponse<Unit>

    @PATCH(APIEndpoint.ROOM_CHANGE_STATUS)
    suspend fun changeRoomStatus(
        @Path("id") id: Long,
        @Body request: StatusChangeRequestDTO
    ): GlobalResponse<Unit>
}
