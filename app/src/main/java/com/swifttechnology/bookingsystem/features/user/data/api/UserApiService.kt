package com.swifttechnology.bookingsystem.features.user.data.api

import com.swifttechnology.bookingsystem.core.network.APIEndpoint
import com.swifttechnology.bookingsystem.core.network.GlobalResponse
import com.swifttechnology.bookingsystem.features.user.data.dtos.*
import retrofit2.http.*

interface UserApiService {

    @POST(APIEndpoint.SIGNUP)
    suspend fun createUser(@Body request: CreateUserRequestDTO): GlobalResponse<Unit>

    @POST(APIEndpoint.USERS_GET_ALL)
    suspend fun getAllUsers(@Body request: UserDataRequestDTO): GlobalResponse<UserPageDTO>

    @POST(APIEndpoint.USERS_GET_ALL_ACTIVE)
    suspend fun getAllActiveUsers(@Body request: com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.PaginatedDataRequestDTO): GlobalResponse<UserPageDTO>

    @PUT(APIEndpoint.USER_UPDATE)
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body request: UpdateUserRequestDTO
    ): GlobalResponse<Unit>

    @PATCH(APIEndpoint.USER_CHANGE_STATUS)
    suspend fun changeUserStatus(
        @Path("id") id: Long,
        @Body request: com.swifttechnology.bookingsystem.features.meetingrooms.data.dtos.StatusChangeRequestDTO
    ): GlobalResponse<Unit>

    @GET(APIEndpoint.USER_BY_ID)
    suspend fun getUserById(@Path("id") id: Long): GlobalResponse<UserDetailsDTO>

    @GET(APIEndpoint.USER_GET_ALL_SIMPLE)
    suspend fun getAllUsersSimple(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): GlobalResponse<UserPageDTO>

    @GET(APIEndpoint.USER_SEARCH)
    suspend fun searchUsers(
        @Query("email") email: String? = null,
        @Query("departmentId") departmentId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sortBy") sortBy: String = "id"
    ): GlobalResponse<UserPageDTO>

    @GET(APIEndpoint.USER_PUBLIC)
    suspend fun testPublic(): String

    @GET(APIEndpoint.USER_PRIVATE)
    suspend fun testPrivate(): String

    @GET(APIEndpoint.USER_ADMIN)
    suspend fun adminOnly(): String
}
