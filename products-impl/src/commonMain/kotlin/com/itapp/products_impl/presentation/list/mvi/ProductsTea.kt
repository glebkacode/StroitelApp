package com.itapp.products_impl.presentation.list.mvi

import com.itapp.core_architecture.tea.Tea
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.Event
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.Intent
import com.itapp.products_impl.presentation.list.mvi.ProductsTea.State
import com.itapp.shelves_api.domain.model.shelf.ShelfDto

interface ProductsTea : Tea<State, Intent, Event> {

    sealed interface Intent {
        sealed interface UiIntent : Intent {
            data class ShelfItemClicked(
                val shelfId: Long,
                val shelfItemId: Long
            ) : UiIntent
            data object Refresh : UiIntent
        }
        sealed interface EffectIntent : Intent {
            data object GetShelves : EffectIntent
            data class GetShelvesCompleted(val shelves: List<ShelfDto>) : EffectIntent
            data class GetShelvesFailed(val throwable: Throwable) : EffectIntent
        }
    }

    sealed interface Effect {
        data object GetShelves : Effect
        data class ShelfItemClicked(
            val shelfId: Long,
            val shelfItemId: Long
        ) : Effect
    }

    sealed interface Event {
        data class OpenProductDetails(
            val shelfId: Long,
            val shelfItemId: Long
        ) : Event
    }



    sealed interface State {
        data object Loading : State
        data class Data(val shelves: List<ShelfDto>) : State
        data class Error(val throwable: Throwable) : State
    }
}