package com.itapp.auth_impl.presentation.sms_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class SmsValidationComponentImpl(
    @Assisted componentContext: ComponentContext
) : BaseComponent(componentContext), SmsValidationComponent {

    @Composable
    override fun render(modifier: Modifier) {
        SmsValidationScreen(
            modifier = modifier,
            component = this
        )
    }

    override fun onContinueClicked() {

    }

    @AssistedFactory
    interface Factory : SmsValidationComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext
        ) : SmsValidationComponentImpl
    }
}