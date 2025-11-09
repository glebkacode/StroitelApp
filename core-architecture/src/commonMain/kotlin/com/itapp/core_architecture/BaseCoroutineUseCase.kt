package com.itapp.core_architecture

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

abstract class BaseCoroutineUseCase<I, O> : CoroutineUseCase<I, O> {

    suspend operator fun invoke(input: I): Result<O> {
        return withContext(Dispatchers.IO) {
            runCatching { run(input) }
        }
    }
}