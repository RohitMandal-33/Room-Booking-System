package com.swifttechnology.bookingsystem.features.user.domain.repository

import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserDetailsDTO
import com.swifttechnology.bookingsystem.features.user.data.dtos.UserPageDTO

/**
 * User management domain repository contract.
 */
interface UserRepository {
    suspend fun createUser(email: String, password: String, roleId: Long, departmentId: Long): Result<Unit>
    suspend fun getAllUsers(pageNo: Int = 0, pageSize: Int = 10, email: String? = null, deptName: String? = null): Result<UserPageDTO>
    suspend fun getUserById(id: Long): Result<UserDetailsDTO>
    suspend fun searchUsers(email: String? = null, departmentId: Long? = null, page: Int = 0, size: Int = 10): Result<UserPageDTO>
}
