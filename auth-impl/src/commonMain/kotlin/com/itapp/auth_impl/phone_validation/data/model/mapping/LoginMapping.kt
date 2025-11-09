package com.itapp.auth_impl.phone_validation.data.model.mapping

import com.itapp.auth_impl.phone_validation.data.model.request.LoginRequestDTO
import com.itapp.auth_impl.phone_validation.domain.model.LoginDTO

internal fun LoginDTO.toRequest(): LoginRequestDTO {
    return LoginRequestDTO(
        phoneNumber = phoneNumber,
        password = password
    )
}