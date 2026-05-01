package com.itapp.auth_impl.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.itapp.auth_api.root.RootAuthComponent
import com.itapp.auth_impl.presentation.screens.SmsConfirmationScreen

@Composable
fun AuthScreen(
    modifier: Modifier,
    component: RootAuthComponent
) {
    Children(component.stack) {
        when (val child = it.instance) {
            is RootAuthComponent.Child.PhoneValidationChild -> child.component.render(modifier = modifier)
            is RootAuthComponent.Child.RegistrationChild -> child.component.render(modifier = modifier)
            is RootAuthComponent.Child.SmsConfirmationChild -> SmsConfirmationScreen(
                modifier = modifier,
                onConfirmed = child.onConfirmed,
                onBack = child.onBack,
            )
        }
    }
}
