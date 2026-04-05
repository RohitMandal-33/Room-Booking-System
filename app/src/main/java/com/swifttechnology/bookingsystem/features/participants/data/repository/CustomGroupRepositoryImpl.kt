package com.swifttechnology.bookingsystem.features.participants.data.repository

import com.swifttechnology.bookingsystem.features.participants.data.api.CustomGroupApiService
import com.swifttechnology.bookingsystem.features.participants.data.dtos.CustomGroupRequestDTO
import com.swifttechnology.bookingsystem.features.participants.domain.model.CustomGroup
import com.swifttechnology.bookingsystem.features.participants.domain.repository.CustomGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CustomGroupRepositoryImpl @Inject constructor(
    private val api: CustomGroupApiService
) : CustomGroupRepository {

    override fun getCustomGroups(): Flow<List<CustomGroup>> = flow {
        val response = runCatching { api.getAllCustomGroups() }.getOrThrow()
        if (!response.success || response.data == null) throw Exception(response.message)
        
        val content = response.data
        val groups = content.map { dto ->
            CustomGroup(
                id = dto.id ?: 0,
                name = dto.groupName ?: "Unknown Group",
                description = dto.description ?: "",
                memberCount = dto.member?.size ?: 0
            )
        }
        emit(groups)
    }

    override suspend fun createCustomGroup(name: String, description: String?, memberIds: List<Long>): Result<CustomGroup> = runCatching {
        val request = CustomGroupRequestDTO(groupName = name, description = description, member = memberIds)
        val response = api.addCustomGroup(request)
        if (!response.success || response.data == null) throw Exception(response.message)
        
        val dto = response.data
        CustomGroup(
            id = dto.id ?: 0,
            name = dto.groupName ?: "Unknown Group",
            description = dto.description ?: "",
            memberCount = dto.member?.size ?: 0
        )
    }

    override suspend fun updateCustomGroup(id: Long, name: String, description: String?, memberIds: List<Long>): Result<CustomGroup> = runCatching {
        val request = CustomGroupRequestDTO(groupName = name, description = description, member = memberIds)
        val response = api.updateCustomGroup(id, request)
        if (!response.success || response.data == null) throw Exception(response.message)
        
        val dto = response.data
        CustomGroup(
            id = dto.id ?: 0,
            name = dto.groupName ?: "Unknown Group",
            description = dto.description ?: "",
            memberCount = dto.member?.size ?: 0
        )
    }

    override suspend fun deleteCustomGroup(id: Long): Result<Unit> = runCatching {
        val response = api.deleteCustomGroup(id)
        if (!response.success) throw Exception(response.message)
    }
}
