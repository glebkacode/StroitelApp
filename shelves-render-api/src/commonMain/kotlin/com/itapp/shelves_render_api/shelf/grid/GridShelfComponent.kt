package com.itapp.shelves_render_api.shelf.grid

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface GridShelfComponent : UiComponent {
    val index: Int
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            index: Int
        ): GridShelfComponent
    }
}