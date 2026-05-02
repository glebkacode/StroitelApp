package com.itapp.auth_impl.phone_validation.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_impl.phone_validation.presentation.mapping.toUi
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStoreFactory
import com.itapp.core_architecture.getStore
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class PhoneValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: PhoneValidationComponent.Callbacks,
    storeFactory: PhoneValidationStoreFactory,
) : BaseComponent(componentContext), PhoneValidationComponent {

    private val store = instanceKeeper.getStore { storeFactory.create() }

    override val uiState = store.stateFlow.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = PhoneValidationComponent.UiState()
    )

    override fun onPhoneChanged(text: String) {
        store.accept(PhoneValidationStore.Intent.PhoneChanged(text))
    }

    override fun onLoginClicked() {
        val phone = store.state.phone
        if (phone.isNotBlank()) {
            callbacks.onAuthSuccess()
        }
    }

    override fun onRegisterClicked() {
        callbacks.onRegisterClicked()
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
            callbacks: PhoneValidationComponent.Callbacks,
        ): PhoneValidationComponentImpl
    }
}
