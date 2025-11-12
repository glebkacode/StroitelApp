package com.itapp.auth_impl.data.model.mapping

import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDTO
import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto

internal fun ValidationPhoneDto.toRequest(): ValidatePhoneRequestDTO {
    return ValidatePhoneRequestDTO(
        phoneNumber = phoneNumber,
        password = password
    )
}

internal fun LoginDto.toRequest(): LoginRequestDto {
    return LoginRequestDto(
        phone = phone,
        password = password,
        smsCode = smsCode
    )
}