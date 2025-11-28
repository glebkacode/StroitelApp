package com.itapp.products_api.details

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value
import com.itapp.core_navigation.UiComponent

interface ProductDetailsComponent : UiComponent {
    val stack: Value<ChildStack<*, Child>>

    fun onDescriptionTabClicked()
    fun onCharacteristicsTabClicked()

    sealed interface Child {
        class Description(val component: ProductDescriptionComponent) : Child
        class Characteristics(val component: ProductCharacteristicsComponent) : Child
    }

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            shelfId: Long,
            shelfItemId: Long
        ): ProductDetailsComponent
    }
}