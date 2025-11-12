package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.repository.AuthRepository
import dev.zacsweers.metro.Inject

@Inject
class AuthUseCaseImpl(
    private val authRepository: AuthRepository
) : AuthUseCase() {

    override suspend fun run(input: Params) {
        authRepository.login(dto = input.dto)
    }
}