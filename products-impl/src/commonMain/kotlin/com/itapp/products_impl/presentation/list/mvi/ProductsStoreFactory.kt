package com.itapp.products_impl.presentation.list.mvi

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.Intent
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.Label
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.State
import com.itapp.shelves_api.domain.model.shelf.ShelfDto
import com.itapp.shelves_api.domain.usecase.GetShelvesUseCase
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.productsStore(
    getShelvesUseCase: GetShelvesUseCase,
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): ProductsStore =
    object : ProductsStore, Store<Intent, State, Label> by create(
        name = "products_store",
        initialState = State.Loading,
        bootstrapper = SimpleBootstrapper(Action.GetShelves),
        executorFactory = { ExecutorImpl(getShelvesUseCase, mainContext, ioContext) },
        reducer = PasswordValidationReducer
    ) {}

private sealed interface Msg : JvmSerializable {
    data class GetShelvesCompleted(val shelves: List<ShelfDto>) : Msg
    data class GetShelvesFailed(val throwable: Throwable) : Msg
}

private sealed interface Action : JvmSerializable {
    data object GetShelves : Action
}

private class ExecutorImpl(
    private val getShelvesUseCase: GetShelvesUseCase,
    private val mainContext: CoroutineContext,
    private val ioContext: CoroutineContext
) : CoroutineExecutor<Intent, Action, State, Msg, Label>(mainContext) {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            Intent.Refresh -> getShelves()
            is Intent.ShelfItemClicked -> onShelfItemClicked(intent.shelfId, intent.shelfItemId)
        }
    }

    override fun executeAction(action: Action) {
        when (action) {
            Action.GetShelves -> getShelves()
        }
    }

    private fun getShelves() {
        scope.launch(ioContext) {
            getShelvesUseCase(Unit).fold(
                onSuccess = { shelves ->
                    withContext(mainContext) {
                        dispatch(Msg.GetShelvesCompleted(shelves))
                    }
                },
                onFailure = { throwable ->
                    dispatch(Msg.GetShelvesFailed(throwable))
                }
            )
        }
    }

    private fun onShelfItemClicked(shelfId: Long, shelfItemId: Long) {
        publish(Label.OpenProductDetails(shelfId, shelfItemId))
    }
}

private object PasswordValidationReducer : Reducer<State, Msg> {

    override fun State.reduce(msg: Msg): State {
        return when (msg) {
            is Msg.GetShelvesCompleted -> State.Data(shelves = msg.shelves)
            is Msg.GetShelvesFailed -> State.Error(throwable = msg.throwable)
        }
    }
}


