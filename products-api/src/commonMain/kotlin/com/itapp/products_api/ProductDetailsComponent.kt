package com.itapp.products_api

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface ProductDetailsComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): ProductDetailsComponent
    }
}