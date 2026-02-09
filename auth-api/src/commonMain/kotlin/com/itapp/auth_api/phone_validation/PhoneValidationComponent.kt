package com.itapp.auth_api.phone_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import kotlinx.coroutines.flow.StateFlow

interface PhoneValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onPhoneChanged(text: String)
    fun onLoginClicked()

    data class Callbacks(
        val onAuthSuccess: () -> Unit,
    )

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            callbacks: Callbacks,
        ) : PhoneValidationComponent
    }
    data class UiState(
        val phone: String = ""
    )
}
