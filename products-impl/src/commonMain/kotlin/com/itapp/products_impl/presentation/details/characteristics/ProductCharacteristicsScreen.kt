package com.itapp.products_impl.presentation.details.characteristics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itapp.products_api.details.ProductCharacteristicsComponent

@Composable
fun ProductCharacteristicsScreen(
    modifier: Modifier,
    component: ProductCharacteristicsComponent
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Characteristics")
    }
}