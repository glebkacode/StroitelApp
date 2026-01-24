# Shelves Render API Module

Public interfaces for shelf rendering components.

## Overview

This module defines UI components for rendering shelves in different layouts. It's used by `products-impl` to display products.

```
┌─────────────────────────────────────────────────────────────────┐
│                    ShelvesRenderComponent                        │
│                      (root renderer)                             │
│                            │                                     │
│         ┌──────────────────┼──────────────────┐                 │
│         ▼                  ▼                  ▼                 │
│  ┌─────────────┐   ┌─────────────┐   ┌─────────────┐           │
│  │ Horizontal  │   │    Grid     │   │   Video     │           │
│  │ShelfComponent│   │ShelfComponent│   │ShelfComponent│           │
│  └─────────────┘   └─────────────┘   └─────────────┘           │
│         │                  │                  │                 │
│         └──────────────────┼──────────────────┘                 │
│                            ▼                                     │
│                    ┌─────────────┐                              │
│                    │ PosterModel │                              │
│                    │ (item)      │                              │
│                    └─────────────┘                              │
└─────────────────────────────────────────────────────────────────┘
```

## Components

### ShelvesRenderComponent
Root component managing multiple shelf renderers.

**Location:** `shelf/root/ShelvesRenderComponent.kt`

```kotlin
interface ShelvesRenderComponent : UiComponent {
    fun apply(models: List<ShelfModel>)
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        onItemClicked: (Long, Long) -> Unit,      // (shelfId, itemId)
        onShelfVisible: (Long) -> Unit,            // Analytics
        onShelfItemVisible: (Long) -> Unit         // Analytics
    ): ShelvesRenderComponent
}
```

---

### HorizontalShelfComponent
Horizontal scrollable shelf renderer.

**Location:** `shelf/horizontal/HorizontalShelfComponent.kt`

```kotlin
interface HorizontalShelfComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemClicked: (Long, Long) -> Unit,
            onShelfVisible: (Long) -> Unit,
            onShelfItemVisible: (Long) -> Unit
        ): HorizontalShelfComponent
    }
}
```

---

### GridShelfComponent
Grid layout shelf renderer.

**Location:** `shelf/grid/GridShelfComponent.kt`

```kotlin
interface GridShelfComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemClicked: (Long, Long) -> Unit,
            onShelfVisible: (Long) -> Unit,
            onShelfItemVisible: (Long) -> Unit
        ): GridShelfComponent
    }
}
```

---

### VideoShelfComponent
Video content shelf renderer.

**Location:** `shelf/video/VideoShelfComponent.kt`

```kotlin
interface VideoShelfComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onItemClicked: (Long, Long) -> Unit,
            onShelfVisible: (Long) -> Unit,
            onShelfItemVisible: (Long) -> Unit
        ): VideoShelfComponent
    }
}
```

## Models

### ShelfModel (Interface)
Base interface for shelf UI models.

**Location:** `shelf/root/model/shelf/ShelfModel.kt`

```kotlin
interface ShelfModel {
    val id: Long
    val header: String
}
```

---

### HorizontalShelfModel

**Location:** `shelf/horizontal/HorizontalShelfModel.kt`

```kotlin
data class HorizontalShelfModel(
    override val id: Long,
    override val header: String,
    val items: List<PosterModel>
) : ShelfModel
```

---

### GridShelfModel

**Location:** `shelf/grid/GridShelfModel.kt`

```kotlin
data class GridShelfModel(
    override val id: Long,
    override val header: String,
    val items: List<PosterModel>
) : ShelfModel
```

---

### ShelfItemModel (Interface)
Base interface for shelf item UI models.

**Location:** `shelf/root/model/shelfitem/ShelfItemModel.kt`

```kotlin
interface ShelfItemModel {
    val id: Long
}
```

---

### PosterModel
Product poster item.

**Location:** `shelf/root/model/shelfitem/PosterModel.kt`

```kotlin
data class PosterModel(
    override val id: Long,
    val title: String,
    val description: String,
    val url: String,
    val labelInfo: String,
    val price: Double
) : ShelfItemModel
```

## Callbacks

| Callback | Purpose |
|----------|---------|
| `onItemClicked(shelfId, itemId)` | User tapped on product item |
| `onShelfVisible(shelfId)` | Shelf became visible (analytics) |
| `onShelfItemVisible(itemId)` | Item became visible (analytics) |

## Usage

Used by `ProductListComponentImpl` to render shelves:

```kotlin
// In ProductListComponentImpl
shelvesRenderComponent.apply(
    models = shelves.map { it.toModel() }
)
```

## Dependencies

- `core-navigation` — Decompose base classes
