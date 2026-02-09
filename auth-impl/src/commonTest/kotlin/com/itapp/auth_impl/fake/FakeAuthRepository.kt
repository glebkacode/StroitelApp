package com.itapp.auth_impl.fake

import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.repository.AuthRepository

class FakeAuthRepository : AuthRepository {

    var validatePhoneCalls: MutableList<ValidationPhoneDto> = mutableListOf()
    var validatePhoneException: Exception? = null

    override suspend fun validatePhone(dto: ValidationPhoneDto) {
        validatePhoneException?.let { throw it }
        validatePhoneCalls.add(dto)
    }

    fun reset() {
        validatePhoneCalls.clear()
        validatePhoneException = null
    }
}
