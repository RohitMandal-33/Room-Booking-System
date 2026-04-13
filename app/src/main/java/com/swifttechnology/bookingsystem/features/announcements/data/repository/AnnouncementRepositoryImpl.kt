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
        val response = api.getAllAnnouncements()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data.content?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getPinnedAnnouncements(): Result<List<Announcement>> = runCatching {
        val response = api.getPinnedAnnouncements()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data.content?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun getTargetedPinnedAnnouncements(): Result<List<Announcement>> = runCatching {
        val response = api.getTargetedPinnedAnnouncements()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data.map { it.toDomain() }
    }

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

    private fun AnnouncementDTO.toDomain() = Announcement(
        id = id ?: 0L,
        title = title.orEmpty(),
        message = message.orEmpty(),
        priorityLevel = priorityLevel.orEmpty(),
        pinned = pinned ?: false,
        authorName = authorName.orEmpty(),
        authorPosition = authorPosition.orEmpty(),
        authorId = authorId,
        createdAt = createdAt.orEmpty()
    )
}
