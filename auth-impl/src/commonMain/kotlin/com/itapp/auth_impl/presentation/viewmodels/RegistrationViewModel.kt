package com.itapp.auth_impl.presentation.viewmodels

import com.itapp.auth_api.registration.RegistrationComponent.Field
import com.itapp.auth_api.registration.RegistrationComponent.UiState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel {

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _navigateToSuccess = Channel<Unit>(Channel.BUFFERED)
    val navigateToSuccess = _navigateToSuccess.receiveAsFlow()

    fun onFirstNameChanged(text: String) {
        _state.update { it.copy(firstName = text, firstNameError = false) }
    }

    fun onLastNameChanged(text: String) {
        _state.update { it.copy(lastName = text, lastNameError = false) }
    }

    fun onLoginChanged(text: String) {
        _state.update { it.copy(login = text, loginError = false) }
    }

    fun onPasswordChanged(text: String) {
        _state.update { it.copy(password = text, passwordError = false) }
    }

    fun onPhoneChanged(text: String) {
        _state.update { it.copy(phone = text, phoneError = false) }
    }

    fun onPasswordVisibilityToggle() {
        _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onFieldFocusLost(field: Field) {
        val current = _state.value
        when (field) {
            Field.FIRST_NAME -> _state.update { it.copy(firstNameError = current.firstName.isBlank()) }
            Field.LAST_NAME -> _state.update { it.copy(lastNameError = current.lastName.isBlank()) }
            Field.LOGIN -> _state.update { it.copy(loginError = current.login.isBlank()) }
            Field.PASSWORD -> _state.update { it.copy(passwordError = current.password.isBlank()) }
            Field.PHONE -> _state.update { it.copy(phoneError = current.phone.isBlank()) }
        }
    }

    fun onRegisterClicked() {
        val current = _state.value
        val firstNameError = current.firstName.isBlank()
        val lastNameError = current.lastName.isBlank()
        val loginError = current.login.isBlank()
        val passwordError = current.password.isBlank()
        val phoneError = current.phone.isBlank()
        _state.update {
            it.copy(
                firstNameError = firstNameError,
                lastNameError = lastNameError,
                loginError = loginError,
                passwordError = passwordError,
                phoneError = phoneError,
            )
        }
        val isValid = !firstNameError && !lastNameError && !loginError && !passwordError && !phoneError
        if (isValid) {
            _navigateToSuccess.trySend(Unit)
        }
    }
}
