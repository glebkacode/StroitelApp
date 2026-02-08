package com.itapp.auth_impl.presentation.sms_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.itapp.auth_api.sms_validation.SmsValidationComponent
import com.itapp.auth_impl.domain.usecase.ValidateSmsCodeUseCase
import com.itapp.auth_impl.presentation.sms_validation.mapping.toUi
import com.itapp.auth_impl.presentation.sms_validation.mvi.SmsValidationTea
import com.itapp.auth_impl.presentation.sms_validation.mvi.smsValidationTea
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
class SmsValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    @Assisted("phoneNumber") private val phoneNumber: String,
    @Assisted("onSmsValidated") private val onSmsValidated: () -> Unit,
    @Assisted("onBack") private val onBack: () -> Unit,
    private val teaFactory: TeaFactory,
    private val validateSmsCodeUseCase: ValidateSmsCodeUseCase
) : BaseComponent(componentContext), SmsValidationComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.smsValidationTea(
            phoneNumber = phoneNumber,
            validateSmsCodeUseCase = validateSmsCodeUseCase,
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    init {
        store.events
            .onEach { event ->
                when (event) {
                    is SmsValidationTea.Effect.NavigateToPassword -> onSmsValidated()
                    is SmsValidationTea.Effect.VerifySmsCode -> { /* handled by effector */ }
                }
            }
            .launchIn(componentScope)
    }

    override val uiState = store.state.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = SmsValidationComponent.UiState(phoneNumber = phoneNumber)
    )

    override fun onSmsCodeChanged(text: String) {
        store.accept(SmsValidationTea.Intent.SmsCodeChanged(text))
    }

    override fun onConfirmClicked() {
        store.accept(SmsValidationTea.Intent.ConfirmClicked)
    }

    override fun onBackClicked() {
        onBack()
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
            @Assisted("phoneNumber") phoneNumber: String,
            @Assisted("onSmsValidated") onSmsValidated: () -> Unit,
            @Assisted("onBack") onBack: () -> Unit
        ): SmsValidationComponentImpl
    }
}
