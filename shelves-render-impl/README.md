# Shelves Render Impl Module

Implementation of shelf rendering components.

## Overview

This module implements UI components for rendering shelves in different layouts (horizontal, grid, video).

## Components

### ShelvesRenderComponentImpl
Root renderer that manages child shelf components.

**Location:** `presentation/root/ShelvesRenderComponentImpl.kt`

**Responsibilities:**
- Receives `List<ShelfModel>` via `apply()` method
- Creates appropriate child components based on model type
- Delegates rendering to child components
- Forwards callbacks to parent

```kotlin
class ShelvesRenderComponentImpl(
    componentContext: ComponentContext,
    private val onItemClicked: (Long, Long) -> Unit,
    private val onShelfVisible: (Long) -> Unit,
    private val onShelfItemVisible: (Long) -> Unit,
    private val horizontalShelfFactory: HorizontalShelfComponent.Factory,
    private val gridShelfFactory: GridShelfComponent.Factory,
    private val videoShelfFactory: VideoShelfComponent.Factory
) : BaseComponent(componentContext), ShelvesRenderComponent
```

---

### HorizontalShelfComponentImpl
Renders items in horizontal scrollable list.

**Location:** `presentation/horizontal/HorizontalShelfComponentImpl.kt`

**UI:** Horizontal `LazyRow` with `PosterModel` items.

---

### GridShelfComponentImpl
Renders items in grid layout.

**Location:** `presentation/grid/GridShelfComponentImpl.kt`

**UI:** Grid `LazyVerticalGrid` with `PosterModel` items.

---

### VideoShelfComponentImpl
Renders video content shelf.

**Location:** `presentation/video/VideoShelfComponentImpl.kt`

**UI:** Video player with poster items.

## Rendering Flow

```
ProductListComponent
    │
    │ shelves.map { it.toModel() }
    ▼
List<ShelfModel>
    │
    │ shelvesRenderComponent.apply(models)
    ▼
ShelvesRenderComponentImpl
    │
    │ For each model:
    │   - HorizontalShelfModel → HorizontalShelfComponent
    │   - GridShelfModel → GridShelfComponent
    │   - VideoShelfModel → VideoShelfComponent
    ▼
Child components render UI
    │
    │ User clicks item
    ▼
onItemClicked(shelfId, itemId)
    │
    │ (callback chain)
    ▼
ProductListComponent receives click
```

## Screen Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                      ProductListScreen                           │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                 ShelvesRenderComponent                     │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────────┐ │  │
│  │  │ HorizontalShelf: "Популярные товары"                  │ │  │
│  │  │ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐              │ │  │
│  │  │ │Item1│ │Item2│ │Item3│ │Item4│ │Item5│ ──────▶      │ │  │
│  │  │ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘              │ │  │
│  │  └──────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  │  ┌──────────────────────────────────────────────────────┐ │  │
│  │  │ GridShelf: "Каталог"                                  │ │  │
│  │  │ ┌─────┐ ┌─────┐ ┌─────┐                              │ │  │
│  │  │ │Item1│ │Item2│ │Item3│                              │ │  │
│  │  │ └─────┘ └─────┘ └─────┘                              │ │  │
│  │  │ ┌─────┐ ┌─────┐ ┌─────┐                              │ │  │
│  │  │ │Item4│ │Item5│ │Item6│                              │ │  │
│  │  │ └─────┘ └─────┘ └─────┘                              │ │  │
│  │  └──────────────────────────────────────────────────────┘ │  │
│  │                                                            │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Item Click Flow

```
User taps on PosterModel item
    │
    ▼
ShelfItemComponent.onClick()
    │
    │ onItemClicked(shelfId, itemId)
    ▼
HorizontalShelfComponent callback
    │
    │ onItemClicked(shelfId, itemId)
    ▼
ShelvesRenderComponent callback
    │
    │ onItemClicked(shelfId, itemId)
    ▼
ProductListComponentImpl
    │
    │ store.accept(Intent.ShelfItemClicked(shelfId, itemId))
    ▼
Reducer → Event.OpenProductDetails
    │
    ▼
openProductDetails(shelfId, itemId)
    │
    ▼
ProductDetailsScreen
```

## Module Dependencies

- `shelves-render-api` — interfaces and models
- `core-navigation` — Decompose base classes
- `uikit` — shared UI components
- Compose Multiplatform — UI framework
