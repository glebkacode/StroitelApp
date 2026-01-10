package com.itapp.auth_impl.data.model.mapping

import com.itapp.auth_impl.domain.model.LoginDto
import com.itapp.auth_impl.domain.model.ValidationPhoneDto
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestMappingTest {

    @Test
    fun `should map phoneNumber correctly when ValidationPhoneDto toRequest called`() {
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val request = dto.toRequest()

        assertEquals("+79001234567", request.phoneNumber)
    }

    @Test
    fun `should map password correctly when ValidationPhoneDto toRequest called`() {
        val dto = ValidationPhoneDto(
            phoneNumber = "+79001234567",
            password = "password123"
        )

        val request = dto.toRequest()

        assertEquals("password123", request.password)
    }

    @Test
    fun `should map phone correctly when LoginDto toRequest called`() {
        val dto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        val request = dto.toRequest()

        assertEquals("+79001234567", request.phone)
    }

    @Test
    fun `should map password correctly when LoginDto toRequest called`() {
        val dto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        val request = dto.toRequest()

        assertEquals("password123", request.password)
    }

    @Test
    fun `should map smsCode correctly when LoginDto toRequest called`() {
        val dto = LoginDto(
            phone = "+79001234567",
            password = "password123",
            smsCode = "1234"
        )

        val request = dto.toRequest()

        assertEquals("1234", request.smsCode)
    }
}
