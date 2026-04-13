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

        val participants = content.map { it.toParticipant() }

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

    override suspend fun getParticipantsByIds(ids: List<Long>): Result<List<Participant>> = runCatching {
        if (ids.isEmpty()) return@runCatching emptyList()
        
        // If there's no batch API, we fetch individually (or from a cached list if we had one)
        ids.map { id ->
            userRepository.getUserById(id).getOrThrow().toParticipant()
        }
    }

    private fun com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO.toParticipant(): Participant {
        val fullName = listOfNotNull(firstname, lastname)
            .joinToString(" ")
            .takeIf { it.isNotBlank() } ?: email

        return Participant(
            id = id,
            name = fullName,
            role = role ?: "Unknown Role",
            email = email,
            phone = phoneNo?.trim()?.takeIf { it.isNotEmpty() } ?: "N/A",
            meetingCount = 0,
            department = department ?: "Unknown Department",
            status = status ?: "UNKNOWN",
            position = position ?: "N/A",
            firstname = firstname?.trim()?.takeIf { it.isNotEmpty() },
            lastname = lastname?.trim()?.takeIf { it.isNotEmpty() },
            departmentId = departmentId,
            roleId = roleId
        )
    }
}
