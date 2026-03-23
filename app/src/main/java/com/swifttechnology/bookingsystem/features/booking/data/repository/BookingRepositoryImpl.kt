package com.swifttechnology.bookingsystem.features.booking.data.repository

import com.swifttechnology.bookingsystem.features.booking.data.api.BookingApiService
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
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
}
