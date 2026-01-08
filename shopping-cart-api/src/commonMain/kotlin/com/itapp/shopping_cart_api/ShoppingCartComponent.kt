package com.itapp.shopping_cart_api

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import kotlinx.coroutines.flow.StateFlow

interface ShoppingCartComponent : UiComponent {

    val uiState: StateFlow<UiState>

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
        ): ShoppingCartComponent
    }

    data class UiState(
        val isEmpty: Boolean = true
    )
}