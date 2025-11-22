package com.itapp.shelves_render_api.shelf.grid

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface GridShelfComponent : UiComponent {
    val model: GridShelfModel
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            model: GridShelfModel
        ): GridShelfComponent
    }
}