package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.ValidateSmsCodeDto
import com.itapp.auth_impl.domain.repository.AuthRepository
import dev.zacsweers.metro.Inject

@Inject
class ValidateSmsCodeUseCaseImpl(
    private val authRepository: AuthRepository
) : ValidateSmsCodeUseCase() {

    override suspend fun run(input: Params) {
        authRepository.validateSmsCode(
            ValidateSmsCodeDto(
                phoneNumber = input.phoneNumber,
                smsCode = input.smsCode
            )
        )
    }
}
