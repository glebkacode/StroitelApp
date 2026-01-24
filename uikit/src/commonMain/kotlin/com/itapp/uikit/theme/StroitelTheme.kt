package com.itapp.uikit.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.itapp.uikit.theme.color.LocalColorScheme
import com.itapp.uikit.theme.color.SdsColorScheme
import com.itapp.uikit.theme.typography.FontResources
import com.itapp.uikit.theme.typography.createTypography

@Composable
fun StroitelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Загружаем шрифты
    val fontFamily = FontResources.interFont()

    // Создаем типографию с нашими шрифтами
    val typography = createTypography(fontFamily)

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = typography,
        content = content
    )
}

object StroitelTheme {

    val colorScheme: SdsColorScheme
        @Composable @ReadOnlyComposable get() = LocalColorScheme.current
}