# Dependency Injection (Metro)

```kotlin
// di/FeatureGraph.kt
@DependencyGraph
interface FeatureGraph {

    val featureComponentFactory: Lazy<FeatureComponent.Factory>

    // Bindings: Interface → Implementation
    @Binds
    val FeatureUseCaseImpl.bind: FeatureUseCase

    @Binds
    val FeatureRepositoryImpl.bind: FeatureRepository

    @Binds
    val FeatureDataSourceImpl.bind: FeatureDataSource

    @Binds
    val FeatureComponentImpl.Factory.bind: FeatureComponent.Factory

    // Factory для создания графа с внешними зависимостями
    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides httpClient: HttpClient,
        ): FeatureGraph
    }
}
```

**Правила графа:**

- `@DependencyGraph` — на интерфейсе графа фичи.
- `@Binds` — связь интерфейса с реализацией; пишется как `val Impl.bind: Interface`.
- Component-фабрика выставляется наружу через `Lazy<Component.Factory>` (создание компонента ленивое).
- ViewModel **не** биндится в графе — она создаётся внутри Component-а вручную и время её жизни управляется `instanceKeeper`. Зависимости VM (UseCase) приходят в Component через DI и передаются конструктором VM.
- В `@DependencyGraph.Factory` через `@Provides` приходят только внешние зависимости (HttpClient и т.п.), которые предоставляет родительский граф (например, `AppGraph`).

---
