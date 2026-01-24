package com.itapp.shopping_cart_impl.presentation.mvi

import com.itapp.core_architecture.tea.Tea

internal interface ShoppingCartTea : Tea<ShoppingCartTea.State, ShoppingCartTea.Intent, ShoppingCartTea.Event> {

    sealed interface Intent

    sealed interface Effect

    sealed interface Event

    data class State(
        val isEmpty: Boolean = true
    )
}
