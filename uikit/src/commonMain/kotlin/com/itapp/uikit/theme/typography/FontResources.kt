package com.itapp.uikit.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import stroitelapp.uikit.generated.resources.Inter_Medium
import stroitelapp.uikit.generated.resources.Inter_Regular
import stroitelapp.uikit.generated.resources.Inter_SemiBold
import stroitelapp.uikit.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
object FontResources {

    @Composable
    fun interFont(): FontFamily {
        return FontFamily(
            Font(
                resource = Res.font.Inter_Regular,
                weight = FontWeight.Companion.Normal
            ),
            Font(
                resource = Res.font.Inter_Medium,
                weight = FontWeight.Companion.Medium
            ),
            Font(
                resource = Res.font.Inter_SemiBold,
                weight = FontWeight.Companion.SemiBold
            )
        )
    }
}