package com.itapp.auth_impl.sms_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent

internal class ValidationComponentImpl(
    componentContext: ComponentContext
) : BaseUiComponent(componentContext), SmsValidationComponent {

    @Composable
    override fun render(modifier: Modifier) {
        SmsValidationScreen(modifier)
    }
}