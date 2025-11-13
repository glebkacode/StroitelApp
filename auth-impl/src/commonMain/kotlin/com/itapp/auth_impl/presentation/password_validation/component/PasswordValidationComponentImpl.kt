package com.itapp.auth_impl.presentation.password_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_api.password_validation.PasswordValidationComponent.UiState
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.presentation.password_validation.mapping.toUiState
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.Intent
import com.itapp.auth_impl.presentation.password_validation.mvi.PasswordValidationStore.Label
import com.itapp.auth_impl.presentation.password_validation.mvi.passwordValidationStore
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
    private val storeFactory: StoreFactory,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    @Assisted private val phone: String,
    @Assisted private val openSmsScreen: (String, String) -> Unit
) : BaseComponent(componentContext), PasswordValidationComponent {

    private val store = instanceKeeper.getStore {
        storeFactory.passwordValidationStore(
            phone = phone,
            validatePhoneNumberUseCase = validatePhoneNumberUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO,
            defaultContext = Dispatchers.Default
        )
    }

    override val uiState = store.stateFlow.map { it.toUiState() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = UiState.Loading()
    )

    init {
        lifecycle.doOnCreate {
            store.labels.onEach { label ->
                when (label) {
                    is Label.OpenSmsValidation -> openSmsScreen(label.phone, label.password)
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