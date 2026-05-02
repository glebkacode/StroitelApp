package com.itapp.auth_impl.presentation.registration.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.itapp.auth_api.registration.RegistrationComponent
import com.itapp.auth_impl.presentation.registration.mapping.toUi
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStore
import com.itapp.auth_impl.presentation.registration.mvi.RegistrationStoreFactory
import com.itapp.core_architecture.getStore
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class RegistrationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: RegistrationComponent.Callbacks,
    storeFactory: RegistrationStoreFactory,
) : BaseComponent(componentContext), RegistrationComponent {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    override val uiState = store.stateFlow.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Eagerly,
        initialValue = RegistrationComponent.UiState(),
    )

    init {
        store.labels
            .onEach { label ->
                when (label) {
                    RegistrationStore.Label.NavigateToSuccess -> callbacks.onRegistrationSuccess()
                }
            }
            .launchIn(componentScope)
    }

    override fun onFirstNameChanged(text: String) =
        store.accept(RegistrationStore.Intent.FirstNameChanged(text))

    override fun onLastNameChanged(text: String) =
        store.accept(RegistrationStore.Intent.LastNameChanged(text))

    override fun onLoginChanged(text: String) =
        store.accept(RegistrationStore.Intent.LoginChanged(text))

    override fun onPasswordChanged(text: String) =
        store.accept(RegistrationStore.Intent.PasswordChanged(text))

    override fun onPhoneChanged(text: String) =
        store.accept(RegistrationStore.Intent.PhoneChanged(text))

    override fun onPasswordVisibilityToggle() =
        store.accept(RegistrationStore.Intent.PasswordVisibilityToggle)

    override fun onFieldFocusLost(field: RegistrationComponent.Field) =
        store.accept(RegistrationStore.Intent.FieldFocusLost(field))

    override fun onRegisterClicked() =
        store.accept(RegistrationStore.Intent.RegisterClicked)

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
