package com.itapp.uikit.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val LocalColorScheme = staticCompositionLocalOf { SdsDefaultColorScheme }

@Immutable
data class SdsColorScheme(
    val text: SdsTextColor
)

val SdsDefaultColorScheme = SdsColorScheme(
    text = SdsTextColor(
        moscow = Color(0xFFEE7100),
        piter = Color.White,
        transparent = Color.Transparent
    )
)