package com.itapp.shelves_impl.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SelfItemDtoResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("url")
    val url: String,
    @SerialName("labelInfo")
    val labelInfo: String,
    @SerialName("price")
    val price: Double
)