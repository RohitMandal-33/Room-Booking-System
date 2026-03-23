package com.swifttechnology.bookingsystem.core.network

import com.swifttechnology.bookingsystem.core.network.plugins.TokenAuthenticator
import com.swifttechnology.bookingsystem.core.network.plugins.TokenPlugin
import com.swifttechnology.bookingsystem.core.storage.TokenStorage
import dagger.Lazy
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Centralized provider for OkHttpClient and Retrofit instances.
 * All feature API services are created through [retrofit].
 * Consumed by [com.swifttechnology.bookingsystem.di.NetworkModule].
 */
object NetworkProvider {

    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 30L
    private const val WRITE_TIMEOUT_SECONDS = 30L

    fun buildOkHttpClient(tokenStorage: Lazy<TokenStorage>): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(TokenPlugin(tokenStorage))
            .authenticator(TokenAuthenticator(tokenStorage))
            .addInterceptor(logging)
            .build()
    }

    fun buildRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(APIEndpoint.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
