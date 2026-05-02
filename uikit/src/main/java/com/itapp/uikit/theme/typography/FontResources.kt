package com.itapp.uikit.theme.typography

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.itapp.uikit.R

object FontResources {

    @Composable
    fun interFont(): FontFamily {
        return FontFamily(
            Font(R.font.inter_regular, weight = FontWeight.Normal),
            Font(R.font.inter_medium, weight = FontWeight.Medium),
            Font(R.font.inter_semibold, weight = FontWeight.SemiBold),
        )
    }
}
