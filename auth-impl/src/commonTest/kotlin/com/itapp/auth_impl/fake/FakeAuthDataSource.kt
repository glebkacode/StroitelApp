package com.itapp.auth_impl.fake

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto

class FakeAuthDataSource : AuthDataSource {

    var validatePhoneCalls: MutableList<ValidatePhoneRequestDto> = mutableListOf()
    var validatePhoneException: Exception? = null

    override suspend fun validatePhone(request: ValidatePhoneRequestDto) {
        validatePhoneException?.let { throw it }
        validatePhoneCalls.add(request)
    }

    fun reset() {
        validatePhoneCalls.clear()
        validatePhoneException = null
    }
}
