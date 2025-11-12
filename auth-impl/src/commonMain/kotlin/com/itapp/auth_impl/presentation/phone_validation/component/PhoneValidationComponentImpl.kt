package com.itapp.auth_impl.presentation.phone_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_impl.presentation.phone_validation.mapping.toUi
import com.itapp.auth_impl.presentation.phone_validation.mvi.PhoneValidationStore
import com.itapp.auth_impl.presentation.phone_validation.mvi.phoneValidationStore
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
    private val storeFactory: StoreFactory,
    @Assisted private val openPasswordScreen: (String) -> Unit,
) : BaseComponent(componentContext), PhoneValidationComponent {

    private val store = instanceKeeper.getStore {
        storeFactory.phoneValidationStore(
            mainContext = Dispatchers.Main,
            ioContext = Dispatchers.IO
        )
    }

    override val uiState = store.stateFlow.map { it.toUi() }.stateIn(
        scope = componentScope,
        started = SharingStarted.Lazily,
        initialValue = PhoneValidationComponent.UiState()
    )

    init {
        lifecycle.doOnCreate {
            store.labels.onEach { label ->
                when (label) {
                    is PhoneValidationStore.Label.OpenPasswordValidation -> openPasswordScreen(
                        label.phone
                    )
                }
            }.launchIn(componentScope)
        }
    }

    override fun onPhoneChanged(text: String) {
        store.accept(PhoneValidationStore.Intent.PhoneChanged(text))
    }

    override fun onNextClicked() {
        store.accept(PhoneValidationStore.Intent.PhoneApply)
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