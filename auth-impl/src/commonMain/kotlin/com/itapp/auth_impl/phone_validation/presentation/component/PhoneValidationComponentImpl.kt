package com.itapp.auth_impl.phone_validation.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnCreate
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.itapp.auth_api.phone_validation.PhoneValidationComponent
import com.itapp.auth_impl.phone_validation.presentation.mvi.PhoneValidationStore
import com.itapp.auth_impl.phone_validation.presentation.mvi.phoneValidationStore
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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