package com.itapp.auth_impl.data.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValidateSmsCodeRequestDto(
    @SerialName("phoneNumber")
    val phoneNumber: String,
    @SerialName("smsCode")
    val smsCode: String
)
