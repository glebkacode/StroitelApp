package com.itapp.products_impl.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.itapp.products_api.RootProductsComponent

@Composable
fun RootProductsScreen(
    modifier: Modifier,
    component: RootProductsComponent
) {
    Children(component.stack) {
        when (val child = it.instance) {
            is RootProductsComponent.Child.ProductDetails -> child.component.render(modifier)
            is RootProductsComponent.Child.ProductsList -> child.component.render(modifier)
        }
    }
}