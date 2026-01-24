package com.itapp.auth_impl.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(
    @SerialName("phone")
    val phone: String,
    @SerialName("password")
    val password: String,
    @SerialName("smsCode")
    val smsCode: String
)