package com.itapp.shelves_render_api.shelf.horizontal

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface HorizontalShelfComponent : UiComponent {
    val model: HorizontalShelfModel

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            model: HorizontalShelfModel
        ): HorizontalShelfComponent
    }
}