package com.itapp.auth_impl.password_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.core_navigation.BaseComponent

internal class PasswordValidationComponentImpl(
    componentContext: ComponentContext,
    private val openSmsScreen: () -> Unit
) : BaseComponent(componentContext), PasswordValidationComponent {

    override fun onNextClicked() {
        openSmsScreen()
    }

    @Composable
    override fun render(modifier: Modifier) {
        PasswordValidationScreen(
            modifier = modifier,
            component = this
        )
    }
}