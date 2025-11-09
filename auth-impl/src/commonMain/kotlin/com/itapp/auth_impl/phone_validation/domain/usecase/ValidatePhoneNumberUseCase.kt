package com.itapp.auth_impl.phone_validation.domain.usecase

import com.itapp.auth_impl.phone_validation.domain.model.LoginDTO
import com.itapp.core_architecture.BaseCoroutineUseCase

internal abstract class ValidatePhoneNumberUseCase : BaseCoroutineUseCase<LoginDTO, Unit>() {
    internal data class Params(
        val loginDTO: LoginDTO
    )
}