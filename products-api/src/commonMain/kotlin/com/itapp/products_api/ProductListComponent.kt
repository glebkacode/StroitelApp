package com.itapp.products_api

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent

interface ProductListComponent : UiComponent {
    val shelvesRenderComponent: ShelvesRenderComponent
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openProductDetails: (Long) -> Unit
        ): ProductListComponent
    }
}