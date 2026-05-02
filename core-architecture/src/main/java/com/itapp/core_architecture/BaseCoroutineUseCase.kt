package com.itapp.core_architecture

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Базовый класс для доменных use case с автоматической обработкой ошибок.
 *
 * Выполняет [run] на IO диспетчере и оборачивает результат в [Result].
 *
 * ## Использование
 * ```kotlin
 * class GetUserUseCase @Inject constructor(
 *     private val repository: UserRepository
 * ) : BaseCoroutineUseCase<Long, User>() {
 *
 *     override suspend fun run(input: Long): User {
 *         return repository.getUser(input)
 *     }
 * }
 *
 * // Вызов
 * val result = getUserUseCase(userId)
 * result.fold(
 *     onSuccess = { user -> showUser(user) },
 *     onFailure = { error -> showError(error) }
 * )
 * ```
 *
 * @param I тип входных данных
 * @param O тип выходных данных
 *
 * @see CoroutineUseCase базовый интерфейс
 */
abstract class BaseCoroutineUseCase<I, O> : CoroutineUseCase<I, O> {

    /**
     * Вызывает use case и возвращает [Result].
     *
     * Выполняется на [Dispatchers.IO] и автоматически ловит исключения.
     *
     * @param input входные данные
     * @return [Result.success] с результатом или [Result.failure] с исключением
     */
    suspend operator fun invoke(input: I): Result<O> {
        return withContext(Dispatchers.IO) {
            runCatching { run(input) }
        }
    }
}