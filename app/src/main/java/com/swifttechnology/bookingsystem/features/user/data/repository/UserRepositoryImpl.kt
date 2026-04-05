package com.swifttechnology.bookingsystem.features.user.data.repository

import com.swifttechnology.bookingsystem.features.user.data.api.UserApiService
import com.swifttechnology.bookingsystem.features.user.data.dtos.*
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApiService
) : UserRepository {

    override suspend fun createUser(
        firstname: String,
        lastname: String,
        email: String,
        position: String,
        phoneNo: String,
        password: String,
        roleId: Long,
        departmentId: Long
    ): Result<Unit> = runCatching {
        val response = api.createUser(
            CreateUserRequestDTO(
                firstname = firstname,
                lastname = lastname,
                email = email,
                position = position,
                phoneNo = phoneNo,
                password = password,
                roleId = roleId,
                departmentId = departmentId
            )
        )
        if (!response.success) throw Exception(response.message)
    }

    override suspend fun getAllUsers(
        pageNo: Int,
        pageSize: Int,
        email: String?,
        deptName: String?
    ): Result<UserPageDTO> = runCatching {
        val response = api.getAllUsers(
            UserDataRequestDTO(pageNo = pageNo, pageSize = pageSize, email = email, deptName = deptName)
        )
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getUserById(id: Long): Result<UserDetailsDTO> = runCatching {
        val response = api.getUserById(id)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun searchUsers(
        email: String?,
        departmentId: Long?,
        page: Int,
        size: Int
    ): Result<UserPageDTO> = runCatching {
        val response = api.searchUsers(email = email, departmentId = departmentId, page = page, size = size)
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }
}
