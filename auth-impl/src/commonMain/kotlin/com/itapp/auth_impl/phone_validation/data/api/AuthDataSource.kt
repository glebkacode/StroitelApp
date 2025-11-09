package com.itapp.auth_impl.phone_validation.data.api

import com.itapp.auth_impl.phone_validation.data.model.request.LoginRequestDTO

internal interface AuthDataSource {

    suspend fun login(request: LoginRequestDTO)
}