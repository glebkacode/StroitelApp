package com.itapp.shelves_impl.data.api

import com.itapp.shelves_impl.data.model.SelfItemDtoResponse
import com.itapp.shelves_impl.data.model.ShelfDtoResponse
import dev.zacsweers.metro.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

private const val SHELVES_BASE_URL = "https://api.yourservice.com"

@Inject
class ShelvesDataSourceImpl(
    private val httpClient: HttpClient
) : ShelvesDataSource {

    override suspend fun getShelves(): List<ShelfDtoResponse> {
        //return httpClient.get(SHELVES_BASE_URL).body()
        return listOf(
            ShelfDtoResponse(
                id = 1,
                header = "Хиты продаж",
                type = "default",
                filterOptions = null,
                items = listOf(
                    SelfItemDtoResponse(
                        id = 1,
                        title = "МОЙКА RE 130 PLUS",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                    SelfItemDtoResponse(
                        id = 2,
                        title = "Мотокоса FS 38",
                        description = "Лёгкая мотокоса мощностью 0,65 кВт",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 17990.0
                    ),
                    SelfItemDtoResponse(
                        id = 3,
                        title = "РУЧНОЙ ОПРЫСКИВАТЕЛЬ SG 11",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),

                    SelfItemDtoResponse(
                        id = 4,
                        title = "МОЙКА RE 130 PLUS",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                    SelfItemDtoResponse(
                        id = 5,
                        title = "Мотокоса FS 38",
                        description = "Лёгкая мотокоса мощностью 0,65 кВт",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 17990.0
                    ),
                    SelfItemDtoResponse(
                        id = 6,
                        title = "РУЧНОЙ ОПРЫСКИВАТЕЛЬ SG 11",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),

                    SelfItemDtoResponse(
                        id = 7,
                        title = "МОЙКА RE 130 PLUS",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                    SelfItemDtoResponse(
                        id = 8,
                        title = "Мотокоса FS 38",
                        description = "Лёгкая мотокоса мощностью 0,65 кВт",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 17990.0
                    ),
                    SelfItemDtoResponse(
                        id = 9,
                        title = "РУЧНОЙ ОПРЫСКИВАТЕЛЬ SG 11",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                )
            ),
            ShelfDtoResponse(
                id = 2,
                header = "Каталог Stihl",
                type = "default",
                filterOptions = null,
                items = listOf(
                    SelfItemDtoResponse(
                        id = 1,
                        title = "МОЙКА RE 130 PLUS",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                    SelfItemDtoResponse(
                        id = 2,
                        title = "Мотокоса FS 38",
                        description = "Лёгкая мотокоса мощностью 0,65 кВт",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 17990.0
                    ),
                    SelfItemDtoResponse(
                        id = 3,
                        title = "РУЧНОЙ ОПРЫСКИВАТЕЛЬ SG 11",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),

                    SelfItemDtoResponse(
                        id = 4,
                        title = "МОЙКА RE 130 PLUS",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                    SelfItemDtoResponse(
                        id = 5,
                        title = "Мотокоса FS 38",
                        description = "Лёгкая мотокоса мощностью 0,65 кВт",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 17990.0
                    ),
                    SelfItemDtoResponse(
                        id = 6,
                        title = "РУЧНОЙ ОПРЫСКИВАТЕЛЬ SG 11",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),

                    SelfItemDtoResponse(
                        id = 7,
                        title = "МОЙКА RE 130 PLUS",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                    SelfItemDtoResponse(
                        id = 8,
                        title = "Мотокоса FS 38",
                        description = "Лёгкая мотокоса мощностью 0,65 кВт",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 17990.0
                    ),
                    SelfItemDtoResponse(
                        id = 9,
                        title = "РУЧНОЙ ОПРЫСКИВАТЕЛЬ SG 11",
                        description = "Компактная мойка высокого давления",
                        url = "",
                        labelInfo = "ХИТ",
                        price = 49990.0
                    ),
                )
            )
        )
    }
}