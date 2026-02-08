package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.fake.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginUseCaseImplTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var useCase: LoginUseCaseImpl

    @BeforeTest
    fun setup() {
        fakeRepository = FakeAuthRepository()
        useCase = LoginUseCaseImpl(fakeRepository)
    }

    @Test
    fun `should call repository login with correct dto when run called`() = runTest {
        val loginDto = LoginDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        useCase.run(LoginUseCase.Params(loginDto))

        assertEquals(1, fakeRepository.loginCalls.size)
        assertEquals(loginDto, fakeRepository.loginCalls[0])
    }

    @Test
    fun `should return success result when invoke succeeds`() = runTest {
        val loginDto = LoginDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val result = useCase(LoginUseCase.Params(loginDto))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure result when invoke throws exception`() = runTest {
        fakeRepository.loginException = RuntimeException("Login failed")
        val loginDto = LoginDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val result = useCase(LoginUseCase.Params(loginDto))

        assertTrue(result.isFailure)
        assertEquals("Login failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should pass phoneNumber correctly when run called`() = runTest {
        val loginDto = LoginDto(
            phoneNumber = "+79999999999",
            password = "pass"
        )

        useCase.run(LoginUseCase.Params(loginDto))

        assertEquals("+79999999999", fakeRepository.loginCalls[0].phoneNumber)
    }

    @Test
    fun `should pass password correctly when run called`() = runTest {
        val loginDto = LoginDto(
            phoneNumber = "+79001234567",
            password = "secretPassword"
        )

        useCase.run(LoginUseCase.Params(loginDto))

        assertEquals("secretPassword", fakeRepository.loginCalls[0].password)
    }
}
