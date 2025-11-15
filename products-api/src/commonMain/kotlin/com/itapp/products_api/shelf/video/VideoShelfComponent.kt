package com.itapp.products_api.shelf.video

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface VideoShelfComponent : UiComponent {
    val index: Int
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            index: Int
        ): VideoShelfComponent
    }
}