package com.itapp.products_impl.presentation.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.itapp.products_api.ProductDetailsComponent

@Composable
fun ProductDetailsScreen(
    modifier: Modifier,
    component: ProductDetailsComponent
) {
    val model by component.models.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        TextField(
            value = model.text,
            onValueChange = { text ->
                component.onTextChanged(text)
            }
        )
    }
}