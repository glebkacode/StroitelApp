package com.itapp.auth_impl.data.repository

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.mapping.toRequest
import com.itapp.auth_impl.domain.model.LoginDTO
import com.itapp.auth_impl.domain.repository.AuthRepository
import dev.zacsweers.metro.Inject

@Inject
class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(dto: LoginDTO) {
        authDataSource.login(dto.toRequest())
    }
}