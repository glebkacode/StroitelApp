package com.itapp.shelves_impl.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FilterDtoResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("option")
    val option: String
)