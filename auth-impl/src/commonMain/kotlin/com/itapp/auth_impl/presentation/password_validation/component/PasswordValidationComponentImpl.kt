package com.itapp.auth_impl.presentation.password_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.password_validation.PasswordValidationComponent.UiState
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.presentation.password_validation.mapping.toUiState
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Event.*
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationTea.Intent
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
class PasswordValidationComponentImpl internal constructor(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    @Assisted private val phone: String,
    @Assisted private val openSmsScreen: (String, String) -> Unit
) : BaseComponent(componentContext), PasswordValidationComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.passwordValidationTea(
            phone = phone,
            validatePhoneNumberUseCase = validatePhoneNumberUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO,
            defaultContext = Dispatchers.Default
        )
    }

    override val uiState = store.state.map { it.toUiState() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = UiState.Loading()
    )

    init {
        lifecycle.doOnCreate {
            store.events.onEach { event ->
                when (event) {
                    is OpenSmsValidation -> openSmsScreen(event.phone, event.password)
                }
            }.launchIn(componentScope)
        }
    }

    override fun onPasswordChanged(text: String) {
        store.accept(Intent.PasswordChanged(text))
    }

    override fun onNextClicked() {
        store.accept(Intent.ValidatePasswordClicked)
    }

    override fun onForgotPasswordClicked() {

    }

    override fun onBackClicked() {

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
            phone: String,
            openSmsScreen: (String, String) -> Unit
        ): PasswordValidationComponentImpl
    }
}