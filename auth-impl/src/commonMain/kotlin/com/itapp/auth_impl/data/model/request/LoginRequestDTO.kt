package com.itapp.auth_impl.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDTO(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("password")
    val password: String
)