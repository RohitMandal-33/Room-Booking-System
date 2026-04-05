package com.swifttechnology.bookingsystem.features.participants.domain.repository

import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import kotlinx.coroutines.flow.Flow

interface CustomGroupRepository {
    fun getCustomGroups(): Flow<List<CustomGroup>>
    suspend fun createCustomGroup(name: String, description: String?, memberIds: List<Long>): Result<CustomGroup>
    suspend fun updateCustomGroup(id: Long, name: String, description: String?, memberIds: List<Long>): Result<CustomGroup>
    suspend fun deleteCustomGroup(id: Long): Result<Unit>
}
