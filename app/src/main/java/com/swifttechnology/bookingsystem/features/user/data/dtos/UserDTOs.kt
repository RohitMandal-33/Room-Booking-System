package com.swifttechnology.bookingsystem.features.user.data.dtos

/**
 * Request body for POST /api/v1/signup (Admin only).
 */
data class CreateUserRequestDTO(
    val email: String,
    val password: String,
    val roleId: Long,
    val departmentId: Long
)

/**
 * Request body for POST /api/v1/users/get-all (paginated + filtered).
 */
data class UserDataRequestDTO(
    val pageNo: Int? = 0,
    val pageSize: Int? = 10,
    val sortBy: String? = "id",
    val sortDir: String? = "asc",
    val email: String? = null,
    val deptName: String? = null
)

/**
 * User details returned from the API.
 */
data class UserDetailsDTO(
    val id: Long,
    val firstname: String? = null,
    val lastname: String? = null,
    val email: String,
    val position: String? = null,
    val phoneNumber: String? = null,
    val role: String? = null,
    val department: String? = null,
    val status: String? = null
)

/**
 * Paginated user list response.
 */
data class UserPageDTO(
    val content: List<UserDetailsDTO>? = null,
    val totalElements: Long? = null,
    val totalPages: Int? = null,
    val pageNo: Int? = null,
    val pageSize: Int? = null,
    val last: Boolean? = null
)
