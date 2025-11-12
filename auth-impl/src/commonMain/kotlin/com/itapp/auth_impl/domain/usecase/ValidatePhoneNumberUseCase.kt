package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.LoginDTO
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase.Params
import com.itapp.core_architecture.BaseCoroutineUseCase

abstract class ValidatePhoneNumberUseCase : BaseCoroutineUseCase<Params, Unit>() {
    data class Params(
        val loginDTO: LoginDTO
    )
}