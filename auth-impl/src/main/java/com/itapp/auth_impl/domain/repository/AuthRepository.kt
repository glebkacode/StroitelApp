package com.itapp.auth_impl.domain.repository

import com.itapp.auth_impl.domain.model.ValidationPhoneDto

/**
 * Репозиторий для операций аутентификации и управления учётными данными.
 *
 * Абстрагирует источник данных от доменного слоя. Все методы — suspend,
 * выполняются на IO-диспетчере через [com.itapp.core_architecture.BaseCoroutineUseCase].
 */
interface AuthRepository {

    /**
     * Проверяет номер телефона и инициирует отправку SMS-кода.
     *
     * @param dto данные для валидации номера телефона
     * @throws Exception при сетевой ошибке или отказе сервера
     */
    suspend fun validatePhone(dto: ValidationPhoneDto)
}
