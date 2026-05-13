package com.swifttechnology.bookingsystem.features.user.data.repository

import com.google.gson.JsonParser
import com.swifttechnology.bookingsystem.features.user.data.api.UserApiService
import com.swifttechnology.bookingsystem.features.user.data.dtos.*
import com.swifttechnology.bookingsystem.features.user.domain.repository.UserRepository
import javax.inject.Inject
import retrofit2.HttpException

class UserRepositoryImpl @Inject constructor(
    private val api: UserApiService
) : UserRepository {

    private fun messageFromErrorBody(e: HttpException): String? {
        val raw = e.response()?.errorBody()?.string() ?: return null
        return runCatching {
            JsonParser().parse(raw).asJsonObject.get("message")?.asString
        }.getOrNull()
    }

    override suspend fun createUser(
        firstName: String,
        lastName: String,
        email: String,
        position: String,
        phoneNumber: String,
        password: String,
        roleId: Long,
        departmentId: Long
    ): Result<Unit> =
        try {
            val response = api.createUser(
                CreateUserRequestDTO(
                    firstname = firstName,
                    lastname = lastName,
                    email = email,
                    position = position,
                    phoneNo = phoneNumber,
                    password = password,
                    roleId = roleId,
                    departmentId = departmentId
                )
            )
            if (!response.success) Result.failure(Exception(response.message))
            else Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(
                Exception(messageFromErrorBody(e) ?: "Request failed (${e.code()})")
            )
        }

    override suspend fun updateUser(
        id: Long,
        firstName: String,
        lastName: String,
        email: String,
        position: String,
        phoneNumber: String,
        roleId: Long,
        departmentId: Long
    ): Result<Unit> =
        try {
            val response = api.updateUser(
                id = id,
                request = UpdateUserRequestDTO(
                    firstname = firstName,
                    lastname = lastName,
                    email = email,
                    position = position,
                    phoneNo = phoneNumber,
                    roleId = roleId,
                    departmentId = departmentId
                )
            )
            if (!response.success) Result.failure(Exception(response.message))
            else Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(
                Exception(messageFromErrorBody(e) ?: "Request failed (${e.code()})")
            )
        }

    override suspend fun getAllUsers(
        pageNo: Int,
        pageSize: Int,
        email: String?,
        deptName: String?,
        sortBy: String,
        sortDir: String
    ): Result<UserPageDTO> = runCatching {
        val response = api.getAllUsers(
            UserDataRequestDTO(
                pageNo = pageNo,
                pageSize = pageSize,
                email = email,
                departmentId = deptName,
                sortBy = sortBy,
                sortDir = sortDir
            )
        )
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun getAllActiveUsers(
        pageNo: Int,
        pageSize: Int,
        email: String?,
        deptName: String?,
        sortBy: String,
        sortDir: String
    ): Result<UserPageDTO> = runCatching {
        val response = api.getAllActiveUsers(
            UserDataRequestDTO(
                pageNo = pageNo,
                pageSize = pageSize,
                email = email,
                departmentId = deptName,
                sortBy = sortBy,
                sortDir = sortDir
            )
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

    override suspend fun getCurrentUser(): Result<UserDetailsDTO> = runCatching {
        val response = api.getCurrentUser()
        if (!response.success || response.data == null) throw Exception(response.message)
        response.data
    }

    override suspend fun changeUserStatus(id: Long, status: String): Result<Unit> =
        try {
            val response = api.changeUserStatus(
                id = id,
                request = com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO(status)
            )
            if (!response.success) Result.failure(Exception(response.message))
            else Result.success(Unit)
        } catch (e: HttpException) {
            Result.failure(
                Exception(messageFromErrorBody(e) ?: "Request failed (${e.code()})")
            )
        }

    override suspend fun updateLoggedInUser(
        firstName: String,
        lastName: String,
        position: String,
        phoneNumber: String,
        departmentId: Long
    ): Result<Unit> = try {
        val response = api.updateLoggedInUser(
            LoggedInUserInfoUpdateRequestDTO(
                firstname = firstName,
                lastname = lastName,
                position = position,
                phoneNo = phoneNumber,
                departmentId = departmentId
            )
        )
        if (!response.success) Result.failure(Exception(response.message))
        else Result.success(Unit)
    } catch (e: HttpException) {
        Result.failure(
            Exception(messageFromErrorBody(e) ?: "Request failed (${e.code()})")
        )
    }
}
