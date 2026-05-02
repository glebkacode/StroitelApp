package com.itapp.auth_impl.presentation.registration.mvi

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import com.itapp.auth_api.registration.RegistrationComponent.Field
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore.Intent
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore.Label
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore.State
import dev.zacsweers.metro.Inject

@Inject
class RegistrationStoreFactory(
    private val storeFactory: StoreFactory,
) {

    internal fun create(): RegistrationStore =
        object : RegistrationStore, Store<Intent, State, Label> by storeFactory.create(
            name = "RegistrationStore",
            initialState = State(),
            executorFactory = coroutineExecutorFactory<Intent, Nothing, State, Msg, Label> {
                onIntent<Intent.FirstNameChanged> { dispatch(Msg.FirstNameChanged(it.text)) }
                onIntent<Intent.LastNameChanged> { dispatch(Msg.LastNameChanged(it.text)) }
                onIntent<Intent.LoginChanged> { dispatch(Msg.LoginChanged(it.text)) }
                onIntent<Intent.PasswordChanged> { dispatch(Msg.PasswordChanged(it.text)) }
                onIntent<Intent.PhoneChanged> { dispatch(Msg.PhoneChanged(it.text)) }
                onIntent<Intent.PasswordVisibilityToggle> { dispatch(Msg.PasswordVisibilityToggle) }

                onIntent<Intent.FieldFocusLost> { intent ->
                    val current = state()
                    val isError = when (intent.field) {
                        Field.FIRST_NAME -> current.firstName.isBlank()
                        Field.LAST_NAME -> current.lastName.isBlank()
                        Field.LOGIN -> current.login.isBlank()
                        Field.PASSWORD -> current.password.isBlank()
                        Field.PHONE -> current.phone.isBlank()
                    }
                    dispatch(Msg.FieldErrorUpdated(intent.field, isError))
                }

                onIntent<Intent.RegisterClicked> {
                    val current = state()
                    val firstNameError = current.firstName.isBlank()
                    val lastNameError = current.lastName.isBlank()
                    val loginError = current.login.isBlank()
                    val passwordError = current.password.isBlank()
                    val phoneError = current.phone.isBlank()

                    dispatch(
                        Msg.AllErrorsUpdated(
                            firstNameError = firstNameError,
                            lastNameError = lastNameError,
                            loginError = loginError,
                            passwordError = passwordError,
                            phoneError = phoneError,
                        )
                    )

                    val isValid = !firstNameError && !lastNameError && !loginError &&
                        !passwordError && !phoneError
                    if (isValid) {
                        publish(Label.NavigateToSuccess)
                    }
                }
            },
            reducer = Reducer<State, Msg> { msg ->
                when (msg) {
                    is Msg.FirstNameChanged -> copy(firstName = msg.text, firstNameError = false)
                    is Msg.LastNameChanged -> copy(lastName = msg.text, lastNameError = false)
                    is Msg.LoginChanged -> copy(login = msg.text, loginError = false)
                    is Msg.PasswordChanged -> copy(password = msg.text, passwordError = false)
                    is Msg.PhoneChanged -> copy(phone = msg.text, phoneError = false)
                    Msg.PasswordVisibilityToggle -> copy(isPasswordVisible = !isPasswordVisible)
                    is Msg.FieldErrorUpdated -> when (msg.field) {
                        Field.FIRST_NAME -> copy(firstNameError = msg.isError)
                        Field.LAST_NAME -> copy(lastNameError = msg.isError)
                        Field.LOGIN -> copy(loginError = msg.isError)
                        Field.PASSWORD -> copy(passwordError = msg.isError)
                        Field.PHONE -> copy(phoneError = msg.isError)
                    }
                    is Msg.AllErrorsUpdated -> copy(
                        firstNameError = msg.firstNameError,
                        lastNameError = msg.lastNameError,
                        loginError = msg.loginError,
                        passwordError = msg.passwordError,
                        phoneError = msg.phoneError,
                    )
                }
            },
        ) {}

    internal sealed interface Msg {
        data class FirstNameChanged(val text: String) : Msg
        data class LastNameChanged(val text: String) : Msg
        data class LoginChanged(val text: String) : Msg
        data class PasswordChanged(val text: String) : Msg
        data class PhoneChanged(val text: String) : Msg
        data object PasswordVisibilityToggle : Msg
        data class FieldErrorUpdated(val field: Field, val isError: Boolean) : Msg
        data class AllErrorsUpdated(
            val firstNameError: Boolean,
            val lastNameError: Boolean,
            val loginError: Boolean,
            val passwordError: Boolean,
            val phoneError: Boolean,
        ) : Msg
    }
}
