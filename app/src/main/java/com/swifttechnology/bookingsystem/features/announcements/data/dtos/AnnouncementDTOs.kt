package com.swifttechnology.bookingsystem.features.announcements.data.dtos

import com.google.gson.annotations.SerializedName

data class AnnouncementPinStatusRequestDTO(
    @SerializedName("pageNo") val pageNo: Int? = null,
    @SerializedName("pageSize") val pageSize: Int? = null,
    @SerializedName("sortBy") val sortBy: String? = null,
    @SerializedName("sortDir") val sortDir: String? = null,
    @SerializedName("pinStatus") val pinStatus: Boolean
)

data class AnnouncementRequestDTO(
    @SerializedName("title") val title: String,
    @SerializedName("message") val message: String,
    @SerializedName("priorityLevel") val priorityLevel: String,   // HIGH | NORMAL
    @SerializedName("pinned") val pinned: Boolean? = null,
    @SerializedName("roleId") val roleId: Long? = null,
    @SerializedName("groupId") val groupId: Long? = null,
    @SerializedName("allUser") val allUser: Boolean? = null,
    @SerializedName("authorId") val authorId: Long,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null
)

/**
 * Response DTO for a single announcement item returned from the API.
 */
data class AnnouncementDTO(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("priorityLevel") val priorityLevel: String? = null,
    @SerializedName("pinned") val pinned: Boolean? = null,
    @SerializedName("authorName") val authorName: String? = null,
    @SerializedName("authorPosition") val authorPosition: String? = null,
    @SerializedName("authorId") val authorId: Long? = null,
    @SerializedName("roleId") val roleId: Long? = null,
    @SerializedName("groupId") val groupId: Long? = null,
    @SerializedName("allUser") val allUser: Boolean? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null,
    @SerializedName("startDate") val startDate: String? = null,
    @SerializedName("endDate") val endDate: String? = null
)

/**
 * Paginated wrapper for list endpoints
 */
data class AnnouncementPageDTO(
    @SerializedName("content") val content: List<AnnouncementDTO>?,
    @SerializedName("pageNo") val pageNo: Int?,
    @SerializedName("pageSize") val pageSize: Int?,
    @SerializedName("totalElements") val totalElements: Long?,
    @SerializedName("totalPages") val totalPages: Int?,
    @SerializedName("last") val last: Boolean?
)
