package com.itapp.auth_impl.domain.model

data class ValidateSmsCodeDto(
    val phoneNumber: String,
    val smsCode: String
)
