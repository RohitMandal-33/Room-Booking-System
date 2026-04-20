package com.swifttechnology.bookingsystem.features.announcements.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementDTO
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementPageDTO
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementRequestDTO
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AnnouncementApiService {

    /** POST /api/v1/announcement/list — all announcements (paginated) */
    @POST(APIEndpoint.ANNOUNCEMENT_LIST)
    suspend fun getAllAnnouncements(
        @Body request: com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.PaginatedDataRequestDTO
    ): GlobalResponse<AnnouncementPageDTO>

    /**
     * POST /api/v1/announcement/get — announcements filtered by pinStatus
     *  - pinStatus = true  → pinned announcements
     *  - pinStatus = false → un-pinned announcements
     */
    @POST(APIEndpoint.ANNOUNCEMENT_GET)
    suspend fun getAnnouncementsByPinStatus(
        @Body request: com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementPinStatusRequestDTO
    ): GlobalResponse<AnnouncementPageDTO>

    /** POST /api/v1/announcement/add */
    @POST(APIEndpoint.ANNOUNCEMENT_ADD)
    suspend fun addAnnouncement(
        @Body request: AnnouncementRequestDTO
    ): GlobalResponse<Any>

    /** PUT /api/v1/announcement/{id}/update */
    @PUT(APIEndpoint.ANNOUNCEMENT_UPDATE)
    suspend fun updateAnnouncement(
        @Path("id") id: Long,
        @Body request: AnnouncementRequestDTO
    ): GlobalResponse<Any>

    /** DELETE /api/v1/announcement/{id}/delete */
    @DELETE(APIEndpoint.ANNOUNCEMENT_DELETE)
    suspend fun deleteAnnouncement(
        @Path("id") id: Long
    ): GlobalResponse<Any>

    /** PATCH /api/v1/announcement/{id}/change-pin-status */
    @PATCH(APIEndpoint.ANNOUNCEMENT_CHANGE_PIN)
    suspend fun changePinStatus(
        @Path("id") id: Long
    ): GlobalResponse<Any>

    /** GET /api/v1/announcement/{id} */
    @GET(APIEndpoint.ANNOUNCEMENT_BY_ID)
    suspend fun getAnnouncementById(
        @Path("id") id: Long
    ): GlobalResponse<AnnouncementDTO>

    /** PATCH /api/v1/announcement/{id}/mark-as-read */
    @PATCH(APIEndpoint.ANNOUNCEMENT_MARK_READ)
    suspend fun markAnnouncementAsRead(
        @Path("id") id: Long
    ): GlobalResponse<Any>

    /** DELETE /api/v1/announcements/batch */
    @retrofit2.http.HTTP(method = "DELETE", path = APIEndpoint.ANNOUNCEMENT_BATCH_DELETE, hasBody = true)
    suspend fun deleteBulkAnnouncements(
        @Body request: com.swifttechnology.bookingsystem.features.announcements.data.dtos.DeleteIdsRequestDTO
    ): GlobalResponse<Any>
}
