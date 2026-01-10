package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.fake.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidatePhoneNumberUseCaseImplTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var useCase: ValidatePhoneNumberUseCaseImpl

    @BeforeTest
    fun setup() {
        fakeRepository = FakeAuthRepository()
        useCase = ValidatePhoneNumberUseCaseImpl(fakeRepository)
    }

    @Test
    fun `should call repository validatePhone with correct dto when run called`() = runTest {
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        assertEquals(1, fakeRepository.validatePhoneCalls.size)
        assertEquals(validationDto, fakeRepository.validatePhoneCalls[0])
    }

    @Test
    fun `should return success result when invoke succeeds`() = runTest {
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val result = useCase(ValidatePhoneNumberUseCase.Params(validationDto))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure result when invoke throws exception`() = runTest {
        fakeRepository.validatePhoneException = RuntimeException("Validation failed")
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
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79999999999",
            password = "pass"
        )

        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        assertEquals("+79999999999", fakeRepository.validatePhoneCalls[0].phoneNumber)
    }

    @Test
    fun `should pass password correctly when run called`() = runTest {
        val validationDto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "secretPassword"
        )

        useCase.run(ValidatePhoneNumberUseCase.Params(validationDto))

        assertEquals("secretPassword", fakeRepository.validatePhoneCalls[0].password)
    }
}
