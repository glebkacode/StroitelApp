package com.itapp.shelves_impl.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShelfDtoResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("type")
    val type: String,
    @SerialName("header")
    val header: String,
    @SerialName("filtersOptions")
    val filterOptions: List<FilterDtoResponse>?,
    @SerialName("items")
    val items: List<SelfItemDtoResponse>
)
