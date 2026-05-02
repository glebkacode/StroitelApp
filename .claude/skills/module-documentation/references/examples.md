# Примеры документации

Примеры документирования различных элементов кода в проекте.

## Интерфейс компонента (API модуль)

```kotlin
/**
 * Компонент экрана валидации номера телефона.
 *
 * Предоставляет UI для ввода и валидации номера телефона
 * перед отправкой SMS-кода. Интегрируется с Compose через [render].
 *
 * ## Жизненный цикл
 * Компонент привязан к [ComponentContext] и автоматически
 * сохраняет состояние при изменении конфигурации.
 *
 * ## Использование
 * ```kotlin
 * val component = phoneValidationComponentFactory.create(
 *     componentContext = componentContext,
 *     navigation = { event ->
 *         when (event) {
 *             is Event.NavigateToSmsCode -> navigateToSmsCode(event.phone)
 *         }
 *     }
 * )
 *
 * // В Compose
 * component.render(Modifier.fillMaxSize())
 * ```
 *
 * @see PhoneValidationComponentImpl для реализации
 * @see UiComponent базовый интерфейс
 */
interface PhoneValidationComponent : UiComponent {

    /**
     * Текущее состояние экрана.
     *
     * Эмитит [UiState] при каждом изменении:
     * - введённого номера телефона
     * - статуса валидации
     * - состояния загрузки
     */
    val uiState: StateFlow<UiState>

    /**
     * Обрабатывает ввод номера телефона.
     *
     * Автоматически форматирует номер и запускает валидацию.
     * Невалидные символы игнорируются.
     *
     * @param phone введённый текст (может содержать любые символы)
     */
    fun onPhoneChanged(phone: String)

    /**
     * Инициирует отправку SMS-кода.
     *
     * Доступно только если [UiState.isSubmitEnabled] == `true`.
     * При успехе эмитит [Event.NavigateToSmsCode].
     */
    fun onSubmitClick()

    /**
     * Состояние UI экрана валидации телефона.
     *
     * @property phone отформатированный номер телефона для отображения
     * @property phoneError текст ошибки валидации или `null`
     * @property isLoading `true` во время отправки запроса
     * @property isSubmitEnabled `true` если кнопка отправки активна
     */
    data class UiState(
        val phone: String = "",
        val phoneError: String? = null,
        val isLoading: Boolean = false,
        val isSubmitEnabled: Boolean = false,
    )

    /**
     * События навигации компонента.
     */
    sealed interface Event {
        /**
         * Запрос перехода к экрану ввода SMS-кода.
         *
         * @property phone валидный номер телефона в формате +7XXXXXXXXXX
         */
        data class NavigateToSmsCode(val phone: String) : Event
    }

    /**
     * Фабрика для создания [PhoneValidationComponent].
     *
     * Используется DI для инъекции зависимостей.
     */
    interface Factory {
        /**
         * Создаёт экземпляр компонента.
         *
         * @param componentContext контекст жизненного цикла Decompose
         * @param navigation callback для обработки событий навигации
         * @return готовый к использованию компонент
         */
        fun create(
            componentContext: ComponentContext,
            navigation: (Event) -> Unit,
        ): PhoneValidationComponent
    }
}
```

## MviKotlin Store интерфейс

```kotlin
/**
 * MviKotlin Store для экрана валидации телефона.
 *
 * Управляет состоянием ввода номера телефона и процессом
 * отправки запроса на получение SMS-кода.
 *
 * ## Поток данных
 * ```
 * User Input → Intent → Executor → Msg → Reducer → State
 *                          │
 *                          └────► Label (one-shot для UI)
 * ```
 *
 * ## State
 * - [State.phone] — введённый номер
 * - [State.validationError] — ошибка валидации
 * - [State.requestState] — состояние запроса
 *
 * ## Intents
 * - [Intent.PhoneChanged] — изменение номера
 * - [Intent.SubmitClicked] — нажатие кнопки отправки
 *
 * ## Labels (one-shot события)
 * - [Label.NavigateToSmsCode] — переход к вводу кода
 * - [Label.ShowError] — отображение ошибки
 *
 * @see PhoneValidationStoreFactory фабрика, реализующая reducer и executor
 */
internal interface PhoneValidationStore : Store<
    PhoneValidationStore.Intent,
    PhoneValidationStore.State,
    PhoneValidationStore.Label
> {

    /**
     * Состояние экрана валидации телефона.
     *
     * @property phone текущий номер телефона (только цифры)
     * @property formattedPhone отформатированный номер для отображения
     * @property validationError текст ошибки валидации
     * @property requestState состояние отправки запроса
     */
    data class State(
        val phone: String = "",
        val formattedPhone: String = "",
        val validationError: String? = null,
        val requestState: RequestState = RequestState.Idle,
    ) {
        /**
         * Состояние HTTP запроса.
         */
        sealed interface RequestState {
            /** Запрос не выполняется. */
            data object Idle : RequestState
            /** Запрос в процессе. */
            data object Loading : RequestState
            /** Запрос завершён успешно. */
            data object Success : RequestState
            /** Запрос завершён с ошибкой. */
            data class Error(val message: String) : RequestState
        }
    }

    /**
     * Интенты (действия пользователя и системы).
     */
    sealed interface Intent {
        /**
         * Пользователь изменил номер телефона.
         *
         * @property phone новое значение поля ввода
         */
        data class PhoneChanged(val phone: String) : Intent

        /** Пользователь нажал кнопку отправки. */
        data object SubmitClicked : Intent

        /**
         * Запрос на отправку SMS завершён.
         *
         * @property result успех или ошибка
         */
        data class RequestCompleted(val result: Result<Unit>) : Intent
    }

    /**
     * Побочные эффекты.
     */
    sealed interface Effect {
        /**
         * Отправить запрос на получение SMS-кода.
         *
         * @property phone валидный номер телефона
         */
        data class SendSmsCode(val phone: String) : Effect
    }

    /**
     * События для внешних подписчиков.
     */
    sealed interface Event {
        /**
         * Запрос перехода к экрану ввода SMS-кода.
         *
         * @property phone номер на который отправлен код
         */
        data class NavigateToSmsCode(val phone: String) : Event
    }
}
```

## Reducer

```kotlin
/**
 * Reducer для обработки интентов экрана валидации телефона.
 *
 * Реализует чистую функцию преобразования состояния:
 * `(State, Intent) → (State, Effects)`.
 *
 * Не содержит побочных эффектов — вся асинхронная логика
 * делегируется в [PhoneValidationEffector] через [Effect].
 *
 * @param phoneFormatter форматтер номера телефона
 * @param phoneValidator валидатор номера телефона
 *
 * @see PhoneValidationTea.Intent для списка интентов
 * @see PhoneValidationTea.Effect для списка эффектов
 */
internal class PhoneValidationReducer(
    private val phoneFormatter: PhoneFormatter,
    private val phoneValidator: PhoneValidator,
) : DslReducer<
    PhoneValidationTea.State,
    PhoneValidationTea.Intent,
    PhoneValidationTea.Effect
>() {

    override fun ReducerContext.reduce(intent: PhoneValidationTea.Intent) {
        when (intent) {
            is Intent.PhoneChanged -> handlePhoneChanged(intent.phone)
            is Intent.SubmitClicked -> handleSubmitClicked()
            is Intent.RequestCompleted -> handleRequestCompleted(intent.result)
        }
    }

    /**
     * Обрабатывает изменение номера телефона.
     *
     * Форматирует номер, валидирует и обновляет состояние.
     */
    private fun ReducerContext.handlePhoneChanged(phone: String) {
        val digits = phone.filter { it.isDigit() }
        val formatted = phoneFormatter.format(digits)
        val error = phoneValidator.validate(digits)

        state {
            copy(
                phone = digits,
                formattedPhone = formatted,
                validationError = error,
            )
        }
    }
}
```

## Repository

```kotlin
/**
 * Репозиторий для работы с авторизацией.
 *
 * Предоставляет методы для:
 * - Отправки SMS-кода на номер телефона
 * - Верификации SMS-кода
 * - Управления сессией пользователя
 *
 * @see AuthRepositoryImpl для реализации
 * @see AuthDataSource для работы с API
 */
interface AuthRepository {

    /**
     * Запрашивает отправку SMS-кода на указанный номер.
     *
     * При успехе сервер отправляет 6-значный код на номер.
     * Повторный запрос возможен через 60 секунд.
     *
     * @param phone номер телефона в формате +7XXXXXXXXXX
     * @return [Result.success] при успешной отправке,
     *         [Result.failure] с [AuthException] при ошибке
     *
     * @throws AuthException.TooManyRequests при превышении лимита запросов
     * @throws AuthException.InvalidPhone при невалидном номере
     * @throws NetworkException при ошибке сети
     */
    suspend fun requestSmsCode(phone: String): Result<Unit>

    /**
     * Верифицирует SMS-код и авторизует пользователя.
     *
     * При успешной верификации:
     * - Сохраняет access и refresh токены
     * - Загружает профиль пользователя
     *
     * @param phone номер телефона в формате +7XXXXXXXXXX
     * @param code 6-значный SMS-код
     * @return [Result.success] с данными пользователя,
     *         [Result.failure] с [AuthException] при ошибке
     *
     * @throws AuthException.InvalidCode при неверном коде
     * @throws AuthException.CodeExpired при истёкшем коде
     * @throws NetworkException при ошибке сети
     */
    suspend fun verifySmsCode(phone: String, code: String): Result<User>

    /**
     * Выполняет выход из аккаунта.
     *
     * Очищает сохранённые токены и данные пользователя.
     * Не требует сетевого запроса.
     */
    suspend fun logout()

    /**
     * Проверяет наличие активной сессии.
     *
     * @return `true` если есть валидный access token
     */
    suspend fun isLoggedIn(): Boolean
}
```

## Use Case

```kotlin
/**
 * Use case для запроса SMS-кода авторизации.
 *
 * Инкапсулирует логику валидации номера телефона
 * и отправки запроса на получение кода.
 *
 * ## Ответственность
 * - Валидация формата номера телефона
 * - Проверка rate limiting
 * - Делегирование запроса в [AuthRepository]
 *
 * ## Использование
 * ```kotlin
 * val result = requestSmsCodeUseCase("+79991234567")
 * result.fold(
 *     onSuccess = { navigateToSmsInput() },
 *     onFailure = { error -> showError(error) }
 * )
 * ```
 *
 * @see AuthRepository.requestSmsCode
 */
abstract class RequestSmsCodeUseCase {

    /**
     * Запрашивает отправку SMS-кода.
     *
     * @param phone номер телефона в международном формате
     * @return [Result.success] при успешной отправке,
     *         [Result.failure] при ошибке валидации или сети
     */
    abstract suspend operator fun invoke(phone: String): Result<Unit>
}

/**
 * Реализация [RequestSmsCodeUseCase].
 *
 * @param authRepository репозиторий авторизации
 * @param phoneValidator валидатор номера телефона
 */
internal class RequestSmsCodeUseCaseImpl(
    private val authRepository: AuthRepository,
    private val phoneValidator: PhoneValidator,
) : RequestSmsCodeUseCase() {

    override suspend fun invoke(phone: String): Result<Unit> {
        // Валидация
        val validationError = phoneValidator.validate(phone)
        if (validationError != null) {
            return Result.failure(ValidationException(validationError))
        }

        // Запрос
        return authRepository.requestSmsCode(phone)
    }
}
```

## Data Source

```kotlin
/**
 * Источник данных для API авторизации.
 *
 * Выполняет HTTP запросы к эндпоинтам авторизации.
 * Обрабатывает сериализацию/десериализацию и маппинг ошибок.
 *
 * @see AuthDataSourceImpl для реализации с Ktor
 */
internal interface AuthDataSource {

    /**
     * POST /auth/request-code
     *
     * Запрашивает отправку SMS-кода.
     *
     * @param request тело запроса с номером телефона
     * @return пустой ответ при успехе
     * @throws HttpException при ошибке HTTP
     */
    suspend fun requestCode(request: RequestCodeRequest): RequestCodeResponse

    /**
     * POST /auth/verify-code
     *
     * Верифицирует SMS-код.
     *
     * @param request тело запроса с номером и кодом
     * @return токены авторизации и данные пользователя
     * @throws HttpException при ошибке HTTP
     */
    suspend fun verifyCode(request: VerifyCodeRequest): VerifyCodeResponse
}
```

## Compose Screen

```kotlin
/**
 * Экран ввода номера телефона.
 *
 * Отображает поле ввода с маской номера телефона,
 * кнопку отправки и индикатор загрузки.
 *
 * @param modifier модификатор для корневого элемента
 * @param component компонент с бизнес-логикой
 */
@Composable
internal fun PhoneValidationScreen(
    modifier: Modifier = Modifier,
    component: PhoneValidationComponent,
) {
    val state by component.uiState.collectAsState()

    PhoneValidationContent(
        modifier = modifier,
        state = state,
        onPhoneChanged = component::onPhoneChanged,
        onSubmitClick = component::onSubmitClick,
    )
}

/**
 * Stateless контент экрана валидации телефона.
 *
 * Используется для Preview и тестирования.
 *
 * @param modifier модификатор для корневого элемента
 * @param state текущее состояние UI
 * @param onPhoneChanged callback изменения номера
 * @param onSubmitClick callback нажатия кнопки
 */
@Composable
private fun PhoneValidationContent(
    modifier: Modifier = Modifier,
    state: PhoneValidationComponent.UiState,
    onPhoneChanged: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    // UI implementation
}
```

## Extension функции

```kotlin
/**
 * Форматирует строку как номер телефона.
 *
 * Преобразует последовательность цифр в читаемый формат:
 * `79991234567` → `+7 (999) 123-45-67`
 *
 * @receiver строка содержащая только цифры номера
 * @return отформатированный номер или исходная строка при ошибке
 *
 * @sample com.itapp.auth_impl.samples.formatAsPhoneSample
 */
fun String.formatAsPhone(): String {
    if (length < 11) return this

    return buildString {
        append("+")
        append(this@formatAsPhone[0])
        append(" (")
        append(this@formatAsPhone.substring(1, 4))
        append(") ")
        append(this@formatAsPhone.substring(4, 7))
        append("-")
        append(this@formatAsPhone.substring(7, 9))
        append("-")
        append(this@formatAsPhone.substring(9, 11))
    }
}

/**
 * Извлекает цифры из строки.
 *
 * Удаляет все символы кроме цифр 0-9.
 *
 * @receiver произвольная строка
 * @return строка содержащая только цифры
 *
 * ```kotlin
 * "+7 (999) 123-45-67".digitsOnly() // "79991234567"
 * "abc123def456".digitsOnly()       // "123456"
 * "no digits".digitsOnly()          // ""
 * ```
 */
fun String.digitsOnly(): String = filter { it.isDigit() }
```

## Enum и Sealed классы

```kotlin
/**
 * Ошибки авторизации.
 *
 * Используется для типизированной обработки ошибок
 * в слоях presentation и domain.
 *
 * @property message человекочитаемое описание ошибки
 */
sealed class AuthException(override val message: String) : Exception(message) {

    /**
     * Неверный формат номера телефона.
     *
     * Возникает при попытке отправить номер не соответствующий маске.
     */
    data object InvalidPhone : AuthException("Неверный формат номера телефона")

    /**
     * Неверный SMS-код.
     *
     * Возникает при вводе кода не совпадающего с отправленным.
     *
     * @property attemptsLeft оставшееся количество попыток
     */
    data class InvalidCode(val attemptsLeft: Int) : AuthException(
        "Неверный код. Осталось попыток: $attemptsLeft"
    )

    /**
     * Срок действия кода истёк.
     *
     * Код действителен 5 минут с момента отправки.
     */
    data object CodeExpired : AuthException("Срок действия кода истёк")

    /**
     * Превышен лимит запросов.
     *
     * @property retryAfterSeconds секунд до возможности повторного запроса
     */
    data class TooManyRequests(val retryAfterSeconds: Int) : AuthException(
        "Слишком много запросов. Повторите через $retryAfterSeconds сек."
    )
}
```
