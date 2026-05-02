package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.delay

private const val MOCK_DELAY_MS = 1500L

@Inject
class AuthDataSourceImpl : AuthDataSource {

    override suspend fun validatePhone(request: ValidatePhoneRequestDto) {
        delay(MOCK_DELAY_MS)
    }
}
