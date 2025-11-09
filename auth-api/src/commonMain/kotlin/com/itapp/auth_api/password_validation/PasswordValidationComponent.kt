package com.itapp.auth_api.password_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

interface PasswordValidationComponent : BaseUiComponent {
    fun onNextClicked()
    fun onForgotPasswordClicked()
    fun onBackClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            phone: String,
            openSmsScreen: () -> Unit
        ): PasswordValidationComponent
    }
}