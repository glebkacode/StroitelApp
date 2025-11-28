package com.itapp.products_impl.presentation.list.mvi

import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.Effect
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.Event
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.Intent
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.State
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.productsTea(
    getShelvesUseCase: GetShelvesUseCase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): ProductsTea =
    object : ProductsTea, Tea<State, Intent, Event> by create(
        initialState = State.Loading,
        dispatcher = mainContext,
        effector = ProductsEffector(getShelvesUseCase, mainContext, ioContext),
        reducer = ProductsReducer()
    ) {}


private class ProductsEffector(
    private val getShelvesUseCase: GetShelvesUseCase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext
) : CoroutineEffector<Effect, Intent, Event>(mainContext) {

    override fun onEffect(effect: Effect) {
        when (effect) {
            Effect.GetShelves -> getShelves()
            is Effect.ShelfItemClicked -> onShelfItemClicked(
                shelfId = effect.shelfId,
                shelfItemId = effect.shelfItemId
            )
        }
    }

    private fun getShelves() {
        scope.launch(ioContext) {
            getShelvesUseCase(Unit).fold(
                onSuccess = { shelves ->
                    withContext(mainContext) {
                        dispatch(Intent.EffectIntent.GetShelvesCompleted(shelves))
                    }
                },
                onFailure = { throwable ->
                    dispatch(Intent.EffectIntent.GetShelvesFailed(throwable))
                }
            )
        }
    }

    private fun onShelfItemClicked(shelfId: Long, shelfItemId: Long) {
        publish(Event.OpenProductDetails(shelfId, shelfItemId))
    }
}

private class ProductsReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        when (intent) {
            is Intent.UiIntent -> reduceUiIntent(intent)
            is Intent.EffectIntent -> reduceEffectIntent(intent)
        }
    }

    private fun ReducerContext<State, Effect>.reduceUiIntent(intent: Intent.UiIntent) {
        when (intent) {
            Intent.UiIntent.Refresh -> {
                effects(Effect.GetShelves)
            }

            is Intent.UiIntent.ShelfItemClicked -> {
                effects(
                    Effect.ShelfItemClicked(
                        shelfId = intent.shelfId,
                        shelfItemId = intent.shelfItemId
                    )
                )
            }

            Intent.UiIntent.InitShelves -> {
                effects(Effect.GetShelves)
            }
        }
    }

    private fun ReducerContext<State, Effect>.reduceEffectIntent(intent: Intent.EffectIntent) {
        when (intent) {
            Intent.EffectIntent.GetShelves -> {
                effects(Effect.GetShelves)
            }

            is Intent.EffectIntent.GetShelvesCompleted -> {
                state { State.Data(shelves = intent.shelves) }
            }

            is Intent.EffectIntent.GetShelvesFailed -> {
                state { State.Error(throwable = intent.throwable) }
            }
        }
    }
}

