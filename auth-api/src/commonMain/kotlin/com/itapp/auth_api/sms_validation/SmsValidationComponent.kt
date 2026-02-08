package com.itapp.auth_api.sms_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import dev.zacsweers.metro.Assisted
import kotlinx.coroutines.flow.StateFlow

interface SmsValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onSmsCodeChanged(text: String)
    fun onConfirmClicked()
    fun onBackClicked()

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("phoneNumber") phoneNumber: String,
            @Assisted("onSmsValidated") onSmsValidated: () -> Unit,
            @Assisted("onBack") onBack: () -> Unit
        ): SmsValidationComponent
    }

    data class UiState(
        val phoneNumber: String = "",
        val smsCode: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
