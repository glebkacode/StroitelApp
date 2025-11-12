package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.usecase.AuthUseCase.Params
import com.itapp.core_architecture.BaseCoroutineUseCase

abstract class AuthUseCase : BaseCoroutineUseCase<Params, Unit>() {
    data class Params(val dto: LoginDto)
}