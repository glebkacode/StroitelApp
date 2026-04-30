package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.repository.AuthRepository
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.eq
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidatePhoneNumberUseCaseImplTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: ValidatePhoneNumberUseCaseImpl

    @BeforeTest
    fun setup() {
        repository = mock<AuthRepository>()
        useCase = ValidatePhoneNumberUseCaseImpl(repository)
    }

    @Test
    fun `should call repository validatePhone with correct dto when run called`() = runTest {
        // Given
        everySuspend { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        // When
        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        // Then
        verifySuspend { repository.validatePhone(eq(validationDto)) }
    }

    @Test
    fun `should return success result when invoke succeeds`() = runTest {
        // Given
        everySuspend { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        // When
        val result = useCase(ValidatePhoneNumberUseCase.Params(validationDto))

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure result when invoke throws exception`() = runTest {
        // Given
        everySuspend { repository.validatePhone(any()) } throws RuntimeException("Validation failed")
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        // When
        val result = useCase(ValidatePhoneNumberUseCase.Params(validationDto))

        // Then
        assertTrue(result.isFailure)
        assertEquals("Validation failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should pass phoneNumber correctly when run called`() = runTest {
        // Given
        everySuspend { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79999999999",
            password = "pass"
        )

        // When
        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        // Then
        verifySuspend { repository.validatePhone(eq(validationDto)) }
    }

    @Test
    fun `should pass password correctly when run called`() = runTest {
        // Given
        everySuspend { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "secretPassword"
        )

        // When
        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        // Then
        verifySuspend { repository.validatePhone(eq(validationDto)) }
    }
}
