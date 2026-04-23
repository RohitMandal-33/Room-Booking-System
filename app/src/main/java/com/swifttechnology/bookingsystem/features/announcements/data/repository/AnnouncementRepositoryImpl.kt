package com.swifttechnology.bookingsystem.features.announcements.data.repository

import com.swifttechnology.bookingsystem.features.announcements.data.api.AnnouncementApiService
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementDTO
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementRequestDTO
import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import com.swifttechnology.bookingsystem.features.announcements.domain.repository.AnnouncementRepository
import javax.inject.Inject

class AnnouncementRepositoryImpl @Inject constructor(
    private val api: AnnouncementApiService
) : AnnouncementRepository {

    override suspend fun getAllAnnouncements(): Result<List<Announcement>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.PaginatedDataRequestDTO(pageNo = 0, pageSize = 50)
        val response = api.getAllAnnouncements(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data.content?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getPinnedAnnouncements(): Result<List<Announcement>> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementPinStatusRequestDTO(
            pageNo   = 0,
            pageSize = 50,
            pinStatus = true
        )
        val response = api.getAnnouncementsByPinStatus(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data.content?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getTargetedPinnedAnnouncements(): Result<List<Announcement>> =
        getPinnedAnnouncements()

    override suspend fun addAnnouncement(request: AnnouncementRequestDTO): Result<Unit> = runCatching {
        val response = api.addAnnouncement(request)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun updateAnnouncement(id: Long, request: AnnouncementRequestDTO): Result<Unit> = runCatching {
        val response = api.updateAnnouncement(id, request)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun deleteAnnouncement(id: Long): Result<Unit> = runCatching {
        val response = api.deleteAnnouncement(id)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun changePinStatus(id: Long): Result<Unit> = runCatching {
        val response = api.changePinStatus(id)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun deleteBulkAnnouncements(ids: List<Long>): Result<Unit> = runCatching {
        val request = com.swifttechnology.bookingsystem.features.announcements.data.dtos.DeleteIdsRequestDTO(ids)
        val response = api.deleteBulkAnnouncements(request)
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun markAsRead(id: Long): Result<Unit> = runCatching {
        val response = api.markAnnouncementAsRead(id)
        if (!response.success) throw Exception(response.message)
    }

    private fun AnnouncementDTO.toDomain() = Announcement(
        id             = id ?: 0L,
        title          = title.orEmpty(),
        message        = message.orEmpty(),
        priorityLevel  = priorityLevel.orEmpty(),
        pinned         = pinned ?: false,
        authorName     = authorName.orEmpty(),
        authorPosition = authorPosition.orEmpty(),
        authorId       = authorId,
        createdAt      = createdAt.orEmpty(),
        startDate      = startDate,
        endDate        = endDate,
        isRead         = read ?: isRead ?: false
    )
}
