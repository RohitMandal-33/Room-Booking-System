package com.swifttechnology.bookingsystem.features.booking.data.repository

import com.swifttechnology.bookingsystem.core.utils.ErrorMapper
import com.swifttechnology.bookingsystem.features.booking.data.api.BookingApiService
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO
import com.swifttechnology.bookingsystem.features.booking.domain.repository.BookingRepository
import javax.inject.Inject

/** Maps any exception inside a [Result] to a user-friendly message via [ErrorMapper]. */
private fun <T> Result<T>.mapErrors(): Result<T> =
    mapCatching { it }.recoverCatching { throw Exception(ErrorMapper.map(it)) }

class BookingRepositoryImpl @Inject constructor(
    private val api: BookingApiService
) : BookingRepository {

    override suspend fun bookRoom(request: RoomBookingRequestDTO): Result<Unit> = runCatching {
        val response = api.bookRoom(request)
        if (!response.success) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
    }.mapErrors()

    override suspend fun updateBooking(
        bookingId: Long?,
        request: RoomBookingRequestDTO
    ): Result<Unit> = runCatching {
        val response = api.updateBooking(bookingId, request)
        if (!response.success) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
    }.mapErrors()

    override suspend fun updateRecurrenceBooking(
        recurrenceId: String,
        request: RoomBookingRequestDTO
    ): Result<Unit> = runCatching {
        val response = api.updateRecurrenceBooking(recurrenceId, request)
        if (!response.success) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
    }.mapErrors()

    override suspend fun getMyBookings(): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getMyBookings()
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getAllBookings(): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getAllBookings()
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getUpcomingMeetings(): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getUpcomingMeetings()
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        val data = response.data
        
        val flattenedRecurrences = data.recurrenceRoomBookings.flatMap { recurring ->
            if (!recurring.dates.isNullOrEmpty()) {
                recurring.dates.map { recurrenceDate ->
                    recurring.copy(
                        id = recurrenceDate.meetingId ?: recurring.id,
                        meetingId = recurrenceDate.meetingId,
                        date = recurrenceDate.date
                    )
                }
            } else {
                listOf(recurring)
            }
        }
        
        val allMeetings = data.singleRoomBookings + flattenedRecurrences
        allMeetings.sortedWith(compareBy({ it.date }, { it.startTimeString }))
    }.mapErrors()

    override suspend fun getRoomMeetings(roomId: Long): Result<List<BookingResponseDTO>> = runCatching {
        val response = api.getRoomMeetings(roomId)
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getMeetingTypes(): Result<List<MeetingTypeDTO>> = runCatching {
        val response = api.getAllMeetingTypes()
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun createMeetingType(request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO): Result<Unit> = runCatching {
        val response = api.createMeetingType(request)
        if (!response.success) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
    }.mapErrors()

    override suspend fun updateMeetingType(
        meetingTypeId: Long,
        request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO
    ): Result<Unit> = runCatching {
        val response = api.updateMeetingType(meetingTypeId, request)
        if (!response.success) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
    }.mapErrors()

    override suspend fun changeMeetingTypeStatus(meetingTypeId: Long, status: String): Result<Unit> = runCatching {
        val response = api.changeMeetingTypeStatus(
            meetingTypeId,
            com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO(status = status)
        )
        if (!response.success) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
    }.mapErrors()

    override suspend fun getCalendarMonth(date: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO(date = date)
        val response = api.getCalendarMonth(request)
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getCalendarWeek(date: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO(date = date)
        val response = api.getCalendarWeek(request)
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getCalendarDay(date: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO(date = date)
        val response = api.getCalendarDay(request)
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getCalendarDateInterval(startDate: String, endDate: String): Result<List<BookingResponseDTO>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderTimelineBetweenRequestDTO(startDate = startDate, endDate = endDate)
        val response = api.getCalendarDateInterval(request)
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()

    override suspend fun getBookingDetails(bookingId: Long): Result<BookingResponseDTO> = runCatching {
        val response = api.getBookedRoomById(bookingId)
        if (!response.success || response.data == null) throw Exception(ErrorMapper.sanitizeServerMessage(response.message))
        response.data
    }.mapErrors()
}
