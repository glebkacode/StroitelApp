package com.itapp.auth_impl.data.model.mapping

import com.itapp.auth_impl.data.model.request.LoginRequestDto
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.data.model.request.ValidateSmsCodeRequestDto
import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidateSmsCodeDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto

internal fun ValidationPhoneDto.toRequest(): ValidatePhoneRequestDto {
    return ValidatePhoneRequestDto(
        phoneNumber = phoneNumber,
        password = password
    )
}

internal fun LoginDto.toLoginRequest(): LoginRequestDto {
    return LoginRequestDto(
        phoneNumber = phoneNumber,
        password = password
    )
}

internal fun ValidateSmsCodeDto.toRequest(): ValidateSmsCodeRequestDto {
    return ValidateSmsCodeRequestDto(
        phoneNumber = phoneNumber,
        smsCode = smsCode
    )
}
