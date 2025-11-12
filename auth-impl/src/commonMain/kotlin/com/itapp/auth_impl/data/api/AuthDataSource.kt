package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDTO

interface AuthDataSource {

    suspend fun validatePhone(request: ValidatePhoneRequestDTO)
    suspend fun login(request: LoginRequestDto)
}