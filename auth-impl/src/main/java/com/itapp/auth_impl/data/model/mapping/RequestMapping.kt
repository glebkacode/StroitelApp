package com.itapp.auth_impl.data.model.mapping

import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto

internal fun ValidationPhoneDto.toRequest(): ValidatePhoneRequestDto {
    return ValidatePhoneRequestDto(
        phoneNumber = phoneNumber,
        password = password
    )
}
