package com.itapp.auth_impl.data.api

import com.itapp.auth_impl.data.model.request.ValidatePhoneRequestDto

/**
 * Источник данных для операций аутентификации.
 *
 * Определяет низкоуровневый контракт взаимодействия с сетевым API.
 * Реализуется [AuthDataSourceImpl], который делегирует вызовы Ktor-клиенту.
 *
 * @see com.itapp.auth_impl.domain.repository.AuthRepository доменный слой поверх этого источника
 */
interface AuthDataSource {

    /**
     * Отправляет запрос валидации номера телефона и инициирует отправку SMS-кода.
     *
     * @param request тело запроса с номером телефона
     * @throws Exception при сетевой ошибке или отказе сервера
     */
    suspend fun validatePhone(request: ValidatePhoneRequestDto)
}
