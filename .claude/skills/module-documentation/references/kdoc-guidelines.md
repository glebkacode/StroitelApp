# KDoc Guidelines

## Структура KDoc

KDoc комментарий начинается с `/**` и заканчивается `*/`. Первый абзац — краткое описание, остальные — детальное.

```kotlin
/**
 * Краткое описание (первое предложение).
 *
 * Детальное описание, которое может занимать
 * несколько строк и абзацев.
 *
 * @param name описание параметра
 * @return описание возвращаемого значения
 */
```

## Теги KDoc

### Основные теги

| Тег | Назначение | Пример |
|-----|------------|--------|
| `@param` | Описание параметра функции/конструктора | `@param phone номер телефона` |
| `@return` | Описание возвращаемого значения | `@return список пользователей` |
| `@throws` / `@exception` | Описание исключения | `@throws IllegalArgumentException если phone пустой` |
| `@see` | Ссылка на связанный элемент | `@see PhoneValidator` |
| `@since` | Версия с которой появился элемент | `@since 1.2.0` |
| `@suppress` | Подавление предупреждений | `@suppress("UNUSED")` |
| `@sample` | Ссылка на пример кода | `@sample com.example.samples.usage` |
| `@property` | Описание свойства класса | `@property id уникальный идентификатор` |
| `@constructor` | Описание конструктора | `@constructor создаёт экземпляр репозитория` |
| `@receiver` | Описание receiver в extension | `@receiver строка для обработки` |

### Inline разметка

| Синтаксис | Назначение | Пример |
|-----------|------------|--------|
| `[ClassName]` | Ссылка на класс/метод | `реализует [UiComponent]` |
| `[methodName]` | Ссылка на метод | `вызывает [validate]` |
| `` `code` `` | Inline код | `` возвращает `null` `` |
| `**bold**` | Жирный текст | `**Важно:**` |
| `*italic*` | Курсив | `*опционально*` |

## Что документировать

### Обязательно документировать

1. **Публичные классы и интерфейсы**
```kotlin
/**
 * Репозиторий для работы с данными пользователя.
 *
 * Предоставляет методы для получения, обновления и удаления
 * информации о пользователе. Использует [UserDataSource] для
 * доступа к API и [UserCache] для кэширования.
 *
 * @see UserDataSource
 * @see UserCache
 */
interface UserRepository {
```

2. **Публичные методы**
```kotlin
/**
 * Получает пользователя по идентификатору.
 *
 * Сначала проверяет кэш, при отсутствии делает запрос к API.
 * Результат кэшируется на 5 минут.
 *
 * @param userId уникальный идентификатор пользователя
 * @return данные пользователя или `null` если не найден
 * @throws NetworkException при ошибке сети
 * @throws UnauthorizedException если токен невалиден
 */
suspend fun getUser(userId: String): User?
```

3. **Публичные свойства**
```kotlin
/**
 * Текущее состояние экрана.
 *
 * Эмитит новое значение при каждом изменении состояния.
 * Начальное значение — [UiState.Loading].
 */
val uiState: StateFlow<UiState>
```

4. **Sealed классы и их подтипы**
```kotlin
/**
 * Состояние экрана авторизации.
 */
sealed interface AuthState {
    /**
     * Начальное состояние — ввод номера телефона.
     *
     * @property phone текущий введённый номер
     * @property isValid `true` если номер валиден
     */
    data class PhoneInput(
        val phone: String,
        val isValid: Boolean,
    ) : AuthState

    /**
     * Ожидание ввода SMS-кода.
     *
     * @property phone номер на который отправлен код
     * @property remainingSeconds секунд до возможности повторной отправки
     */
    data class CodeInput(
        val phone: String,
        val remainingSeconds: Int,
    ) : AuthState
}
```

5. **Extension функции**
```kotlin
/**
 * Форматирует номер телефона для отображения.
 *
 * Преобразует номер из формата `+71234567890` в `+7 (123) 456-78-90`.
 *
 * @receiver номер телефона в международном формате
 * @return отформатированный номер или исходная строка при ошибке
 */
fun String.formatAsPhone(): String
```

### Можно не документировать

- `private` и `internal` элементы (если код self-documenting)
- Очевидные getter/setter
- Переопределённые методы (если не меняют контракт)
- Data class properties с говорящими именами

## Стиль написания

### Используй

- Третье лицо: "Возвращает...", "Создаёт...", "Валидирует..."
- Настоящее время
- Конкретные описания с указанием поведения
- Примеры для сложных случаев

### Избегай

- Начинать с "Этот метод...", "Этот класс..."
- Повторять имя элемента в описании
- Общих фраз без конкретики
- Описания очевидного

### Хорошо vs Плохо

```kotlin
// Плохо
/**
 * Метод валидации телефона.
 */
fun validatePhone(phone: String): Boolean

// Хорошо
/**
 * Проверяет соответствие номера телефона формату +7XXXXXXXXXX.
 *
 * @param phone номер телефона для проверки
 * @return `true` если номер содержит 11 цифр и начинается с +7
 */
fun validatePhone(phone: String): Boolean
```

## Документирование в контексте проекта

### TEA компоненты

```kotlin
/**
 * TEA store для экрана каталога товаров.
 *
 * Управляет состоянием списка товаров, фильтрации и поиска.
 *
 * ## Состояние
 * - [State.products] — загруженные товары
 * - [State.filter] — текущие параметры фильтрации
 * - [State.isLoading] — флаг загрузки
 *
 * ## Интенты
 * - [Intent.LoadProducts] — загрузить товары
 * - [Intent.ApplyFilter] — применить фильтр
 * - [Intent.Search] — поиск по названию
 *
 * ## События
 * - [Event.NavigateToProduct] — переход к детальной странице
 *
 * @see CatalogReducer для логики обработки интентов
 * @see CatalogEffector для побочных эффектов
 */
interface CatalogTea : Tea<State, Intent, Event>
```

### Decompose компоненты

```kotlin
/**
 * Компонент экрана профиля пользователя.
 *
 * Отображает информацию о пользователе и позволяет редактировать
 * персональные данные. Интегрируется с навигацией через [Factory].
 *
 * ## Использование
 * ```kotlin
 * val component = profileComponentFactory.create(
 *     componentContext = componentContext,
 *     navigation = { event ->
 *         when (event) {
 *             is Event.Logout -> router.pop()
 *             is Event.EditProfile -> router.push(Config.EditProfile)
 *         }
 *     }
 * )
 * ```
 *
 * @see ProfileComponentImpl для реализации
 */
interface ProfileComponent : UiComponent {
```

### Use Cases

```kotlin
/**
 * Use case для авторизации пользователя по номеру телефона.
 *
 * Выполняет полный цикл авторизации:
 * 1. Отправка номера телефона
 * 2. Получение и верификация SMS-кода
 * 3. Сохранение токенов авторизации
 *
 * @see AuthRepository для работы с API авторизации
 * @see TokenStorage для сохранения токенов
 */
abstract class LoginByPhoneUseCase {
    /**
     * Отправляет запрос на авторизацию.
     *
     * @param phone номер телефона в формате +7XXXXXXXXXX
     * @param code SMS-код из 6 цифр
     * @return [Result.success] с данными пользователя или [Result.failure] с ошибкой
     */
    abstract suspend operator fun invoke(phone: String, code: String): Result<User>
}
```
