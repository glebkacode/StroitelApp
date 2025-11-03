package com.itapp.auth_impl.password_validation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class PasswordValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val openSmsScreen: () -> Unit
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

    @AssistedFactory
    interface Factory : PasswordValidationComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            openSmsScreen: () -> Unit
        ): PasswordValidationComponentImpl
    }
}