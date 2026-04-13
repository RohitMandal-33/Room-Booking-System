package com.swifttechnology.bookingsystem.features.participants.domain.repository

import com.swifttechnology.bookingsystem.features.participants.domain.model.Participant
import kotlinx.coroutines.flow.Flow

interface ParticipantRepository {
    fun getParticipants(): Flow<List<Participant>>
    fun searchParticipants(query: String): Flow<List<Participant>>
    suspend fun getParticipantsByIds(ids: List<Long>): Result<List<Participant>>
}
