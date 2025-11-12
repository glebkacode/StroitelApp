package com.itapp.auth_impl.domain.model

data class LoginDto(
    val phone: String,
    val password: String,
    val smsCode: String
)