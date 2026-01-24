package com.itapp.products_api.details

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent

interface ProductCharacteristicsComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): ProductCharacteristicsComponent
    }
}