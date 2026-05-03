# Структура модулей (API/Implementation Split)

Каждая фича разделена на два модуля:

```
feature-api/     # Публичные контракты (com.android.library)
feature-impl/    # Реализация       (com.android.library)
```

Все модули — стандартные Android Gradle Library модули (`com.android.library` или `com.android.application` для `composeApp`). Без KMP source-set'ов: код в `src/main/java/`, тесты в `src/test/java/`, ресурсы в `src/main/res/`.

### feature-api содержит:
- **Component интерфейсы** — контракт UI компонента
- **Component.Factory** — фабрика для создания компонента
- **Callbacks** — data class колбэков навигации, передаётся в Factory
- **UiState** — публичная модель состояния экрана
- **Domain модели** (если нужны другим модулям)
- **UseCase абстрактные классы** (если нужны другим модулям)

```kotlin
// feature-api/.../FeatureComponent.kt
interface FeatureComponent : UiComponent {
    val uiState: StateFlow<UiState>

    fun onInputChanged(text: String)
    fun onSubmitClicked()
    fun onBackClicked()

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            callbacks: Callbacks,
        ): FeatureComponent
    }

    data class Callbacks(
        val onSuccess: () -> Unit,
        val onBack: () -> Unit,
    )

    @Immutable
    data class UiState(
        val data: String = "",
        val isLoading: Boolean = false,
        val error: String? = null,
    )
}
```

**Правила контракта в `*-api`:**

- Component-интерфейс не упоминает корутины, UseCase или Decompose-навигацию.
- Все взаимодействия с навигацией — через `data class Callbacks(...)`, передаваемый в `Factory.invoke(...)`.
- `UiState` помечается `@Immutable` для оптимизации Compose-рекомпозиции.

### feature-impl содержит:

Файлы лежат в `feature-impl/src/main/java/com/itapp/<feature>_impl/...`. Тесты — в `feature-impl/src/test/java/...` с зеркальной структурой пакетов. Android-ресурсы — в `feature-impl/src/main/res/`.

```
feature-impl/src/main/java/com/itapp/<feature>_impl/
├── di/
│   └── FeatureGraph.kt
├── presentation/
│   └── feature_screen/
│       ├── component/
│       │   ├── FeatureComponentImpl.kt
│       │   └── FeatureScreen.kt
│       ├── mvi/
│       │   ├── FeatureStore.kt
│       │   └── FeatureStoreFactory.kt
│       └── mapping/
│           └── StateMapping.kt              # опционально
├── domain/
│   ├── model/
│   │   └── FeatureDto.kt
│   ├── repository/
│   │   └── FeatureRepository.kt
│   └── usecase/
│       ├── FeatureUseCase.kt (если не в api)
│       └── FeatureUseCaseImpl.kt
└── data/
    ├── api/
    │   ├── FeatureDataSource.kt
    │   └── FeatureDataSourceImpl.kt
    ├── model/
    │   ├── FeatureResponse.kt
    │   └── FeatureRequest.kt
    ├── mapper/
    │   └── FeatureMappingExt.kt
    └── repository/
        └── FeatureRepositoryImpl.kt
```

**Правила presentation-слоя:**

- На каждый экран — отдельная папка `presentation/<screen>/` с подпапками `component/`, `mvi/`, при необходимости `mapping/`.
- В `mvi/` лежат `FeatureStore.kt` (контракт: `Intent`, `State`, `Label`) и `FeatureStoreFactory.kt` (реализация: executor + reducer + внутренний `Msg`).
- `mapping/` создаётся только если `Store.State` отличается от публичного `UiState` из `*-api`.
- Component и Screen лежат рядом в `component/`, потому что Screen вызывается только из этого Component-а.

---
