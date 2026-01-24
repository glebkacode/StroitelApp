package com.itapp.auth_impl.fake

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    var validatePhoneCalls: MutableList<ValidationPhoneDto> = mutableListOf()
    var loginCalls: MutableList<LoginDto> = mutableListOf()

    var validatePhoneException: Exception? = null
    var loginException: Exception? = null

    override suspend fun validatePhone(dto: ValidationPhoneDto) {
        validatePhoneException?.let { throw it }
        validatePhoneCalls.add(dto)
    }

    override suspend fun login(dto: LoginDto) {
        loginException?.let { throw it }
        loginCalls.add(dto)
    }

    fun reset() {
        validatePhoneCalls.clear()
        loginCalls.clear()
        validatePhoneException = null
        loginException = null
    }
}
