package com.itapp.products_api

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import com.itapp.shelves_render_api.shelf.root.ShelvesRenderComponent
import kotlinx.coroutines.flow.StateFlow

interface ProductListComponent : UiComponent {
    val model: StateFlow<Model>
    val shelvesRenderComponent: ShelvesRenderComponent

    sealed interface Model {
        data object Loading : Model
        data object Content : Model
        data class Error(val throwable: Throwable) : Model
    }

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openProductDetails: (Long) -> Unit
        ): ProductListComponent
    }
}