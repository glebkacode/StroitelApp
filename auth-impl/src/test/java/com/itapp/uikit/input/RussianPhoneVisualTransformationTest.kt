package com.itapp.uikit.input

import androidx.compose.ui.text.AnnotatedString
import org.junit.Test
import kotlin.test.assertEquals

class RussianPhoneVisualTransformationTest {

    private val transformation = RussianPhoneVisualTransformation()

    @Test
    fun `should produce empty transformed text when input is empty`() {
        val result = transformation.filter(AnnotatedString(""))

        assertEquals("", result.text.text)
        assertEquals(0, result.offsetMapping.originalToTransformed(0))
        assertEquals(0, result.offsetMapping.transformedToOriginal(0))
    }

    @Test
    fun `should format partial input with one digit`() {
        val result = transformation.filter(AnnotatedString("9"))

        assertEquals("+7 (9", result.text.text)
        assertEquals(4, result.offsetMapping.originalToTransformed(0))
        assertEquals(5, result.offsetMapping.originalToTransformed(1))
    }

    @Test
    fun `should append closing paren after three digits`() {
        val result = transformation.filter(AnnotatedString("988"))

        assertEquals("+7 (988)", result.text.text)
        assertEquals(8, result.offsetMapping.originalToTransformed(3))
    }

    @Test
    fun `should format full ten-digit phone`() {
        val result = transformation.filter(AnnotatedString("9889527628"))

        assertEquals("+7 (988)-952-76-28", result.text.text)
        assertEquals(18, result.offsetMapping.originalToTransformed(10))
    }

    @Test
    fun `should ignore digits beyond the tenth`() {
        val result = transformation.filter(AnnotatedString("98895276281234"))

        assertEquals("+7 (988)-952-76-28", result.text.text)
    }

    @Test
    fun `should strip non-digit characters before formatting`() {
        val result = transformation.filter(AnnotatedString("+7 (988) 952-76-28"))

        // 11 digits "79889527628", filter takes first 10 → "7988952762"
        assertEquals("+7 (798)-895-27-62", result.text.text)
    }

    @Test
    fun `should map original cursor positions to transformed positions for full input`() {
        val mapping = transformation.filter(AnnotatedString("9889527628")).offsetMapping

        assertEquals(4, mapping.originalToTransformed(0))
        assertEquals(5, mapping.originalToTransformed(1))
        assertEquals(6, mapping.originalToTransformed(2))
        assertEquals(9, mapping.originalToTransformed(3))
        assertEquals(10, mapping.originalToTransformed(4))
        assertEquals(11, mapping.originalToTransformed(5))
        assertEquals(13, mapping.originalToTransformed(6))
        assertEquals(14, mapping.originalToTransformed(7))
        assertEquals(16, mapping.originalToTransformed(8))
        assertEquals(17, mapping.originalToTransformed(9))
        assertEquals(18, mapping.originalToTransformed(10))
    }

    @Test
    fun `should map transformed cursor positions to original positions for full input`() {
        val mapping = transformation.filter(AnnotatedString("9889527628")).offsetMapping

        assertEquals(0, mapping.transformedToOriginal(0))
        assertEquals(0, mapping.transformedToOriginal(4))
        assertEquals(1, mapping.transformedToOriginal(5))
        assertEquals(2, mapping.transformedToOriginal(6))
        assertEquals(3, mapping.transformedToOriginal(7))
        assertEquals(3, mapping.transformedToOriginal(8))
        assertEquals(3, mapping.transformedToOriginal(9))
        assertEquals(4, mapping.transformedToOriginal(10))
        assertEquals(5, mapping.transformedToOriginal(11))
        assertEquals(6, mapping.transformedToOriginal(12))
        assertEquals(6, mapping.transformedToOriginal(13))
        assertEquals(7, mapping.transformedToOriginal(14))
        assertEquals(8, mapping.transformedToOriginal(15))
        assertEquals(8, mapping.transformedToOriginal(16))
        assertEquals(9, mapping.transformedToOriginal(17))
        assertEquals(10, mapping.transformedToOriginal(18))
    }

    @Test
    fun `should clamp transformed-to-original mapping to current digits length`() {
        val mapping = transformation.filter(AnnotatedString("988")).offsetMapping

        // Formatted is "+7 (988)" (length 8). Cursor positions 7..8 should land at end of digits (3).
        assertEquals(3, mapping.transformedToOriginal(7))
        assertEquals(3, mapping.transformedToOriginal(8))
    }
}

class StringToRussianPhoneDigitsTest {

    @Test
    fun `should keep already clean ten digits`() {
        assertEquals("9889527628", "9889527628".toRussianPhoneDigits())
    }

    @Test
    fun `should drop leading 7 from eleven-digit international format`() {
        assertEquals("9889527628", "+79889527628".toRussianPhoneDigits())
    }

    @Test
    fun `should drop leading 8 from eleven-digit national format`() {
        assertEquals("9889527628", "89889527628".toRussianPhoneDigits())
    }

    @Test
    fun `should strip formatting characters`() {
        assertEquals("9889527628", "+7 (988) 952-76-28".toRussianPhoneDigits())
    }

    @Test
    fun `should truncate input longer than ten digits when prefix is not 7 or 8`() {
        // 12 digits, no Russian prefix → take first 10
        assertEquals("1234567890", "123456789012".toRussianPhoneDigits())
    }

    @Test
    fun `should accept partial input`() {
        assertEquals("988", "988".toRussianPhoneDigits())
    }

    @Test
    fun `should return empty string for input without digits`() {
        assertEquals("", "() --".toRussianPhoneDigits())
    }
}
