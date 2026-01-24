# Products Impl Module

Implementation of products browsing feature with list and detail screens.

## Screens

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           RootProductsComponent                              │
│                                                                              │
│  ┌───────────────────────┐              ┌───────────────────────────────┐  │
│  │   ProductListScreen   │─────────────▶│     ProductDetailsScreen      │  │
│  │                       │  Item click  │                               │  │
│  │  ┌─────────────────┐  │              │  ┌─────────┐  ┌────────────┐  │  │
│  │  │ HorizontalShelf │  │              │  │  Desc.  │  │  Charact.  │  │  │
│  │  └─────────────────┘  │              │  │   Tab   │  │    Tab     │  │  │
│  │  ┌─────────────────┐  │              │  └─────────┘  └────────────┘  │  │
│  │  │   GridShelf     │  │              └───────────────────────────────┘  │
│  │  └─────────────────┘  │                              │                  │
│  └───────────────────────┘◀─────────────────────────────┘                  │
│                                        Back                                 │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Architecture (TEA Pattern)

### ProductListScreen

**Component:** `presentation/list/component/ProductListComponentImpl.kt`

**TEA Structure:**
```kotlin
// State
data class State(
    val shelves: List<ShelfDto> = emptyList(),
    val isLoading: Boolean = true,
    val error: Throwable? = null
)

// Intents
sealed interface Intent {
    sealed interface UiIntent : Intent {
        data class ShelfItemClicked(val shelfId: Long, val shelfItemId: Long) : UiIntent
    }
    sealed interface InternalIntent : Intent {
        data class ShelvesLoaded(val shelves: List<ShelfDto>) : InternalIntent
        data class LoadingFailed(val error: Throwable) : InternalIntent
    }
}

// Effects
sealed interface Effect {
    data object LoadShelves : Effect
}

// Events (Navigation)
sealed interface Event {
    data class OpenProductDetails(val shelfId: Long, val shelfItemId: Long) : Event
}
```

**State → UI Model Mapping:**
- `isLoading=true` → `Model.Loading`
- `error!=null` → `Model.Error(throwable)`
- `shelves.isNotEmpty()` → `Model.Content(shelvesRenderComponent)`

---

### ProductDetailsScreen

**Component:** `presentation/details/component/ProductDetailsComponentImpl.kt`

Uses internal `StackNavigation` for tab switching:

```kotlin
sealed class Config {
    data object Description : Config()
    data object Characteristics : Config()
}
```

**Child Components:**
- `ProductDescriptionComponent` — description tab
- `ProductCharacteristicsComponent` — characteristics tab

---

## Data Flow

```
ProductListScreen
    │
    │ Component init → store.init()
    │ Reducer → Effect.LoadShelves
    │ Effector → GetShelvesUseCase()
    │ Success → Intent.ShelvesLoaded(shelves)
    │ Reducer → State(shelves=shelves, isLoading=false)
    │ Mapping → shelves.map { it.toModel() }
    │ ShelvesRenderComponent.apply(models)
    │
    │ User clicks item
    │ ShelvesRenderComponent → onItemClicked(shelfId, itemId)
    │ ProductListComponent → Intent.ShelfItemClicked
    │ Reducer → Event.OpenProductDetails
    │ Component → openProductDetails(shelfId, itemId)
    ▼
ProductDetailsScreen
    │
    │ Tab click → onDescriptionTabClicked() / onCharacteristicsTabClicked()
    │ Internal navigation → Config.Description / Config.Characteristics
    │
    │ Back button → RootProductsComponent.navigation.pop()
    ▼
ProductListScreen
```

## Presentation Layer Mapping

### ShelvesModelMapping
```kotlin
fun ShelfDto.toModel(): ShelfModel = when (this) {
    is DefaultShelfDto -> toModel()   // → HorizontalShelfModel
    is CatalogShelfDto -> toModel()   // → GridShelfModel
}

fun DefaultShelfDto.toModel(): HorizontalShelfModel
fun CatalogShelfDto.toModel(): GridShelfModel
```

### ShelfItemModelMapping
```kotlin
fun DefaultShelfItemDto.toModel(): PosterModel
```

## Module Dependencies

- `products-api` — interfaces
- `shelves-api` — shelf domain models (`ShelfDto`, `DefaultShelfDto`, etc.)
- `shelves-render-api` — shelf rendering (`ShelvesRenderComponent`, `ShelfModel`)
- `core-architecture` — TEA framework
- `core-navigation` — Decompose base classes
- `uikit` — UI components
