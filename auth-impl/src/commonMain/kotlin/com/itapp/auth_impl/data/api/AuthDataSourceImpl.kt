package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay

private const val MOCK_DELAY_MS = 1500L

@Inject
class AuthDataSourceImpl(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun validatePhone(request: ValidatePhoneRequestDto) {
        delay(MOCK_DELAY_MS)
    }
}
