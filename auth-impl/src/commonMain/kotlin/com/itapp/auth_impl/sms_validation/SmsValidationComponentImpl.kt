package com.itapp.auth_impl.sms_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.core_navigation.BaseComponent

internal class SmsValidationComponentImpl(
    componentContext: ComponentContext
) : BaseComponent(componentContext), SmsValidationComponent {

    @Composable
    override fun render(modifier: Modifier) {
        SmsValidationScreen(
            modifier = modifier,
            component = this
        )
    }
}