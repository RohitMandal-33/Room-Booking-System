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
import retrofit2.http.Query

interface AnnouncementApiService {

    /** GET /api/v1/announcement/list */
    @GET(APIEndpoint.ANNOUNCEMENT_LIST)
    suspend fun getAllAnnouncements(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): GlobalResponse<AnnouncementPageDTO>

    /** GET /api/v1/announcement/pinned-list */
    @GET(APIEndpoint.ANNOUNCEMENT_PINNED_LIST)
    suspend fun getPinnedAnnouncements(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 50
    ): GlobalResponse<AnnouncementPageDTO>

    /** GET /api/v1/get-Targeted-Audience-Announcement */
    @GET(APIEndpoint.ANNOUNCEMENT_TARGETED)
    suspend fun getTargetedAnnouncements(): GlobalResponse<List<AnnouncementDTO>>

    /** GET /api/v1/get-Targeted-Audience-pinned-Announcement */
    @GET(APIEndpoint.ANNOUNCEMENT_TARGETED_PINNED)
    suspend fun getTargetedPinnedAnnouncements(): GlobalResponse<List<AnnouncementDTO>>

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
}
