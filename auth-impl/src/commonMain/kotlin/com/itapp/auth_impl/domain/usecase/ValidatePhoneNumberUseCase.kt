package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase.Params
import com.itapp.core_architecture.BaseCoroutineUseCase

abstract class ValidatePhoneNumberUseCase : BaseCoroutineUseCase<Params, Unit>() {
    data class Params(
        val validationPhoneDto: ValidationPhoneDto
    )
}