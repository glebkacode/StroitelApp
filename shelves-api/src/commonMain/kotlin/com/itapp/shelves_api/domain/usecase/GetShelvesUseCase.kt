package com.itapp.shelves_api.domain.usecase

import com.itapp.core_architecture.BaseCoroutineUseCase
import com.itapp.shelves_api.domain.model.shelf.ShelfDto

abstract class GetShelvesUseCase : BaseCoroutineUseCase<Unit, List<ShelfDto>>()