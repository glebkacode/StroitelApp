package com.itapp.auth_impl.data.model.mapping

import com.itapp.auth_impl.data.model.request.LoginRequestDTO
import com.itapp.auth_impl.domain.model.LoginDTO

internal fun LoginDTO.toRequest(): LoginRequestDTO {
    return LoginRequestDTO(
        phoneNumber = phoneNumber,
        password = password
    )
}