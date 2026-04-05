package com.swifttechnology.bookingsystem.features.booking.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.RoomBookingRequestDTO
import retrofit2.http.*

interface BookingApiService {

    @POST(APIEndpoint.BOOK_ROOM)
    suspend fun bookRoom(@Body request: RoomBookingRequestDTO): GlobalResponse<BookingResponseDTO>

    @PUT(APIEndpoint.UPDATE_BOOKING)
    suspend fun updateBooking(
        @Path("bookingId") bookingId: Long,
        @Body request: RoomBookingRequestDTO
    ): GlobalResponse<BookingResponseDTO>

    @GET(APIEndpoint.MY_BOOKINGS)
    suspend fun getMyBookings(): GlobalResponse<List<BookingResponseDTO>>

    @GET(APIEndpoint.ALL_BOOKINGS)
    suspend fun getAllBookings(): GlobalResponse<List<BookingResponseDTO>>

    @GET(APIEndpoint.UPCOMING_MEETING)
    suspend fun getUpcomingMeetings(): GlobalResponse<List<BookingResponseDTO>>
}
