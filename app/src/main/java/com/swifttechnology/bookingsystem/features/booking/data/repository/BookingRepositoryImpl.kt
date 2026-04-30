package com.swifttechnology.bookingsystem.features.booking.data.repository

import com.swifttechnology.bookingsystem.features.booking.data.api.BookingApiService
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApiService
) : BookingRepository {

    override suspend fun bookRoom(request: RoomBookingRequestDTO): Result<BookingResponseDTO> = runCatching {
        val response = api.bookRoom(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun updateBooking(
        bookingId: Long,
        request: RoomBookingRequestDTO
    ): Result<BookingResponseDTO> = runCatching {
        val response = api.updateBooking(bookingId, request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun updateRecurrenceBooking(
        recurrenceId: String,
        request: RoomBookingRequestDTO
    ): Result<BookingResponseDTO> = runCatching {
        val response = api.updateRecurrenceBooking(recurrenceId, request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getMyBookings(): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getMyBookings()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getAllBookings(): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getAllBookings()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getUpcomingMeetings(): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getUpcomingMeetings()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getRoomMeetings(roomId: Long): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getRoomMeetings(roomId)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getMeetingTypes(): Result<List<MeetingTypeDTO>> = runCatching {
        val response = api.getAllMeetingTypes()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun createMeetingType(request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO): Result<Unit> = runCatching {
        val response = api.createMeetingType(request)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun updateMeetingType(
        meetingTypeId: Long,
        request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO
    ): Result<Unit> = runCatching {
        val response = api.updateMeetingType(meetingTypeId, request)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun changeMeetingTypeStatus(meetingTypeId: Long, status: String): Result<Unit> = runCatching {
        val response = api.changeMeetingTypeStatus(
            meetingTypeId,
            com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO(status = status)
        )
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun getCalendarMonth(date: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO(date = date)
        val response = api.getCalendarMonth(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getCalendarWeek(date: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO(date = date)
        val response = api.getCalendarWeek(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getCalendarDay(date: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO(date = date)
        val response = api.getCalendarDay(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getBookingDetails(bookingId: Long): Result<BookingResponseDTO> = runCatching {
        val response = api.getBookedRoomById(bookingId)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }
}
