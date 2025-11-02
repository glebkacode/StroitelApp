package com.itapp.auth_impl.phone_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

internal class PhoneValidationComponentImpl(
    componentContext: ComponentContext
) : BaseUiComponent(componentContext), PhoneValidationComponent {

    @Composable
    override fun render(modifier: Modifier) {
        PhoneValidationScreen(modifier)
    }
}