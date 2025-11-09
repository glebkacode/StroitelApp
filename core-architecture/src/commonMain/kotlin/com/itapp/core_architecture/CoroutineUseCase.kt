package com.itapp.core_architecture

interface CoroutineUseCase<in I, out O> {
    suspend fun run(input: I): O
}