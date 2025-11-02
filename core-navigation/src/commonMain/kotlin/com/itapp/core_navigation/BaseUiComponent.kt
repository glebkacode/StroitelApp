package com.itapp.core_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface BaseUiComponent {
    @Composable
    fun render(modifier: Modifier = Modifier)
}