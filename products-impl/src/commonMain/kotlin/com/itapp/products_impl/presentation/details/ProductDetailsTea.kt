package com.itapp.products_impl.presentation.details

import com.itapp.core_architecture.tea.Tea
import com.itapp.products_impl.presentation.details.ProductDetailsTea.Effect
import com.itapp.products_impl.presentation.details.ProductDetailsTea.Intent
import com.itapp.products_impl.presentation.details.ProductDetailsTea.State

interface ProductDetailsTea : Tea<State, Intent, Unit> {

    sealed interface Intent {
        sealed interface UiIntent : Intent {
            data class TextChanged(val text: String) : UiIntent
        }
        sealed interface EffectIntent : Intent {
            data object Complete : EffectIntent
        }
    }

    sealed interface Effect {
        data class LogTextChange(val text: String) : Effect
    }

    data class State(
        val text: String = ""
    )
}