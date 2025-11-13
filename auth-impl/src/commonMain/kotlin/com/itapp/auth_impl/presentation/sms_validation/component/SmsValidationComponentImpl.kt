package com.itapp.auth_impl.presentation.sms_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.auth_impl.domain.usecase.AuthUseCase
import com.itapp.auth_impl.presentation.sms_validation.mapping.toUiState
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.Intent
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsCodeValidationStore.Label
import com.itapp.auth_impl.presentation.sms_validation.mvi.smsValidationStore
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
class SmsValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val authUseCase: AuthUseCase,
    @Assisted("phone") private val phone: String,
    @Assisted("password") private val password: String,
    private val openProducts: () -> Unit
) : BaseComponent(componentContext), SmsValidationComponent {

    private val store = instanceKeeper.getStore {
        storeFactory.smsValidationStore(
            mainContext = Dispatchers.Main,
            defaultContext = Dispatchers.Default,
            ioContext = Dispatchers.IO,
            phone = phone,
            password = password,
            authUseCase = authUseCase
        )
    }

    override val uiState = store.stateFlow.map { state -> state.toUiState() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = SmsValidationComponent.UiState()
    )

    init {
        lifecycle.doOnCreate {
            store.labels.onEach { label ->
                when (label) {
                    is Label.OpenProducts -> openProducts()
                }
            }.launchIn(componentScope)
        }
    }

    override fun onSmsChanged(text: String) {
        store.accept(Intent.SmsCodeChanged(text))
    }

    override fun onContinueClicked() {
        store.accept(Intent.LoginClicked)
    }

    @Composable
    override fun render(modifier: Modifier) {
        SmsValidationScreen(
            modifier = modifier,
            component = this
        )
    }

    @AssistedFactory
    interface Factory : SmsValidationComponent.Factory {
        override operator fun invoke(
            componentContext: ComponentContext,
            @Assisted("phone") phone: String,
            @Assisted("password") password: String,
            openProducts: () -> Unit
        ) : SmsValidationComponentImpl
    }
}