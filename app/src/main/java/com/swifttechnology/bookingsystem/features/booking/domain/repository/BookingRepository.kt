package com.swifttechnology.bookingsystem.features.booking.domain.repository

import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO

/**
 * Booking domain repository contract.
 */
interface BookingRepository {
    suspend fun bookRoom(request: RoomBookingRequestDTO): Result<Unit>
    suspend fun updateBooking(bookingId: Long?, request: RoomBookingRequestDTO): Result<Unit>
    /** Updates every occurrence in a recurrence series. */
    suspend fun updateRecurrenceBooking(recurrenceId: String, request: RoomBookingRequestDTO): Result<Unit>
    suspend fun getMyBookings(): Result<List<BookingResponseDTO>>
    suspend fun getAllBookings(): Result<List<BookingResponseDTO>>
    suspend fun getUpcomingMeetings(): Result<List<BookingResponseDTO>>
    suspend fun getRoomMeetings(roomId: Long): Result<List<BookingResponseDTO>>
    suspend fun getMeetingTypes(): Result<List<MeetingTypeDTO>>
    suspend fun createMeetingType(request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO): Result<Unit>
    suspend fun updateMeetingType(meetingTypeId: Long, request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO): Result<Unit>
    suspend fun changeMeetingTypeStatus(meetingTypeId: Long, status: String): Result<Unit>
    suspend fun getCalendarMonth(date: String): Result<List<BookingResponseDTO>>
    suspend fun getCalendarWeek(date: String): Result<List<BookingResponseDTO>>
    suspend fun getCalendarDay(date: String): Result<List<BookingResponseDTO>>
    suspend fun getCalendarDateInterval(startDate: String, endDate: String): Result<List<BookingResponseDTO>>
    suspend fun getBookingDetails(bookingId: Long): Result<BookingResponseDTO>
    suspend fun changeBookingStatus(bookingId: Long, status: String): Result<Unit>
}
