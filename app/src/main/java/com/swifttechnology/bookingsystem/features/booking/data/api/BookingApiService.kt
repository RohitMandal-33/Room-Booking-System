package com.swifttechnology.bookingsystem.features.booking.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.booking.data.dtos.BookingResponseDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.CalenderRequestDTO
import com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeDTO
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

    @PUT(APIEndpoint.UPDATE_RECURRENCE_BOOKING)
    suspend fun updateRecurrenceBooking(
        @Path("recurrenceId") recurrenceId: String,
        @Body request: RoomBookingRequestDTO
    ): GlobalResponse<BookingResponseDTO>

    @GET(APIEndpoint.MY_BOOKINGS)
    suspend fun getMyBookings(): GlobalResponse<List<BookingResponseDTO>>

    @GET(APIEndpoint.ALL_BOOKINGS)
    suspend fun getAllBookings(): GlobalResponse<List<BookingResponseDTO>>

    @GET(APIEndpoint.UPCOMING_MEETING)
    suspend fun getUpcomingMeetings(): GlobalResponse<List<BookingResponseDTO>>

    @PATCH(APIEndpoint.BOOKED_ROOM_CHANGE_STATUS)
    suspend fun changeBookedRoomStatus(
        @Path("roomBookingId") roomBookingId: Long,
        @Body request: com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO
    ): GlobalResponse<Unit>

    @GET(APIEndpoint.BOOKED_ROOM_BY_ID)
    suspend fun getBookedRoomById(
        @Path("roomBookingId") roomBookingId: Long
    ): GlobalResponse<BookingResponseDTO>

    @GET(APIEndpoint.ROOM_MEETINGS)
    suspend fun getRoomMeetings(
        @Path("roomId") roomId: Long
    ): GlobalResponse<List<BookingResponseDTO>>

    @POST(APIEndpoint.CALENDAR_WEEK)
    suspend fun getCalendarWeek(@Body request: CalenderRequestDTO): GlobalResponse<List<BookingResponseDTO>>

    @POST(APIEndpoint.CALENDAR_MONTH)
    suspend fun getCalendarMonth(@Body request: CalenderRequestDTO): GlobalResponse<List<BookingResponseDTO>>

    @POST(APIEndpoint.CALENDAR_DAY)
    suspend fun getCalendarDay(@Body request: CalenderRequestDTO): GlobalResponse<List<BookingResponseDTO>>

    // Meeting Types
    @POST(APIEndpoint.MEETING_TYPE_ADD)
    suspend fun createMeetingType(@Body request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO): GlobalResponse<Unit>

    @PUT(APIEndpoint.MEETING_TYPE_UPDATE)
    suspend fun updateMeetingType(
        @Path("meetingTypeId") meetingTypeId: Long,
        @Body request: com.swifttechnology.bookingsystem.features.booking.data.dtos.MeetingTypeRequestDTO
    ): GlobalResponse<Unit>

    @PATCH(APIEndpoint.MEETING_TYPE_CHANGE_STATUS)
    suspend fun changeMeetingTypeStatus(
        @Path("meetingTypeId") meetingTypeId: Long,
        @Body request: com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO
    ): GlobalResponse<Unit>

    @GET(APIEndpoint.MEETING_TYPE_GET_ALL)
    suspend fun getAllMeetingTypes(): GlobalResponse<List<MeetingTypeDTO>>
}
