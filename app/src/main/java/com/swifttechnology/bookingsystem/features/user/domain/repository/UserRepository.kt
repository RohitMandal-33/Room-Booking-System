package com.swifttechnology.bookingsystem.features.user.domain.repository

import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserPageDTO

// User management domain repository contract.

interface UserRepository {
    suspend fun createUser(
        firstName: String,
        lastName: String,
        email: String,
        position: String,
        phoneNumber: String,
        password: String,
        roleId: Long,
        departmentId: Long
    ): Result<Unit>

    suspend fun updateUser(
        id: Long,
        firstName: String,
        lastName: String,
        email: String,
        position: String,
        phoneNumber: String,
        roleId: Long,
        departmentId: Long
    ): Result<Unit>

    suspend fun getAllUsers(
        pageNo: Int = 0,
        pageSize: Int = 10,
        email: String? = null,
        deptName: String? = null,
        sortBy: String = "id",
        sortDir: String = "asc"
    ): Result<UserPageDTO>

    suspend fun getAllActiveUsers(
        pageNo: Int = 0,
        pageSize: Int = 10,
        email: String? = null,
        deptName: String? = null,
        sortBy: String = "id",
        sortDir: String = "asc"
    ): Result<UserPageDTO>
    suspend fun getUserById(id: Long): Result<UserDetailsDTO>
    suspend fun searchUsers(email: String? = null, departmentId: Long? = null, page: Int = 0, size: Int = 10): Result<UserPageDTO>
    suspend fun getCurrentUser(): Result<UserDetailsDTO>
    suspend fun updateLoggedInUser(
        firstName: String,
        lastName: String,
        position: String,
        phoneNumber: String,
        departmentId: Long
    ): Result<Unit>
    suspend fun changeUserStatus(id: Long, status: String): Result<Unit>
}
