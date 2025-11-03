package com.itapp.auth_api.sms_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

interface SmsValidationComponent : BaseUiComponent {
    fun onContinueClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ) : SmsValidationComponent
    }
}