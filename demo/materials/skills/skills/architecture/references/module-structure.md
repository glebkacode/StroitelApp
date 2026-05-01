# Структура модулей (API/Implementation Split)

Каждая фича разделена на два модуля:

```
feature-api/     # Публичные контракты
feature-impl/    # Реализация
```

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

- Component-интерфейс не упоминает ViewModel, корутины, UseCase или Decompose-навигацию.
- Все взаимодействия с навигацией — через `data class Callbacks(...)`, передаваемый в `Factory.invoke(...)`.
- `UiState` помечается `@Immutable` для оптимизации Compose-рекомпозиции.

### feature-impl содержит:

```
feature-impl/
├── di/
│   └── FeatureGraph.kt
├── presentation/
│   └── feature_screen/
│       ├── component/
│       │   ├── FeatureComponentImpl.kt
│       │   └── FeatureScreen.kt
│       ├── viewmodel/
│       │   └── FeatureViewModel.kt
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

- На каждый экран — отдельная папка `presentation/<screen>/` с подпапками `component/`, `viewmodel/`, при необходимости `mapping/`.
- `mapping/` создаётся только если `ViewModel.State` отличается от публичного `UiState` из `*-api`. Если ViewModel хранит сразу `UiState` — папку не создаём.
- Component и Screen лежат рядом в `component/`, потому что Screen вызывается только из этого Component-а.

---
