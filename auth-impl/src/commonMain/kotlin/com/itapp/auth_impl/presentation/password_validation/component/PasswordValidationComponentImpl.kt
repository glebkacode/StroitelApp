package com.itapp.auth_impl.presentation.password_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_impl.domain.usecase.LoginUseCase
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
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@AssistedInject
class PasswordValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted("phoneNumber") private val phoneNumber: String,
    @Assisted("onAuthSuccess") private val onAuthSuccess: () -> Unit,
    @Assisted("onBack") private val onBack: () -> Unit,
    private val teaFactory: TeaFactory,
    private val loginUseCase: LoginUseCase
) : BaseComponent(componentContext), PasswordValidationComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.passwordValidationTea(
            phoneNumber = phoneNumber,
            loginUseCase = loginUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    init {
        store.events
            .onEach { event ->
                when (event) {
                    is PasswordValidationTea.Effect.NavigateToSuccess -> onAuthSuccess()
                    is PasswordValidationTea.Effect.PerformLogin -> { /* handled by effector */ }
                }
            }
            .launchIn(componentScope)
    }

    override val uiState = store.state.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = PasswordValidationComponent.UiState(phoneNumber = phoneNumber)
    )

    override fun onPasswordChanged(text: String) {
        store.accept(PasswordValidationTea.Intent.PasswordChanged(text))
    }

    override fun onLoginClicked() {
        store.accept(PasswordValidationTea.Intent.LoginClicked)
    }

    override fun onBackClicked() {
        onBack()
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
            @Assisted("phoneNumber") phoneNumber: String,
            @Assisted("onAuthSuccess") onAuthSuccess: () -> Unit,
            @Assisted("onBack") onBack: () -> Unit
        ): PasswordValidationComponentImpl
    }
}
