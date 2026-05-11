package com.itapp.uikit.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val PlaceholderBackground = Color(0xFFE0E0E0)

@Composable
fun ImagePlaceholder(modifier: Modifier = Modifier) {
    Box(modifier = modifier.background(PlaceholderBackground))
}
