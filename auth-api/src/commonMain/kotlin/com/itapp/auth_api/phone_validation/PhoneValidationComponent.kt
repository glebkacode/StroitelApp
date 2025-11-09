package com.itapp.auth_api.phone_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

interface PhoneValidationComponent : BaseUiComponent {
    fun onNextClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openPasswordScreen: (String) -> Unit
        ) : PhoneValidationComponent
    }
}