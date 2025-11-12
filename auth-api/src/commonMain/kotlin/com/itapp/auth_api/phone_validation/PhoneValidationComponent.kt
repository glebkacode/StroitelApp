package com.itapp.auth_api.phone_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent
import kotlinx.coroutines.flow.StateFlow

interface PhoneValidationComponent : BaseUiComponent {
    val uiState: StateFlow<UiState>
    fun onPhoneChanged(text: String)
    fun onNextClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openPasswordScreen: (String) -> Unit
        ) : PhoneValidationComponent
    }
    data class UiState(
        val phone: String = ""
    )
}