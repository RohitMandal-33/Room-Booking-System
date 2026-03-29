package com.swifttechnology.bookingsystem.features.participants.data.repository

import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ParticipantRepositoryImpl @Inject constructor() : ParticipantRepository {

    private val allParticipants = listOf(
        Participant(1L, "Sarah Johnson", "Senior Engineer", "sarah.j@fintech.com", "+1 (555) 123-4567", 12, "Engineering"),
        Participant(2L, "Michael Chen", "Tech Lead", "michael.c@fintech.com", "+1 (555) 234-5678", 18, "Engineering"),
        Participant(3L, "Emily Rodriguez", "DevOps Engineer", "emily.r@fintech.com", "+1 (555) 345-6789", 24, "Engineering"),
        Participant(4L, "James Lee", "Product Manager", "james.l@fintech.com", "+1 (555) 456-7890", 9, "Product"),
        Participant(5L, "Priya Patel", "Data Scientist", "priya.p@fintech.com", "+1 (555) 567-8901", 15, "Data"),
        Participant(6L, "Tom Walker", "UX Designer", "tom.w@fintech.com", "+1 (555) 678-9012", 7, "Design"),
    )

    override fun getParticipants(): Flow<List<Participant>> = flow {
        emit(allParticipants)
    }

    override fun searchParticipants(query: String): Flow<List<Participant>> = flow {
        if (query.isBlank()) {
            emit(allParticipants)
        } else {
            val filtered = allParticipants.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true) ||
                it.department.contains(query, ignoreCase = true)
            }
            emit(filtered)
        }
    }
}
