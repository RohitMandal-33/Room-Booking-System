package com.swifttechnology.bookingsystem.features.announcements.domain.repository

import com.swifttechnology.bookingsystem.features.announcements.domain.model.Announcement
import com.swifttechnology.bookingsystem.features.announcements.data.dtos.AnnouncementRequestDTO

interface AnnouncementRepository {
    suspend fun getAllAnnouncements(): Result<List<Announcement>>
    suspend fun getPinnedAnnouncements(): Result<List<Announcement>>
    suspend fun getTargetedPinnedAnnouncements(): Result<List<Announcement>>
    suspend fun addAnnouncement(request: AnnouncementRequestDTO): Result<Unit>
    suspend fun updateAnnouncement(id: Long, request: AnnouncementRequestDTO): Result<Unit>
    suspend fun deleteAnnouncement(id: Long): Result<Unit>
    suspend fun changePinStatus(id: Long): Result<Unit>
}
