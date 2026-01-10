package com.itapp.auth_impl.fake

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto

class FakeAuthDataSource : AuthDataSource {

    var validatePhoneCalls: MutableList<ValidatePhoneRequestDto> = mutableListOf()
    var loginCalls: MutableList<LoginRequestDto> = mutableListOf()

    var validatePhoneException: Exception? = null
    var loginException: Exception? = null

    override suspend fun validatePhone(request: ValidatePhoneRequestDto) {
        validatePhoneException?.let { throw it }
        validatePhoneCalls.add(request)
    }

    override suspend fun login(request: LoginRequestDto) {
        loginException?.let { throw it }
        loginCalls.add(request)
    }

    fun reset() {
        validatePhoneCalls.clear()
        loginCalls.clear()
        validatePhoneException = null
        loginException = null
    }
}
