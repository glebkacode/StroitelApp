package com.itapp.auth_impl.phone_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class PhoneValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val openPasswordScreen: () -> Unit,
) : BaseComponent(componentContext), PhoneValidationComponent {

    override fun onNextClicked() {
        openPasswordScreen()
    }

    @Composable
    override fun render(modifier: Modifier) {
        PhoneValidationScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : PhoneValidationComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            openPasswordScreen: () -> Unit
        ) : PhoneValidationComponentImpl
    }
}