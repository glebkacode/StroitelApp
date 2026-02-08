package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.data.model.request.ValidateSmsCodeRequestDto
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay

private const val AUTH_BASE_URL = "https://api.yourservice.com"
private const val MOCK_DELAY_MS = 1500L
private const val MOCK_VALID_SMS_CODE = "1234"

@Inject
class AuthDataSourceImpl(
    private val httpClient: HttpClient
) : AuthDataSource {

    override suspend fun validatePhone(request: ValidatePhoneRequestDto) {
        // Mock: имитируем задержку сети
        delay(MOCK_DELAY_MS)
        /*httpClient.post(AUTH_BASE_URL) {
            setBody(request)
        }*/
    }

    override suspend fun validateSmsCode(request: ValidateSmsCodeRequestDto) {
        // Mock: имитируем задержку сети и проверку SMS кода
        delay(MOCK_DELAY_MS)
        if (request.smsCode != MOCK_VALID_SMS_CODE) {
            throw InvalidSmsCodeException("Неверный код подтверждения")
        }
        /*httpClient.post("$AUTH_BASE_URL/validate-sms") {
            setBody(request)
        }*/
    }

    override suspend fun login(request: LoginRequestDto) {
        // Mock: имитируем задержку сети
        delay(MOCK_DELAY_MS)
        /*httpClient.post("$AUTH_BASE_URL/login") {
            setBody(request)
        }*/
    }
}

class InvalidSmsCodeException(message: String) : Exception(message)
