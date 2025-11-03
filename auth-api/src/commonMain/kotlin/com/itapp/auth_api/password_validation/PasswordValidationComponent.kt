package com.itapp.auth_api.password_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

interface PasswordValidationComponent : BaseUiComponent {
    fun onNextClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openSmsScreen: () -> Unit
        ): PasswordValidationComponent
    }
}