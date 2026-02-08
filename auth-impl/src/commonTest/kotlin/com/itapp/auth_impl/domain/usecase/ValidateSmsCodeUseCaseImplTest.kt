package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.fake.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidateSmsCodeUseCaseImplTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var useCase: ValidateSmsCodeUseCaseImpl

    @BeforeTest
    fun setUp() {
        fakeRepository = FakeAuthRepository()
        useCase = ValidateSmsCodeUseCaseImpl(fakeRepository)
    }

    @Test
    fun `should call repository with correct parameters when invoked`() = runTest {
        val params = ValidateSmsCodeUseCase.Params(
            phoneNumber = "+79001234567",
            smsCode = "1234"
        )

        useCase(params)

        assertEquals(1, fakeRepository.validateSmsCodeCalls.size)
        assertEquals("+79001234567", fakeRepository.validateSmsCodeCalls[0].phoneNumber)
        assertEquals("1234", fakeRepository.validateSmsCodeCalls[0].smsCode)
    }

    @Test
    fun `should return success when repository succeeds`() = runTest {
        val params = ValidateSmsCodeUseCase.Params(
            phoneNumber = "+79001234567",
            smsCode = "1234"
        )

        val result = useCase(params)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure when repository throws exception`() = runTest {
        val exception = RuntimeException("Invalid SMS code")
        fakeRepository.validateSmsCodeException = exception

        val params = ValidateSmsCodeUseCase.Params(
            phoneNumber = "+79001234567",
            smsCode = "0000"
        )

        val result = useCase(params)

        assertTrue(result.isFailure)
        assertEquals("Invalid SMS code", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should pass different sms codes correctly`() = runTest {
        val codes = listOf("1234", "5678", "0000", "9999")

        codes.forEach { code ->
            fakeRepository.reset()
            val params = ValidateSmsCodeUseCase.Params(
                phoneNumber = "+79001234567",
                smsCode = code
            )

            useCase(params)

            assertEquals(code, fakeRepository.validateSmsCodeCalls[0].smsCode)
        }
    }

    @Test
    fun `should pass different phone numbers correctly`() = runTest {
        val phoneNumbers = listOf("+79001234567", "+79009876543", "+12025551234")

        phoneNumbers.forEach { phone ->
            fakeRepository.reset()
            val params = ValidateSmsCodeUseCase.Params(
                phoneNumber = phone,
                smsCode = "1234"
            )

            useCase(params)

            assertEquals(phone, fakeRepository.validateSmsCodeCalls[0].phoneNumber)
        }
    }
}
