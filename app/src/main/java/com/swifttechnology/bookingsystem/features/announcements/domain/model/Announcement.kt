package com.swifttechnology.bookingsystem.features.announcements.domain.model

data class Announcement(
    val id: Long,
    val title: String,
    val message: String,
    val priorityLevel: String,  // "HIGH" | "NORMAL"
    val pinned: Boolean,
    val authorName: String,
    val authorPosition: String,
    val authorId: Long?,
    val createdAt: String       // raw ISO string, formatted in UI
)
