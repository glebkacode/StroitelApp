# Структура модулей (API/Implementation Split)

Каждая фича разделена на два модуля:

```
feature-api/     # Публичные контракты
feature-impl/    # Реализация
```

### feature-api содержит:
- **Component интерфейсы** — контракт UI компонента
- **Component.Factory** — фабрика для создания компонента
- **Domain модели** (если нужны другим модулям)
- **UseCase абстрактные классы** (если нужны другим модулям)

```kotlin
// feature-api/.../FeatureComponent.kt
interface FeatureComponent : UiComponent {
    val uiState: StateFlow<UiState>

    fun onInputChanged(text: String)
    fun onSubmitClicked()

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onNavigate: (Event) -> Unit
        ): FeatureComponent
    }

    @Immutable
    data class UiState(
        val data: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
```

### feature-impl содержит:

```
feature-impl/
├── di/
│   └── FeatureGraph.kt
├── presentation/
│   └── feature_name/
│       ├── component/
│       │   ├── FeatureComponentImpl.kt
│       │   └── FeatureScreen.kt
│       ├── mapping/
│       │   └── StateMapping.kt
│       └── mvi/
│           ├── FeatureTea.kt
│           └── FeatureStoreFactory.kt
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

---