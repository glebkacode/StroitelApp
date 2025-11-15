package com.itapp.core_navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface UiComponent {
    @Composable
    fun render(modifier: Modifier = Modifier)
}