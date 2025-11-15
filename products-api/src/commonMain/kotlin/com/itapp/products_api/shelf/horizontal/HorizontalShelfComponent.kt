package com.itapp.products_api.shelf.horizontal

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface HorizontalShelfComponent : UiComponent {
    val index: Int
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            index: Int
        ): HorizontalShelfComponent
    }
}