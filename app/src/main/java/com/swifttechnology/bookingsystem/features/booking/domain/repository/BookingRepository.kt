package com.swifttechnology.bookingsystem.features.booking.domain.repository

import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO

/**
 * Booking domain repository contract.
 */
interface BookingRepository {
    suspend fun bookRoom(request: RoomBookingRequestDTO): Result<BookingResponseDTO>
    suspend fun updateBooking(bookingId: Long, request: RoomBookingRequestDTO): Result<BookingResponseDTO>
    suspend fun getMyBookings(): Result<List<BookingResponseDTO>>
    suspend fun getAllBookings(): Result<List<BookingResponseDTO>>
    suspend fun getUpcomingMeetings(): Result<List<BookingResponseDTO>>
}
