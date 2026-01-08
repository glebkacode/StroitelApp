package com.itapp.shopping_cart_impl.presentation.mapping

import com.itapp.shopping_cart_api.ShoppingCartComponent
import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea

internal fun ShoppingCartTea.State.toUi(): ShoppingCartComponent.UiState =
    ShoppingCartComponent.UiState(
        isEmpty = isEmpty
    )
