package com.itapp.auth_impl.phone_validation.presentation.mvi

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.Intent
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.Label
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore.State
import dev.zacsweers.metro.Inject

@Inject
class PhoneValidationStoreFactory(
    private val storeFactory: StoreFactory,
) {

    internal fun create(): PhoneValidationStore =
        object : PhoneValidationStore, Store<Intent, State, Label> by storeFactory.create(
            name = "PhoneValidationStore",
            initialState = State(),
            executorFactory = coroutineExecutorFactory<Intent, Nothing, State, Msg, Label> {
                onIntent<Intent.PhoneChanged> { intent ->
                    dispatch(Msg.PhoneChanged(intent.text))
                }
                onIntent<Intent.PhoneApply> { /* No-op for now */ }
            },
            reducer = Reducer<State, Msg> { msg ->
                when (msg) {
                    is Msg.PhoneChanged -> copy(phone = msg.text)
                }
            },
        ) {}

    internal sealed interface Msg {
        data class PhoneChanged(val text: String) : Msg
    }
}
