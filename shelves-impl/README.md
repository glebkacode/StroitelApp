# Shelves Impl Module

Implementation of shelves business logic — data fetching and mapping.

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         Domain Layer                             │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              GetShelvesUseCaseImpl                       │    │
│  │                       │                                  │    │
│  │                       ▼                                  │    │
│  │              ShelvesRepository                           │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                          Data Layer                              │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │              ShelvesRepositoryImpl                       │    │
│  │                       │                                  │    │
│  │                       ▼                                  │    │
│  │              ShelvesDataSource                           │    │
│  │                       │                                  │    │
│  │                       ▼                                  │    │
│  │           ShelfDtoResponse.toDomain()                    │    │
│  │                (mapping)                                 │    │
│  └─────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
```

## Domain Layer

### GetShelvesUseCaseImpl
Fetches shelves from repository.

**Location:** `domain/usecase/GetShelvesUseCaseImpl.kt`

```kotlin
class GetShelvesUseCaseImpl(
    private val shelvesRepository: ShelvesRepository
) : GetShelvesUseCase() {

    override suspend fun run(input: Unit): List<ShelfDto> {
        return shelvesRepository.getShelves()
    }
}
```

### ShelvesRepository (Interface)

**Location:** `domain/repository/ShelvesRepository.kt`

```kotlin
interface ShelvesRepository {
    suspend fun getShelves(): List<ShelfDto>
}
```

## Data Layer

### ShelvesRepositoryImpl
Coordinates data source and mapping.

**Location:** `data/repository/ShelvesRepositoryImpl.kt`

```kotlin
class ShelvesRepositoryImpl(
    private val remoteDataSource: ShelvesDataSource
) : ShelvesRepository {

    override suspend fun getShelves(): List<ShelfDto> {
        return remoteDataSource.getShelves().map { it.toDomain() }
    }
}
```

### ShelvesDataSource (Interface)

**Location:** `data/api/ShelvesDataSource.kt`

```kotlin
interface ShelvesDataSource {
    suspend fun getShelves(): List<ShelfDtoResponse>
}
```

### ShelvesDataSourceImpl
HTTP client implementation (Ktor).

**Location:** `data/api/ShelvesDataSourceImpl.kt`

## Data Models (Response DTOs)

### ShelfDtoResponse

**Location:** `data/model/ShelfDtoResponse.kt`

```kotlin
@Serializable
data class ShelfDtoResponse(
    val id: Long,
    val type: String,           // "default" | "catalog"
    val header: String,
    val filterOptions: List<FilterDtoResponse>?,
    val items: List<SelfItemDtoResponse>
)
```

### SelfItemDtoResponse

**Location:** `data/model/SelfItemDtoResponse.kt`

```kotlin
@Serializable
data class SelfItemDtoResponse(
    val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val labelInfo: String,
    val price: Double
)
```

### FilterDtoResponse

**Location:** `data/model/FilterDtoResponse.kt`

```kotlin
@Serializable
data class FilterDtoResponse(
    val id: Long,
    val option: String
)
```

## Mapping (Response → Domain)

### ShelvesMappingExt

**Location:** `data/mapper/ShelvesMappingExt.kt`

```kotlin
fun ShelfDtoResponse.toDomain(): ShelfDto {
    return when (type) {
        "default" -> toDefaultShelf()
        "catalog" -> toCatalogShelf()
        else -> throw IllegalArgumentException("Unknown shelf type: $type")
    }
}
```

### DefaultShelfMappingExt

**Location:** `data/mapper/DefaultShelfMappingExt.kt`

```kotlin
fun ShelfDtoResponse.toDefaultShelf(): DefaultShelfDto
fun SelfItemDtoResponse.toDefaultShelfItem(): DefaultShelfItemDto
```

### CatalogShelfMappingExt

**Location:** `data/mapper/CatalogShelfMappingExt.kt`

```kotlin
fun ShelfDtoResponse.toCatalogShelf(): CatalogShelfDto
fun FilterDtoResponse.toCatalogFilterOption(): CatalogShelfFilterDto
```

## Data Flow

```
ProductListComponent
    │
    │ Effect.LoadShelves
    ▼
GetShelvesUseCaseImpl.run(Unit)
    │
    ▼
ShelvesRepositoryImpl.getShelves()
    │
    ▼
ShelvesDataSourceImpl.getShelves()
    │ (HTTP request)
    ▼
List<ShelfDtoResponse>
    │
    │ .map { it.toDomain() }
    ▼
List<ShelfDto>
    │
    │ (returned to UseCase → Component)
    ▼
ProductListComponent receives shelves
```

## Module Dependencies

- `shelves-api` — domain interfaces and models
- `core-architecture` — base classes
- `ktor-client-core` — HTTP client
- `kotlinx-serialization-json` — JSON parsing
