package com.itapp.auth_impl.domain.repository

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidateSmsCodeDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto

interface AuthRepository {
    suspend fun validatePhone(dto: ValidationPhoneDto)
    suspend fun validateSmsCode(dto: ValidateSmsCodeDto)
    suspend fun login(dto: LoginDto)
}
