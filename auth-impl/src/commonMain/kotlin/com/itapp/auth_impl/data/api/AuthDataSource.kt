package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.data.model.request.ValidateSmsCodeRequestDto

interface AuthDataSource {
    suspend fun validatePhone(request: ValidatePhoneRequestDto)
    suspend fun validateSmsCode(request: ValidateSmsCodeRequestDto)
    suspend fun login(request: LoginRequestDto)
}
