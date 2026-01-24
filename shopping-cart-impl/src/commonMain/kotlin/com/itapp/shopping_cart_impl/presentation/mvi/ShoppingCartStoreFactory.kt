package com.itapp.shopping_cart_impl.presentation.mvi

import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea.Effect
import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea.Event
import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea.Intent
import com.itapp.shopping_cart_impl.presentation.mvi.ShoppingCartTea.State
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.shoppingCartTea(
    mainContext: CoroutineContext,
): ShoppingCartTea =
    object : ShoppingCartTea, Tea<State, Intent, Event> by create(
        initialState = State(),
        dispatcher = mainContext,
        effectors = listOf(ShoppingCartEffector(mainContext)),
        reducer = ShoppingCartReducer(),
    ) {}

private class ShoppingCartEffector(
    mainContext: CoroutineContext
) : CoroutineEffector<Effect, Intent, Event>(mainContext) {

    override fun onEffect(effect: Effect) {
        // Handle effects here
    }
}

private class ShoppingCartReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        // Handle intents here
    }
}
