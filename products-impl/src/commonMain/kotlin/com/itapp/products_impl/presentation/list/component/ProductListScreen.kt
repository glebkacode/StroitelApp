package com.itapp.products_impl.presentation.list.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.itapp.products_api.ProductListComponent

@Composable
fun ProductListScreen(
    modifier: Modifier,
    component: ProductListComponent
) {
    val model by component.model.collectAsState()
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        when (model) {
            ProductListComponent.Model.Content -> Content(component)
            is ProductListComponent.Model.Error -> Error()
            ProductListComponent.Model.Loading -> Progress()
        }
    }
}

@Composable
fun Progress() {

}

@Composable
fun Content(component: ProductListComponent) {
    component.shelvesRenderComponent.render(Modifier)
}

@Composable
fun Error() {

}