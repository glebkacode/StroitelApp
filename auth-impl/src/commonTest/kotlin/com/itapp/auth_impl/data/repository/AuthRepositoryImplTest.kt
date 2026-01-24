package com.itapp.auth_impl.data.repository

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import com.itapp.auth_impl.fake.FakeAuthDataSource
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthRepositoryImplTest {

    private lateinit var fakeDataSource: FakeAuthDataSource
    private lateinit var repository: AuthRepositoryImpl

    @BeforeTest
    fun setup() {
        fakeDataSource = FakeAuthDataSource()
        repository = AuthRepositoryImpl(fakeDataSource)
    }

    @Test
    fun `should call dataSource with correct request when validatePhone called`() = runTest {
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        repository.validatePhone(dto)

        assertEquals(1, fakeDataSource.validatePhoneCalls.size)
        assertEquals("+79001234567", fakeDataSource.validatePhoneCalls[0].phoneNumber)
        assertEquals("password123", fakeDataSource.validatePhoneCalls[0].password)
    }

    @Test
    fun `should propagate exception when validatePhone dataSource throws`() = runTest {
        val expectedException = RuntimeException("Network error")
        fakeDataSource.validatePhoneException = expectedException

        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val exception = assertFailsWith<RuntimeException> {
            repository.validatePhone(dto)
        }

        assertEquals("Network error", exception.message)
    }

    @Test
    fun `should call dataSource with correct request when login called`() = runTest {
        val dto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        repository.login(dto)

        assertEquals(1, fakeDataSource.loginCalls.size)
        assertEquals("+79001234567", fakeDataSource.loginCalls[0].phone)
        assertEquals("password123", fakeDataSource.loginCalls[0].password)
        assertEquals("1234", fakeDataSource.loginCalls[0].smsCode)
    }

    @Test
    fun `should propagate exception when login dataSource throws`() = runTest {
        val expectedException = RuntimeException("Auth failed")
        fakeDataSource.loginException = expectedException

        val dto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        val exception = assertFailsWith<RuntimeException> {
            repository.login(dto)
        }

        assertEquals("Auth failed", exception.message)
    }
}
