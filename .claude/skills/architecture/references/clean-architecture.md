# Clean Architecture

## Направление зависимостей

```
presentation → domain ← data
```

Domain слой **не зависит** ни от чего. Presentation и Data зависят от Domain.

---

## Domain Layer

### Domain Model

Чистые data-классы без зависимостей от фреймворков.

```kotlin
// domain/model/ProductDto.kt
data class ProductDto(
    val id: Long,
    val name: String,
    val price: Money,
    val category: Category
)

data class Money(
    val amount: BigDecimal,
    val currency: String
)
```

**Правила:**
- Без аннотаций сериализации (`@Serializable`, `@SerialName`)
- Без Android/iOS зависимостей
- Именование: `*Dto` для transfer objects

---

### Repository Interface

Контракт доступа к данным. Определяется в domain, реализуется в data.

```kotlin
// domain/repository/ProductRepository.kt
interface ProductRepository {
    suspend fun getProducts(): List<ProductDto>
    suspend fun getProduct(id: Long): ProductDto
    suspend fun saveProduct(product: ProductDto)
    fun observeProducts(): Flow<List<ProductDto>>
}
```

**Правила:**
- Только `suspend` функции или `Flow` для асинхронных операций
- Оперирует **domain моделями**, не data моделями
- Не знает об источнике данных (API, DB, cache)

---

### UseCase

Инкапсулирует одну бизнес-операцию.

**Абстрактный класс (интерфейс):**

```kotlin
// domain/usecase/GetProductsUseCase.kt
abstract class GetProductsUseCase : BaseCoroutineUseCase<GetProductsUseCase.Params, List<ProductDto>>() {

    data class Params(
        val categoryId: Long? = null,
        val limit: Int = 20
    )
}
```

**Реализация:**

```kotlin
// domain/usecase/GetProductsUseCaseImpl.kt
@Inject
class GetProductsUseCaseImpl(
    private val productRepository: ProductRepository
) : GetProductsUseCase() {

    override suspend fun run(input: Params): List<ProductDto> {
        val products = productRepository.getProducts()

        return products
            .let { list ->
                if (input.categoryId != null) {
                    list.filter { it.category.id == input.categoryId }
                } else {
                    list
                }
            }
            .take(input.limit)
    }
}
```

**Правила UseCase:**
| Правило | Описание |
|---------|----------|
| Наследование | `BaseCoroutineUseCase<Input, Output>` |
| Аннотация | `@Inject` на реализации |
| Единственность | Один UseCase = одна бизнес-операция |
| Params | data class с параметрами (или `Unit`) |
| Вызов | Как функция: `useCase(params)` |
| Возврат | `Result<Output>` (обёртка в BaseCoroutineUseCase) |
| Зависимости | Может использовать несколько репозиториев |
| Логика | Фильтрация, валидация, трансформации, комбинирование данных |

---

### Data Layer

### Data Model (Response/Request)

Модели для сериализации API.

```kotlin
// data/model/ProductResponse.kt
@Serializable
data class ProductResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("product_name")
    val name: String,
    @SerialName("price_cents")
    val priceCents: Int,
    @SerialName("currency_code")
    val currencyCode: String,
    @SerialName("category")
    val category: CategoryResponse
)

// data/model/ProductRequest.kt
@Serializable
data class ProductRequest(
    @SerialName("name")
    val name: String,
    @SerialName("price_cents")
    val priceCents: Int,
    @SerialName("currency_code")
    val currencyCode: String,
    @SerialName("category_id")
    val categoryId: Long
)
```

**Правила:**
- `@Serializable` для kotlinx.serialization
- `@SerialName` для маппинга JSON ключей
- Именование: `*Response` для ответов, `*Request` для запросов
- Отражает структуру API, не бизнес-логику

---

### Mapper

Extension-функции для преобразования data ↔ domain.

```kotlin
// data/mapper/ProductMappingExt.kt

// Response → Domain
internal fun ProductResponse.toDomain(): ProductDto {
    return ProductDto(
        id = id,
        name = name,
        price = Money(
            amount = BigDecimal(priceCents).divide(BigDecimal(100)),
            currency = currencyCode
        ),
        category = category.toDomain()
    )
}

internal fun CategoryResponse.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}

// Domain → Request
internal fun ProductDto.toRequest(): ProductRequest {
    return ProductRequest(
        name = name,
        priceCents = price.amount.multiply(BigDecimal(100)).toInt(),
        currencyCode = price.currency,
        categoryId = category.id
    )
}
```

**Правила Mapper:**
| Правило | Описание |
|---------|----------|
| Формат | Extension-функции |
| Именование | `Response.toDomain()`, `Dto.toRequest()` |
| Видимость | `internal` — не выходят за пределы модуля |
| Файл | `data/mapper/*MappingExt.kt` |
| Ответственность | Вся логика преобразования в одном месте |
| Null-safety | Обрабатывают nullable поля и дефолтные значения |

---

### DataSource

Интерфейс и реализация источника данных.

```kotlin
// data/api/ProductDataSource.kt
interface ProductDataSource {
    suspend fun getProducts(): List<ProductResponse>
    suspend fun getProduct(id: Long): ProductResponse
    suspend fun createProduct(request: ProductRequest): ProductResponse
}

// data/api/ProductDataSourceImpl.kt
@Inject
class ProductDataSourceImpl(
    private val httpClient: HttpClient
) : ProductDataSource {

    override suspend fun getProducts(): List<ProductResponse> {
        return httpClient.get("api/products").body()
    }

    override suspend fun getProduct(id: Long): ProductResponse {
        return httpClient.get("api/products/$id").body()
    }

    override suspend fun createProduct(request: ProductRequest): ProductResponse {
        return httpClient.post("api/products") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }
}
```

**Правила:**
- Интерфейс + реализация с `@Inject`
- Оперирует **data моделями** (Response/Request)
- Только работа с источником (HTTP, DB, файлы)
- Без бизнес-логики

---

### Repository Implementation

Реализует domain интерфейс, использует DataSource и Mapper.

```kotlin
// data/repository/ProductRepositoryImpl.kt
@Inject
class ProductRepositoryImpl(
    private val remoteDataSource: ProductDataSource
) : ProductRepository {

    override suspend fun getProducts(): List<ProductDto> {
        return remoteDataSource.getProducts().map { it.toDomain() }
    }

    override suspend fun getProduct(id: Long): ProductDto {
        return remoteDataSource.getProduct(id).toDomain()
    }

    override suspend fun saveProduct(product: ProductDto) {
        remoteDataSource.createProduct(product.toRequest())
    }
}
```

**Правила:**
- `@Inject` на классе
- Принимает DataSource (один или несколько)
- Использует mapper для преобразования
- Может содержать логику кэширования
- Возвращает **domain модели**

---