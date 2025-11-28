package com.itapp.products_impl.presentation.details.description

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import com.itapp.products_api.details.ProductDescriptionComponent

@Composable
fun ProductDescriptionScreen(
    modifier: Modifier,
    component: ProductDescriptionComponent
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Description")
    }
}