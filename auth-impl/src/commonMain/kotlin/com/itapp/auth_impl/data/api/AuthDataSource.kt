package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.LoginRequestDTO

interface AuthDataSource {

    suspend fun login(request: LoginRequestDTO)
}