package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.repository.AuthRepository
import dev.zacsweers.metro.Inject

@Inject
class ValidatePhoneNumberUseCaseImpl(
    private val authRepository: AuthRepository
) : ValidatePhoneNumberUseCase() {

    override suspend fun run(input: Params) {
        authRepository.login(input.loginDTO)
    }
}