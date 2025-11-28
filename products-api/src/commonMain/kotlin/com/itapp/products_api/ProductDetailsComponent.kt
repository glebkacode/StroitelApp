package com.itapp.products_api

import com.arkivanov.decompose.ComponentContext
import com.itapp.core_navigation.UiComponent
import kotlinx.coroutines.flow.StateFlow

interface ProductDetailsComponent : UiComponent {
    val models: StateFlow<Model>

    fun onTextChanged(text: String)

    data class Model(val text: String = "")

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            shelfId: Long,
            shelfItemId: Long
        ): ProductDetailsComponent
    }
}