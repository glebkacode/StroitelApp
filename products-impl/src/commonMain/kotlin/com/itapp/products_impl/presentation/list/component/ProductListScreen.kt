package com.itapp.products_impl.presentation.list.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.itapp.products_api.ProductListComponent

@Composable
fun ProductListScreen(
    modifier: Modifier,
    component: ProductListComponent
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        component.shelvesRenderComponent.render(Modifier)
    }
}