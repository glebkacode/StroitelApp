package com.itapp.auth_impl.data.repository

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
        // Given
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        // When
        repository.validatePhone(dto)

        // Then
        assertEquals(1, fakeDataSource.validatePhoneCalls.size)
        assertEquals("+79001234567", fakeDataSource.validatePhoneCalls[0].phoneNumber)
        assertEquals("password123", fakeDataSource.validatePhoneCalls[0].password)
    }

    @Test
    fun `should propagate exception when validatePhone dataSource throws`() = runTest {
        // Given
        val expectedException = RuntimeException("Network error")
        fakeDataSource.validatePhoneException = expectedException
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        // When / Then
        val exception = assertFailsWith<RuntimeException> {
            repository.validatePhone(dto)
        }
        assertEquals("Network error", exception.message)
    }
}
