package com.swifttechnology.bookingsystem.features.meetingrooms.data.repository

import com.swifttechnology.bookingsystem.features.meetingrooms.data.api.RoomApiService
import com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.*
import com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository.RoomRepository
import javax.inject.Inject

class RoomRepositoryImpl @Inject constructor(
    private val api: RoomApiService
) : RoomRepository {

    override suspend fun addRoom(
        roomName: String,
        capacity: Int,
        resources: List<String>?
    ): Result<Unit> = runCatching {
        val response = api.addRoom(
            RoomRequestDTO(roomName = roomName, capacity = capacity, resources = resources)
        )
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun listAllRooms(pageNo: Int, pageSize: Int): Result<RoomPageDTO> = runCatching {
        val response = api.listAllRooms(PaginatedDataRequestDTO(pageNo = pageNo, pageSize = pageSize))
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun listActiveRooms(pageNo: Int, pageSize: Int): Result<RoomPageDTO> = runCatching {
        val response = api.listActiveRooms(PaginatedDataRequestDTO(pageNo = pageNo, pageSize = pageSize))
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun updateRoom(
        id: Long,
        roomName: String,
        capacity: Int,
        resources: List<String>?
    ): Result<Unit> = runCatching {
        val response = api.updateRoom(
            id = id,
            request = RoomRequestDTO(roomName = roomName, capacity = capacity, resources = resources)
        )
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun changeRoomStatus(id: Long, status: String): Result<Unit> = runCatching {
        val response = api.changeRoomStatus(id = id, request = StatusChangeRequestDTO(status = status))
        if (!response.success) throw Exception(response.message)
    }
}
