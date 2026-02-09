package com.itapp.auth_impl.presentation.password_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_impl.presentation.password_validation.mapping.toUi
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea
import com.itapp.auth_impl.presentation.password_validation.mvi.passwordValidationTea
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
class PasswordValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted private val callbacks: PasswordValidationComponent.Callbacks,
    private val teaFactory: TeaFactory
) : BaseComponent(componentContext), PasswordValidationComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.passwordValidationTea(
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val uiState = store.state.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = PasswordValidationComponent.UiState()
    )

    override fun onPasswordChanged(text: String) {
        store.accept(PasswordValidationTea.Intent.PasswordChanged(text))
    }

    override fun onContinueClicked() {
        val password = store.state.value.password
        if (password.isNotBlank()) {
            callbacks.onAuthSuccess()
        }
    }

    override fun onRemindPasswordClicked() {
        // TODO: Implement password reminder logic
    }

    override fun onBackClicked() {
        callbacks.onBack()
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
            callbacks: PasswordValidationComponent.Callbacks,
        ): PasswordValidationComponentImpl
    }
}
