package com.itapp.uikit.input

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Маска ввода российского телефона в формате `+7 (XXX)-XXX-XX-XX`.
 *
 * Источник истины (`TextField.value`) — строка из одних цифр длиной 0..10
 * (часть номера после `+7`). Префикс `+7`, скобки и дефисы добавляются
 * только при отображении и не попадают в state.
 *
 * Перед записью значения в state экран должен очистить ввод вызовом
 * [String.toRussianPhoneDigits], чтобы корректно обработать вставку
 * полного номера (`+79001234567`, `89001234567`).
 */
class RussianPhoneVisualTransformation : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit).take(MAX_DIGITS)
        return TransformedText(
            text = AnnotatedString(format(digits)),
            offsetMapping = PhoneOffsetMapping(digits.length),
        )
    }
}

/**
 * Очищает произвольный ввод до 10 цифр номера (без `+7`).
 *
 * Если на вход пришли 11 цифр с ведущей `7` или `8` (типичный результат вставки
 * полного номера) — ведущая цифра отбрасывается. Иначе берутся первые 10 цифр.
 */
fun String.toRussianPhoneDigits(): String {
    val digits = filter(Char::isDigit)
    val withoutCountryCode = if (
        digits.length == MAX_DIGITS + 1 && (digits.first() == '7' || digits.first() == '8')
    ) {
        digits.drop(1)
    } else {
        digits
    }
    return withoutCountryCode.take(MAX_DIGITS)
}

private const val MAX_DIGITS = 10

private fun format(digits: String): String {
    if (digits.isEmpty()) return ""
    return buildString {
        append("+7 (")
        append(digits, 0, minOf(3, digits.length))
        if (digits.length >= 3) append(')')
        if (digits.length > 3) {
            append('-')
            append(digits, 3, minOf(6, digits.length))
        }
        if (digits.length > 6) {
            append('-')
            append(digits, 6, minOf(8, digits.length))
        }
        if (digits.length > 8) {
            append('-')
            append(digits, 8, minOf(10, digits.length))
        }
    }
}

/**
 * Маппинг смещений курсора между «сырым» значением (только цифры) и отображаемым
 * текстом с маской. Ширина маски зависит от количества введённых цифр, поэтому
 * mapping строится под конкретную длину ввода [digitsLength].
 */
private class PhoneOffsetMapping(private val digitsLength: Int) : OffsetMapping {

    override fun originalToTransformed(offset: Int): Int {
        if (digitsLength == 0) return 0
        val o = offset.coerceIn(0, digitsLength)
        return when {
            o == 0 -> DIGIT_POSITIONS[0]
            o < digitsLength -> DIGIT_POSITIONS[o]
            else -> FORMATTED_LENGTHS[digitsLength]
        }
    }

    override fun transformedToOriginal(offset: Int): Int {
        if (digitsLength == 0) return 0
        for ((maxTransformedOffset, originalOffset) in TRANSFORMED_TO_ORIGINAL) {
            if (offset <= maxTransformedOffset) return minOf(originalOffset, digitsLength)
        }
        return digitsLength
    }
}

private val DIGIT_POSITIONS = intArrayOf(4, 5, 6, 9, 10, 11, 13, 14, 16, 17)
private val FORMATTED_LENGTHS = intArrayOf(0, 5, 6, 8, 10, 11, 12, 14, 15, 17, 18)
private val TRANSFORMED_TO_ORIGINAL = listOf(
    4 to 0,
    5 to 1,
    6 to 2,
    9 to 3,
    10 to 4,
    11 to 5,
    13 to 6,
    14 to 7,
    16 to 8,
    17 to 9,
    18 to 10,
)
