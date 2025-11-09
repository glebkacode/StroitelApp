package com.itapp.auth_impl.phone_validation.data.repository

import com.itapp.auth_impl.phone_validation.data.api.AuthDataSource
import com.itapp.auth_impl.phone_validation.data.model.mapping.toRequest
import com.itapp.auth_impl.phone_validation.domain.model.LoginDTO
import com.itapp.auth_impl.phone_validation.domain.repository.AuthRepository

internal class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(dto: LoginDTO) {
        authDataSource.login(dto.toRequest())
    }
}