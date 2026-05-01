package com.itapp.auth_impl.presentation.registration.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_impl.presentation.registration.viewmodel.RegistrationViewModel
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AssistedInject
class RegistrationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: RegistrationComponent.Callbacks,
) : BaseComponent(componentContext), RegistrationComponent {

    private val viewModel = RegistrationViewModel()

    override val uiState = viewModel.state

    init {
        viewModel.navigateToSuccess
            .onEach { callbacks.onRegistrationSuccess() }
            .launchIn(componentScope)
    }

    override fun onFirstNameChanged(text: String) = viewModel.onFirstNameChanged(text)
    override fun onLastNameChanged(text: String) = viewModel.onLastNameChanged(text)
    override fun onLoginChanged(text: String) = viewModel.onLoginChanged(text)
    override fun onPasswordChanged(text: String) = viewModel.onPasswordChanged(text)
    override fun onPhoneChanged(text: String) = viewModel.onPhoneChanged(text)
    override fun onPasswordVisibilityToggle() = viewModel.onPasswordVisibilityToggle()
    override fun onFieldFocusLost(field: RegistrationComponent.Field) = viewModel.onFieldFocusLost(field)
    override fun onRegisterClicked() = viewModel.onRegisterClicked()
    override fun onBackClicked() = callbacks.onBack()

    @Composable
    override fun render(modifier: Modifier) {
        RegistrationScreen(modifier = modifier, component = this)
    }

    @AssistedFactory
    interface Factory : RegistrationComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            callbacks: RegistrationComponent.Callbacks,
        ): RegistrationComponentImpl
    }
}
