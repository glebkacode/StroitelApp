package com.itapp.products_impl.presentation.details

import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.products_impl.presentation.details.ProductDetailsTea.Effect
import com.itapp.products_impl.presentation.details.ProductDetailsTea.Intent
import com.itapp.products_impl.presentation.details.ProductDetailsTea.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun TeaFactory.productDetailsTea(
    mainCoroutineContext: CoroutineContext
): ProductDetailsTea =
    object : ProductDetailsTea, Tea<State, Intent, Unit> by create(
        initialState = State(),
        effectors = listOf(ProductDetailsEffector()),
        dispatcher = mainCoroutineContext,
        reducer = ProductDetailReducer()
    ) {}

private class ProductDetailsEffector : CoroutineEffector<Effect, Intent, Unit>() {

    override fun onEffect(effect: Effect) {
        when (effect) {
            is Effect.LogTextChange -> { onLogTextChange(effect.text) }
        }
    }

    private fun onLogTextChange(text: String) {
        scope.launch {
            println("Text msg = $text")
            delay(3000L)
            dispatch(Intent.EffectIntent.Complete)
        }
    }
}

private class ProductDetailReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        when (intent) {
            is Intent.UiIntent -> reduceUiIntent(intent)
            is Intent.EffectIntent -> reduceEffectIntent(intent)
        }
    }

    private fun ReducerContext<State, Effect>.reduceUiIntent(intent: Intent.UiIntent) {
        when (intent) {
            is Intent.UiIntent.TextChanged -> state { copy(intent.text) }
        }
    }

    private fun ReducerContext<State, Effect>.reduceEffectIntent(intent: Intent.EffectIntent) {
        when (intent) {
            Intent.EffectIntent.Complete -> effects(emptyList())
        }
    }
}