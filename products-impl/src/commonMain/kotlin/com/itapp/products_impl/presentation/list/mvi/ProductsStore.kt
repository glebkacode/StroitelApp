package com.itapp.products_impl.presentation.list.mvi

import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.utils.JvmSerializable
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.Intent
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.Label
import com.itapp.products_impl.presentation.list.mvi.ProductsStore.State
import com.itapp.shelves_api.domain.model.shelf.ShelfDto

interface ProductsStore : Store<Intent, State, Label> {

    sealed interface Intent : JvmSerializable {
        data object Refresh : Intent
        data class ShelfItemClicked(
            val shelfId: Long,
            val shelfItemId: Long
        ) : Intent
    }

    sealed interface Label : JvmSerializable {
        data class OpenProductDetails(
            val shelfId: Long,
            val shelfItemId: Long
        ) : Label
    }

    sealed interface State : JvmSerializable {
        data object Loading : State
        data class Data(val shelves: List<ShelfDto>) : State
        data class Error(val throwable: Throwable) : State
    }
}