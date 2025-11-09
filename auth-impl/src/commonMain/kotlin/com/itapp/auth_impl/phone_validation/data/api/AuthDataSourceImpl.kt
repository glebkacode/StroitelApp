package com.itapp.auth_impl.phone_validation.data.api

import com.itapp.auth_impl.phone_validation.data.model.request.LoginRequestDTO
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

private const val AUTH_BASE_URL = "https://api.yourservice.com"

internal class AuthDataSourceImpl(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun login(request: LoginRequestDTO) {
        httpClient.post(AUTH_BASE_URL) {
            setBody(request)
        }
    }
}