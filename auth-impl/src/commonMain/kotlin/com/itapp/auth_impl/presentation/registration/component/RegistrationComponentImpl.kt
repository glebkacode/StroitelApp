package com.itapp.auth_impl.presentation.registration.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_impl.presentation.registration.mapping.toUi
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationTea
import com.itapp.auth_impl.presentation.registration.mvi.registrationTea
import com.itapp.core_architecture.getTea
import com.itapp.core_architecture.tea.TeaFactory
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class RegistrationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted("onRegistrationSuccess") private val onRegistrationSuccess: () -> Unit,
    @Assisted("onBack") private val onBack: () -> Unit,
    private val teaFactory: TeaFactory
) : BaseComponent(componentContext), RegistrationComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.registrationTea(
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val uiState = store.state.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = RegistrationComponent.UiState()
    )

    override fun onFirstNameChanged(text: String) {
        store.accept(RegistrationTea.Intent.FirstNameChanged(text))
    }

    override fun onLastNameChanged(text: String) {
        store.accept(RegistrationTea.Intent.LastNameChanged(text))
    }

    override fun onLoginChanged(text: String) {
        store.accept(RegistrationTea.Intent.LoginChanged(text))
    }

    override fun onPasswordChanged(text: String) {
        store.accept(RegistrationTea.Intent.PasswordChanged(text))
    }

    override fun onPhoneChanged(text: String) {
        store.accept(RegistrationTea.Intent.PhoneChanged(text))
    }

    override fun onTogglePasswordVisibility() {
        store.accept(RegistrationTea.Intent.TogglePasswordVisibility)
    }

    override fun onRegisterClicked() {
        store.accept(RegistrationTea.Intent.RegisterClicked)
        // Demo: simulate success after button click
        onRegistrationSuccess()
    }

    override fun onBackClicked() {
        onBack()
    }

    @Composable
    override fun render(modifier: Modifier) {
        RegistrationScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : RegistrationComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("onRegistrationSuccess") onRegistrationSuccess: () -> Unit,
            @Assisted("onBack") onBack: () -> Unit
        ): RegistrationComponentImpl
    }
}
