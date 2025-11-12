package com.itapp.auth_impl.presentation.password_validation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.itapp.auth_api.password_validation.PasswordValidationComponent
import com.itapp.auth_impl.domain.usecase.ValidatePhoneNumberUseCase
import com.itapp.auth_impl.presentation.password_validation.mvi.passwordValidationStore
import com.itapp.core_navigation.BaseComponent
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@AssistedInject
class PasswordValidationComponentImpl internal constructor(
    @Assisted componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    private val validatePhoneNumberUseCase: ValidatePhoneNumberUseCase,
    @Assisted private val phone: String,
    @Assisted private val openSmsScreen: () -> Unit
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

    override fun onNextClicked() {
        openSmsScreen()
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
            openSmsScreen: () -> Unit
        ): PasswordValidationComponentImpl
    }
}