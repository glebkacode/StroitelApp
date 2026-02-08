package com.itapp.auth_impl.fake

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidateSmsCodeDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    var validatePhoneCalls: MutableList<ValidationPhoneDto> = mutableListOf()
    var validatePhoneException: Exception? = null

    var validateSmsCodeCalls: MutableList<ValidateSmsCodeDto> = mutableListOf()
    var validateSmsCodeException: Exception? = null

    var loginCalls: MutableList<LoginDto> = mutableListOf()
    var loginException: Exception? = null

    override suspend fun validatePhone(dto: ValidationPhoneDto) {
        validatePhoneException?.let { throw it }
        validatePhoneCalls.add(dto)
    }

    override suspend fun validateSmsCode(dto: ValidateSmsCodeDto) {
        validateSmsCodeException?.let { throw it }
        validateSmsCodeCalls.add(dto)
    }

    override suspend fun login(dto: LoginDto) {
        loginException?.let { throw it }
        loginCalls.add(dto)
    }

    fun reset() {
        validatePhoneCalls.clear()
        validatePhoneException = null
        validateSmsCodeCalls.clear()
        validateSmsCodeException = null
        loginCalls.clear()
        loginException = null
    }
}
