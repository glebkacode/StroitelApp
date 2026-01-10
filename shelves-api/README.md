# Shelves API Module

Domain models and use cases for shelves (product collections).

## Overview

This module defines the domain layer for shelves — collections of products displayed in different layouts (horizontal scroll, grid, etc.).

```
┌─────────────────────────────────────────────────────────────┐
│                       ShelfDto                               │
│                      (interface)                             │
│                          │                                   │
│            ┌─────────────┴─────────────┐                    │
│            ▼                           ▼                    │
│   ┌─────────────────┐        ┌─────────────────┐           │
│   │  DefaultShelfDto │        │  CatalogShelfDto │           │
│   │  (horizontal)    │        │  (grid + filters)│           │
│   └─────────────────┘        └─────────────────┘           │
│            │                           │                    │
│            └───────────┬───────────────┘                    │
│                        ▼                                    │
│              ┌─────────────────┐                            │
│              │DefaultShelfItemDto│                            │
│              └─────────────────┘                            │
└─────────────────────────────────────────────────────────────┘
```

## Domain Models

### ShelfDto (Interface)
Base interface for all shelf types.

**Location:** `domain/model/shelf/ShelfDto.kt`

```kotlin
interface ShelfDto {
    val id: Long
    val header: String
    val items: List<ShelfItemDto>
}
```

---

### DefaultShelfDto
Standard horizontal shelf with items.

**Location:** `domain/model/shelf/DefaultShelfDto.kt`

```kotlin
data class DefaultShelfDto(
    override val id: Long,
    override val header: String,
    override val items: List<DefaultShelfItemDto>
) : ShelfDto
```

---

### CatalogShelfDto
Grid shelf with filter options.

**Location:** `domain/model/shelf/CatalogShelfDto.kt`

```kotlin
data class CatalogShelfDto(
    override val id: Long,
    override val header: String,
    override val items: List<DefaultShelfItemDto>,
    val filterOptions: List<CatalogShelfFilterDto>
) : ShelfDto

data class CatalogShelfFilterDto(
    val id: Long,
    val option: String
)
```

---

### ShelfItemDto (Interface)
Base interface for shelf items.

**Location:** `domain/model/shelfitem/ShelfItemDto.kt`

```kotlin
interface ShelfItemDto {
    val id: Long
}
```

---

### DefaultShelfItemDto
Product item in a shelf.

**Location:** `domain/model/shelfitem/DefaultShelfItemDto.kt`

```kotlin
data class DefaultShelfItemDto(
    override val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val labelInfo: String,
    val price: Double
) : ShelfItemDto
```

## Use Cases

### GetShelvesUseCase
Fetches list of shelves from repository.

**Location:** `domain/usecase/GetShelvesUseCase.kt`

```kotlin
abstract class GetShelvesUseCase : UseCase<Unit, List<ShelfDto>>()
```

## Usage

This module is used by:
- `products-impl` — to display products in ProductListScreen
- `shelves-render-api/impl` — to render shelf UI components

## Dependencies

- `core-architecture` — for `UseCase` base class
