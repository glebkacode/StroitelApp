package com.itapp.core_architecture

/**
 * Интерфейс для доменных use case с поддержкой корутин.
 *
 * Определяет контракт для выполнения бизнес-логики с входными и выходными параметрами.
 *
 * @param I тип входных данных
 * @param O тип выходных данных
 *
 * @see BaseCoroutineUseCase базовая реализация с обработкой ошибок
 */
interface CoroutineUseCase<in I, out O> {
    /**
     * Выполняет use case с переданными входными данными.
     *
     * @param input входные данные
     * @return результат выполнения
     */
    suspend fun run(input: I): O
}