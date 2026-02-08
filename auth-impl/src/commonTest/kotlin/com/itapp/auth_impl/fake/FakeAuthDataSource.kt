package com.itapp.auth_impl.fake

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.data.model.request.ValidateSmsCodeRequestDto

class FakeAuthDataSource : AuthDataSource {

    var validatePhoneCalls: MutableList<ValidatePhoneRequestDto> = mutableListOf()
    var validatePhoneException: Exception? = null

    var validateSmsCodeCalls: MutableList<ValidateSmsCodeRequestDto> = mutableListOf()
    var validateSmsCodeException: Exception? = null

    var loginCalls: MutableList<LoginRequestDto> = mutableListOf()
    var loginException: Exception? = null

    override suspend fun validatePhone(request: ValidatePhoneRequestDto) {
        validatePhoneException?.let { throw it }
        validatePhoneCalls.add(request)
    }

    override suspend fun validateSmsCode(request: ValidateSmsCodeRequestDto) {
        validateSmsCodeException?.let { throw it }
        validateSmsCodeCalls.add(request)
    }

    override suspend fun login(request: LoginRequestDto) {
        loginException?.let { throw it }
        loginCalls.add(request)
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
