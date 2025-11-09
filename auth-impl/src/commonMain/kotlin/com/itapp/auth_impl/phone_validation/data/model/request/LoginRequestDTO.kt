package com.itapp.auth_impl.phone_validation.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequestDTO(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("password")
    val password: String
)