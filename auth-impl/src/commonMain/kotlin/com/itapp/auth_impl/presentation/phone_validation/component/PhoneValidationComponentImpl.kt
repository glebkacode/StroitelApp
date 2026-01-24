package com.itapp.auth_impl.presentation.phone_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_impl.presentation.phone_validation.mapping.toUi
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationTea
import com.itapp.auth_impl.presentation.phone_validation.mvi.phoneValidationTea
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
class PhoneValidationComponentImpl(
    @Assisted componentContext: ComponentContext,
    private val teaFactory: TeaFactory,
    @Assisted private val openPasswordScreen: (String) -> Unit,
) : BaseComponent(componentContext), PhoneValidationComponent {

    private val store = instanceKeeper.getTea {
        teaFactory.phoneValidationTea(
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val uiState = store.state.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = PhoneValidationComponent.UiState()
    )

    init {
        lifecycle.doOnCreate {
            store.events.onEach { event ->
                when (event) {
                    is PhoneValidationTea.Event.OpenPasswordValidation -> openPasswordScreen(
                        event.phone
                    )
                }
            }.launchIn(componentScope)
        }
    }

    override fun onPhoneChanged(text: String) {
        store.accept(PhoneValidationTea.Intent.PhoneChanged(text))
    }

    override fun onNextClicked() {
        store.accept(PhoneValidationTea.Intent.PhoneApply)
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
            openPasswordScreen: (String) -> Unit
        ): PhoneValidationComponentImpl
    }
}