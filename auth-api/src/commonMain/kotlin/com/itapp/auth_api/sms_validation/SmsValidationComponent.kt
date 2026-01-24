package com.itapp.auth_api.sms_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import kotlinx.coroutines.flow.StateFlow

interface SmsValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onSmsChanged(text: String)
    fun onContinueClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            phone: String,
            password: String,
            openProducts: () -> Unit
        ) : SmsValidationComponent
    }
    data class UiState(
        val loading: Boolean = true,
        val smsCode: String = ""
    )
}