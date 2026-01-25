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
            @Provides teaFactory: TeaFactory,
            @Provides httpClient: HttpClient
        ): FeatureGraph
    }
}
```

---