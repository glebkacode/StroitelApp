package com.itapp.auth_impl.password_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

internal class PasswordValidationComponentImpl(
    componentContext: ComponentContext
) : BaseUiComponent(componentContext), PasswordValidationComponent {

    @Composable
    override fun render(modifier: Modifier) {
        PasswordValidationScreen(modifier)
    }
}