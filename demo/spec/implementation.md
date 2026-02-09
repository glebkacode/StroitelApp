# Реализация экрана ввода пароля

## Обзор
Создан новый экран ввода пароля (Password Validation Screen) с навигацией с экрана ввода номера телефона.

## Реализованные компоненты

### 1. API модуль (auth-api)
**Файл:** `auth-api/src/commonMain/kotlin/com/itapp/auth_api/password_validation/PasswordValidationComponent.kt`
- Интерфейс компонента с методами:
  - `onPasswordChanged(text: String)` - обработка изменения пароля
  - `onContinueClicked()` - обработка нажатия кнопки "Продолжить"
  - `onRemindPasswordClicked()` - обработка нажатия ссылки "Напомнить пароль"
  - `onBackClicked()` - обработка нажатия кнопки "Назад"
- `UiState` с полем `password`
- `Callbacks` для навигации: `onAuthSuccess` и `onBack`

### 2. MVI слой (auth-impl)
**Файлы:**
- `presentation/password_validation/mvi/PasswordValidationTea.kt` - определение State, Intent
- `presentation/password_validation/mvi/PasswordValidationStoreFactory.kt` - создание Tea store с Reducer

**State:**
```kotlin
data class State(val password: String = "")
```

**Intents:**
- `PasswordChanged(text: String)` - изменение пароля
- `ContinueClicked` - нажатие кнопки

### 3. Mapping слой
**Файл:** `presentation/password_validation/mapping/StateMapping.kt`
- Преобразование MVI State в UI State

### 4. Component Implementation
**Файл:** `presentation/password_validation/component/PasswordValidationComponentImpl.kt`
- Реализация интерфейса `PasswordValidationComponent`
- Интеграция с Tea store
- Обработка callback'ов для навигации
- Использует `@AssistedInject` для DI

### 5. UI Screen
**Файл:** `presentation/password_validation/component/PasswordValidationScreen.kt`

**UI элементы:**
- TopAppBar с кнопкой назад и заголовком "Вход"
- Логотип "МАГАЗИН СТРОИТЕЛЬ" с иконкой
- Подзаголовок "Вход в приложение Магазин Строитель"
- TextField для ввода пароля с PasswordVisualTransformation
- Ссылка "Напомнить пароль"
- Кнопка "Продолжить"

**Используемые цвета:**
- Оранжевый (#EE7100) - `StroitelTheme.colorScheme.text.moscow`
- Белый - `StroitelTheme.colorScheme.text.piter`
- Серый плейсхолдер (#ADADAD)

### 6. Навигация
**Обновленные файлы:**
- `auth-api/src/commonMain/kotlin/com/itapp/auth_api/root/RootAuthComponent.kt`
  - Добавлен `PasswordValidationChild`
- `auth-impl/src/commonMain/kotlin/com/itapp/auth_impl/presentation/root/RootAuthComponentImpl.kt`
  - Добавлен `Config.PasswordValidation`
  - Реализована навигация: `PhoneValidation -> PasswordValidation`
  - При нажатии "Войти" на экране телефона - `navigation.push(Config.PasswordValidation)`
  - При нажатии "Назад" на экране пароля - `navigation.pop()`
- `auth-impl/src/commonMain/kotlin/com/itapp/auth_impl/presentation/root/AuthScreen.kt`
  - Добавлена обработка `PasswordValidationChild` в Children

### 7. DI интеграция
**Файл:** `auth-impl/src/commonMain/kotlin/com/itapp/auth_impl/di/AuthGraph.kt`
- Добавлен binding: `PasswordValidationComponentImpl.Factory.bind : PasswordValidationComponent.Factory`

### 8. Ресурсы
**Файл:** `auth-impl/src/commonMain/composeResources/values/strings.xml`
- Обновлены строки для password_validation:
  - `password_validation_hint_text` - "Введите пароль"
  - `password_validation_button` - "Продолжить"
  - `password_validation_remind_password` - "Напомнить пароль"

## Паттерны и архитектура

### TEA (The Elm Architecture)
- Используется для управления состоянием экрана
- Чистый Reducer для изменения State
- Intents для действий пользователя

### Decompose Navigation
- StackNavigation для управления стеком экранов
- Serializable Config для восстановления состояния
- Back button handling

### Metro DI
- `@AssistedInject` для компонентов с runtime параметрами
- `@AssistedFactory` для фабрик
- `@Binds` для привязки интерфейсов к реализациям

## Проверка работоспособности

### Компиляция
```bash
./gradlew :auth-impl:compileAndroidMain  # ✅ Успешно
./gradlew :composeApp:assembleDebug      # ✅ Успешно
```

### Навигационный флоу
1. Экран ввода телефона (PhoneValidationScreen)
2. Нажатие кнопки "Войти" → Навигация к PasswordValidationScreen
3. Экран ввода пароля (PasswordValidationScreen)
4. Кнопка "Назад" → Возврат к PhoneValidationScreen
5. Кнопка "Продолжить" → Успешная авторизация (переход к основному экрану)

## TODO / Будущие улучшения
- [ ] Реализовать логику "Напомнить пароль"
- [ ] Добавить валидацию пароля (минимальная длина, требования безопасности)
- [ ] Добавить индикатор загрузки при авторизации
- [ ] Добавить обработку ошибок авторизации
- [ ] Добавить тесты для PasswordValidationReducer
- [ ] Добавить тесты для mapping слоя
