package com.itapp.auth_impl.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.itapp.auth_api.root.AuthRootComponent

@Composable
fun AuthScreen(
    modifier: Modifier,
    component: AuthRootComponent
) {
    Children(component.stack) {
        when (val child = it.instance) {
            is AuthRootComponent.Child.PhoneValidationChild -> child.component.render(modifier = modifier)
            is AuthRootComponent.Child.PasswordValidationChild -> child.component.render(modifier = modifier)
            is AuthRootComponent.Child.SmsValidationChild -> child.component.render(modifier = modifier)
        }
    }
}