package com.itapp.shopping_cart_api

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface ShoppingCartComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): ShoppingCartComponent
    }
}