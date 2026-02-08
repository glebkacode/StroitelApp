package com.itapp.auth_impl.presentation.registration.mvi

import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Intent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.State
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.registrationTea(
    mainContext: CoroutineContext,
    ioContext: CoroutineContext
): RegistrationTea =
    object : RegistrationTea, Tea<State, Intent, Nothing> by create(
        initialState = State(),
        dispatcher = mainContext,
        effectors = emptyList(),
        reducer = RegistrationReducer(),
    ) {}

private class RegistrationReducer : DslReducer<State, Intent, Nothing>() {

    override fun ReducerContext<State, Nothing>.reduce(intent: Intent) {
        when (intent) {
            is Intent.FirstNameChanged -> state { copy(firstName = intent.text) }
            is Intent.LastNameChanged -> state { copy(lastName = intent.text) }
            is Intent.LoginChanged -> state { copy(login = intent.text) }
            is Intent.PasswordChanged -> state { copy(password = intent.text) }
            is Intent.PhoneChanged -> state { copy(phone = intent.text) }
            Intent.TogglePasswordVisibility -> state { copy(isPasswordVisible = !isPasswordVisible) }
            Intent.RegisterClicked -> {
                // Demo: just set loading state briefly
                state { copy(isLoading = true) }
            }
        }
    }
}
