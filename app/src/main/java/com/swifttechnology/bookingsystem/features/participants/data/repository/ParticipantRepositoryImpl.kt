package com.swifttechnology.bookingsystem.features.participants.data.repository

import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import com.swifttechnology.bookingsystem.features.participants.domain.repository.ParticipantRepository
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ParticipantRepositoryImpl @Inject constructor(
    private val userRepository: UserRepository
) : ParticipantRepository {

    override fun getParticipants(): Flow<List<Participant>> = searchParticipants("")

    override fun searchParticipants(query: String): Flow<List<Participant>> = flow {
        val response = userRepository.getAllUsers(
            pageNo = 0,
            pageSize = 100,
            email = query.takeIf { it.isNotBlank() }
        )
        val content = response.getOrThrow().content ?: emptyList()

        val participants = content.map { dto ->
            val fullName = listOfNotNull(dto.firstname, dto.lastname)
                .joinToString(" ")
                .takeIf { it.isNotBlank() } ?: dto.email

            Participant(
                id = dto.id,
                name = fullName,
                role = dto.role ?: "Unknown Role",
                email = dto.email,
                phone = dto.phoneNumber ?: "N/A",
                meetingCount = 0,
                department = dto.department ?: "Unknown Department",
                status = dto.status ?: "UNKNOWN",
                position = dto.position ?: "N/A"
            )
        }

        // Emit the results. Since we passed the query to the API, 
        // we might not need extra local filtering, but it doesn't hurt 
        // if the API's 'email' search is slightly different.
        if (query.isBlank()) {
            emit(participants)
        } else {
            val filtered = participants.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.email.contains(query, ignoreCase = true) ||
                it.department.contains(query, ignoreCase = true)
            }
            emit(filtered)
        }
    }
}
