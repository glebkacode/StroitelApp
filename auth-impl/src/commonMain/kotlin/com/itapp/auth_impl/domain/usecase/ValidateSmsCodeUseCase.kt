package com.itapp.auth_impl.domain.usecase

import com.itapp.core_architecture.BaseCoroutineUseCase

abstract class ValidateSmsCodeUseCase : BaseCoroutineUseCase<ValidateSmsCodeUseCase.Params, Unit>() {
    data class Params(
        val phoneNumber: String,
        val smsCode: String
    )
}
