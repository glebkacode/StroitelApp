package com.itapp.auth_impl.presentation.phone_validation.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationStore.Intent
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationStore.Label
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationStore.State
import kotlin.coroutines.CoroutineContext

internal fun StoreFactory.phoneValidationStore(
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): PhoneValidationStore =
    object : PhoneValidationStore, Store<Intent, State, Label> by create(
        name = "phone_validation_store",
        initialState = State(),
        executorFactory = { ExecutorImpl(mainContext, ioContext) },
        reducer = { reduce(it) }
    ) {}

private sealed interface Msg : JvmSerializable {
    data class PhoneChanged(val text: String) : Msg
}

private class ExecutorImpl(
    mainContext: CoroutineContext,
    private val ioContext: CoroutineContext,
) : CoroutineExecutor<Intent, Nothing, State, Msg, Label>(mainContext) {

    override fun executeIntent(intent: Intent) {
        when (intent) {
            is Intent.PhoneChanged -> onPhoneChanged(intent.text)
            Intent.PhoneApply -> onPhoneApply(state().phone)
        }
    }

    private fun onPhoneChanged(phone: String) {
        dispatch(Msg.PhoneChanged(phone))
    }

    private fun onPhoneApply(phone: String) {
        publish(Label.OpenPasswordValidation(phone))
    }
}

private fun State.reduce(msg: Msg): State {
    return when (msg) {
        is Msg.PhoneChanged -> this.copy(phone = msg.text)
    }
}
