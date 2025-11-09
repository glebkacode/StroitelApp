package com.itapp.auth_api.phone_validation

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.BaseUiComponent
import kotlinx.coroutines.flow.Flow

interface PhoneValidationComponent : BaseUiComponent {
    val state: Flow<PhoneValidationUi>
    fun onPhoneChanged(text: String)
    fun onNextClicked()
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openPasswordScreen: (String) -> Unit
        ) : PhoneValidationComponent
    }
}