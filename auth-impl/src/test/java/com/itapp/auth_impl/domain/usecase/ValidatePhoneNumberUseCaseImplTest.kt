package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.domain.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
        repository = mockk()
        useCase = ValidatePhoneNumberUseCaseImpl(repository)
    }

    @Test
    fun `should call repository validatePhone with correct dto when run called`() = runTest {
        coEvery { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        coVerify { repository.validatePhone(validationDto) }
    }

    @Test
    fun `should return success result when invoke succeeds`() = runTest {
        coEvery { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val result = useCase(ValidatePhoneNumberUseCase.Params(validationDto))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure result when invoke throws exception`() = runTest {
        coEvery { repository.validatePhone(any()) } throws RuntimeException("Validation failed")
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val result = useCase(ValidatePhoneNumberUseCase.Params(validationDto))

        assertTrue(result.isFailure)
        assertEquals("Validation failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should pass phoneNumber correctly when run called`() = runTest {
        coEvery { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79999999999",
            password = "pass"
        )

        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        coVerify { repository.validatePhone(validationDto) }
    }

    @Test
    fun `should pass password correctly when run called`() = runTest {
        coEvery { repository.validatePhone(any()) } returns Unit
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "secretPassword"
        )

        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        coVerify { repository.validatePhone(validationDto) }
    }
}
