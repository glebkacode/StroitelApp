package com.itapp.auth_impl.phone_validation.domain.repository

import com.itapp.auth_impl.phone_validation.domain.model.LoginDTO

internal interface AuthRepository {
    suspend fun login(dto: LoginDTO)
}