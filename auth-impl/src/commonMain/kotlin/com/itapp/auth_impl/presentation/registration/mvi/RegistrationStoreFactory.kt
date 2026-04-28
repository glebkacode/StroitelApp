package com.itapp.auth_impl.presentation.registration.mvi

import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Effect
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Event
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.Intent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea.State
import com.itapp.core_architecture.tea.CoroutineEffector
import com.itapp.core_architecture.tea.DslReducer
import com.itapp.core_architecture.tea.ReducerContext
import com.itapp.core_architecture.tea.Tea
import com.itapp.core_architecture.tea.TeaFactory
import kotlin.coroutines.CoroutineContext

internal fun TeaFactory.registrationTea(
    mainContext: CoroutineContext,
    ioContext: CoroutineContext,
): RegistrationTea =
    object : RegistrationTea, Tea<State, Intent, Event> by create(
        initialState = State(),
        dispatcher = mainContext,
        effectors = listOf(RegistrationEffector()),
        reducer = RegistrationReducer(),
    ) {}

private class RegistrationEffector : CoroutineEffector<Effect, Intent, Event>() {

    override fun onEffect(effect: Effect) {
        when (effect) {
            Effect.NavigateRegistrationSuccess -> publish(Event.RegistrationSuccess)
        }
    }
}

private class RegistrationReducer : DslReducer<State, Intent, Effect>() {

    override fun ReducerContext<State, Effect>.reduce(intent: Intent) {
        when (intent) {
            is Intent.FirstNameChanged -> state { copy(firstName = intent.text, firstNameError = false) }
            is Intent.LastNameChanged -> state { copy(lastName = intent.text, lastNameError = false) }
            is Intent.LoginChanged -> state { copy(login = intent.text, loginError = false) }
            is Intent.PasswordChanged -> state { copy(password = intent.text, passwordError = false) }
            is Intent.PhoneChanged -> state { copy(phone = intent.text, phoneError = false) }
            Intent.PasswordVisibilityToggle -> state { copy(isPasswordVisible = !isPasswordVisible) }
            is Intent.FieldFocusLost -> validateField(intent.field)
            Intent.RegisterClicked -> validateAll()
        }
    }

    private fun ReducerContext<State, Effect>.validateField(field: RegistrationComponent.Field) {
        when (field) {
            RegistrationComponent.Field.FIRST_NAME -> state { copy(firstNameError = firstName.isBlank()) }
            RegistrationComponent.Field.LAST_NAME -> state { copy(lastNameError = lastName.isBlank()) }
            RegistrationComponent.Field.LOGIN -> state { copy(loginError = login.isBlank()) }
            RegistrationComponent.Field.PASSWORD -> state { copy(passwordError = password.isBlank()) }
            RegistrationComponent.Field.PHONE -> state { copy(phoneError = phone.isBlank()) }
        }
    }

    private fun ReducerContext<State, Effect>.validateAll() {
        state {
            copy(
                firstNameError = firstName.isBlank(),
                lastNameError = lastName.isBlank(),
                loginError = login.isBlank(),
                passwordError = password.isBlank(),
                phoneError = phone.isBlank(),
            )
        }
        val current = state
        val isValid = !current.firstNameError &&
            !current.lastNameError &&
            !current.loginError &&
            !current.passwordError &&
            !current.phoneError
        if (isValid) {
            effects(Effect.NavigateRegistrationSuccess)
        }
    }
}
