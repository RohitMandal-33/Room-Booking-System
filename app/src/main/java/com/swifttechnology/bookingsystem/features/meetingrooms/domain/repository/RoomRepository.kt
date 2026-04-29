package com.swifttechnology.bookingsystem.features.meetingrooms.domain.repository

import com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.RoomPageDTO
import com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.RoomResponseDTO
import com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.RoomResourceDTO

/**
 * Meeting room domain repository contract.
 */
interface RoomRepository {
    suspend fun addRoom(roomName: String, capacity: Int, resourceIds: List<Long>?): Result<Unit>
    suspend fun listAllRooms(pageNo: Int = 0, pageSize: Int = 10): Result<RoomPageDTO>
    suspend fun listActiveRooms(pageNo: Int = 0, pageSize: Int = 10, roomName: String? = null): Result<RoomPageDTO>
    suspend fun updateRoom(id: Long, roomName: String, capacity: Int, resourceIds: List<Long>?): Result<Unit>
    suspend fun changeRoomStatus(id: Long, status: String): Result<Unit>
    suspend fun getAllResources(): Result<List<RoomResourceDTO>>
    suspend fun addResource(name: String): Result<Unit>
}
