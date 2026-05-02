package com.itapp.auth_impl.data.repository

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AuthRepositoryImplTest {

    private lateinit var dataSource: AuthDataSource
    private lateinit var repository: AuthRepositoryImpl

    @BeforeTest
    fun setup() {
        dataSource = mockk()
        repository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun `should call dataSource with correct request when validatePhone called`() = runTest {
        val captured = slot<ValidatePhoneRequestDto>()
        coEvery { dataSource.validatePhone(capture(captured)) } returns Unit
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        repository.validatePhone(dto)

        coVerify { dataSource.validatePhone(any()) }
        assertEquals("+79001234567", captured.captured.phoneNumber)
        assertEquals("password123", captured.captured.password)
    }

    @Test
    fun `should propagate exception when validatePhone dataSource throws`() = runTest {
        coEvery { dataSource.validatePhone(any()) } throws RuntimeException("Network error")
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val exception = assertFailsWith<RuntimeException> {
            repository.validatePhone(dto)
        }
        assertEquals("Network error", exception.message)
    }
}
