package com.itapp.auth_impl.domain.repository

import com.itapp.auth_impl.domain.model.LoginDTO

interface AuthRepository {
    suspend fun login(dto: LoginDTO)
}