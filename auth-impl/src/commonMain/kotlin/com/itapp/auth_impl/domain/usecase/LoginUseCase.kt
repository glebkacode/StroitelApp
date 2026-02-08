package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.usecase.LoginUseCase.Params
import com.itapp.core_architecture.BaseCoroutineUseCase

abstract class LoginUseCase : BaseCoroutineUseCase<Params, Unit>() {
    data class Params(
        val loginDto: LoginDto
    )
}
