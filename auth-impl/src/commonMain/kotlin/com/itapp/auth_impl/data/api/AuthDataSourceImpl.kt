package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDTO
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

private const val AUTH_BASE_URL = "https://api.yourservice.com"

@Inject
class AuthDataSourceImpl(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun validatePhone(request: ValidatePhoneRequestDTO) {
        httpClient.post(AUTH_BASE_URL) {
            setBody(request)
        }
    }

    override suspend fun login(request: LoginRequestDto) {
        httpClient.post(AUTH_BASE_URL) {
            setBody(request)
        }
    }
}