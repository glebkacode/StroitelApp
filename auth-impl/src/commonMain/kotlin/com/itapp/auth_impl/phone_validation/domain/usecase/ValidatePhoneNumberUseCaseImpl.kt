package com.itapp.auth_impl.phone_validation.domain.usecase

import com.itapp.auth_impl.phone_validation.domain.model.LoginDTO
import com.itapp.auth_impl.phone_validation.domain.repository.AuthRepository

internal class ValidatePhoneNumberUseCaseImpl(
    private val authRepository: AuthRepository
) : ValidatePhoneNumberUseCase() {

    override suspend fun run(dto: LoginDTO) {
        authRepository.login(dto)
    }
}