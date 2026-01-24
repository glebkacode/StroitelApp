# Products API Module

Public interfaces for the products feature.

## Screens Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                      RootProductsComponent                       │
│                                                                  │
│  ┌─────────────────┐         ┌─────────────────────────────┐   │
│  │  ProductList    │────────▶│     ProductDetails          │   │
│  │   Component     │         │      Component              │   │
│  │                 │         │  ┌────────┐  ┌────────────┐ │   │
│  │ (Shelves with   │         │  │ Descr. │  │ Character. │ │   │
│  │  products)      │         │  │  Tab   │  │    Tab     │ │   │
│  └─────────────────┘         │  └────────┘  └────────────┘ │   │
│                              └─────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

## Components

### 1. ProductListComponent
Main screen showing products organized in shelves.

**Location:** `list/ProductListComponent.kt`

```kotlin
interface ProductListComponent : UiComponent {
    val model: StateFlow<Model>

    sealed interface Model {
        data object Loading : Model
        data class Content(val shelves: ShelvesRenderComponent) : Model
        data class Error(val throwable: Throwable) : Model
    }
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        openProductDetails: (Long, Long) -> Unit  // (shelfId, shelfItemId)
    ): ProductListComponent
}
```

---

### 2. ProductDetailsComponent
Product detail screen with tabs.

**Location:** `details/ProductDetailsComponent.kt`

```kotlin
interface ProductDetailsComponent : UiComponent {
    fun onDescriptionTabClicked()
    fun onCharacteristicsTabClicked()
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        shelfId: Long,
        shelfItemId: Long
    ): ProductDetailsComponent
}
```

---

### 3. ProductDescriptionComponent
Tab content showing product description.

**Location:** `details/description/ProductDescriptionComponent.kt`

```kotlin
interface ProductDescriptionComponent : UiComponent {
    interface Factory {
        operator fun invoke(componentContext: ComponentContext): ProductDescriptionComponent
    }
}
```

---

### 4. ProductCharacteristicsComponent
Tab content showing product characteristics.

**Location:** `details/characteristics/ProductCharacteristicsComponent.kt`

```kotlin
interface ProductCharacteristicsComponent : UiComponent {
    interface Factory {
        operator fun invoke(componentContext: ComponentContext): ProductCharacteristicsComponent
    }
}
```

---

### 5. RootProductsComponent
Container component that orchestrates products flow navigation.

**Location:** `root/RootProductsComponent.kt`

```kotlin
interface RootProductsComponent : UiComponent {
    interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootProductsComponent
    }
}
```

## Navigation Flow

| From | Event | To |
|------|-------|-----|
| ProductList | `openProductDetails(shelfId, itemId)` | ProductDetails |
| ProductDetails | Tab click | Description / Characteristics (internal) |
| ProductDetails | Back button | ProductList |

## Dependencies

- `shelves-render-api` — for `ShelvesRenderComponent` in ProductList
