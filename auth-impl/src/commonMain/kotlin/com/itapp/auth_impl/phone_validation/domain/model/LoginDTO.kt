package com.itapp.auth_impl.phone_validation.domain.model

data class LoginDTO(
    val phoneNumber: String,
    val password: String
)