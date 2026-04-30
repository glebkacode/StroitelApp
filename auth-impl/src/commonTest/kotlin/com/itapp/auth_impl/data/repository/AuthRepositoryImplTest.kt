package com.itapp.auth_impl.data.repository

import com.itapp.auth_impl.data.api.AuthDataSource
import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matching
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
        dataSource = mock<AuthDataSource>()
        repository = AuthRepositoryImpl(dataSource)
    }

    @Test
    fun `should call dataSource with correct request when validatePhone called`() = runTest {
        // Given
        everySuspend { dataSource.validatePhone(any()) } returns Unit
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        // When
        repository.validatePhone(dto)

        // Then
        verifySuspend {
            dataSource.validatePhone(
                matching<ValidatePhoneRequestDto> { request ->
                    request.phoneNumber == "+79001234567" && request.password == "password123"
                }
            )
        }
    }

    @Test
    fun `should propagate exception when validatePhone dataSource throws`() = runTest {
        // Given
        everySuspend { dataSource.validatePhone(any()) } throws RuntimeException("Network error")
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
