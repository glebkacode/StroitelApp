package com.itapp.auth_impl.data.repository

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.mapping.toRequest
import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.repository.AuthRepository
import dev.zacsweers.metro.Inject

@Inject
class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun validatePhone(dto: ValidationPhoneDto) {
        authDataSource.validatePhone(request = dto.toRequest())
    }

    override suspend fun login(dto: LoginDto) {
        authDataSource.login(request = dto.toRequest())
    }
}