package com.itapp.auth_impl.domain.usecase

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.fake.FakeAuthRepository
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AuthUseCaseImplTest {

    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var useCase: AuthUseCaseImpl

    @BeforeTest
    fun setup() {
        fakeRepository = FakeAuthRepository()
        useCase = AuthUseCaseImpl(fakeRepository)
    }

    @Test
    fun `should call repository login with correct dto when run called`() = runTest {
        val loginDto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        useCase.run(AuthUseCase.Params(loginDto))

        assertEquals(1, fakeRepository.loginCalls.size)
        assertEquals(loginDto, fakeRepository.loginCalls[0])
    }

    @Test
    fun `should return success result when invoke succeeds`() = runTest {
        val loginDto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        val result = useCase(AuthUseCase.Params(loginDto))

        assertTrue(result.isSuccess)
    }

    @Test
    fun `should return failure result when invoke throws exception`() = runTest {
        fakeRepository.loginException = RuntimeException("Login failed")
        val loginDto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        val result = useCase(AuthUseCase.Params(loginDto))

        assertTrue(result.isFailure)
        assertEquals("Login failed", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should pass phone correctly when run called`() = runTest {
        val loginDto = LoginDto(
            phone = "+79999999999",
            password = "pass",
            smsCode = "0000"
        )

        useCase.run(AuthUseCase.Params(loginDto))

        assertEquals("+79999999999", fakeRepository.loginCalls[0].phone)
    }

    @Test
    fun `should pass password correctly when run called`() = runTest {
        val loginDto = LoginDto(
            phone = "+79001234567",
            password = "secretPassword",
            smsCode = "1234"
        )

        useCase.run(AuthUseCase.Params(loginDto))

        assertEquals("secretPassword", fakeRepository.loginCalls[0].password)
    }

    @Test
    fun `should pass smsCode correctly when run called`() = runTest {
        val loginDto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "9999"
        )

        useCase.run(AuthUseCase.Params(loginDto))

        assertEquals("9999", fakeRepository.loginCalls[0].smsCode)
    }
}
