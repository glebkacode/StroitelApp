# Shopping Cart API Module

Public interfaces for the shopping cart feature.

## Overview

Simple shopping cart component for displaying cart state.

```
┌─────────────────────────────────────────┐
│         ShoppingCartComponent            │
│                                          │
│  ┌────────────────────────────────────┐ │
│  │         Empty Cart State           │ │
│  │              OR                    │ │
│  │         Cart with Items            │ │
│  └────────────────────────────────────┘ │
└─────────────────────────────────────────┘
```

## Component

### ShoppingCartComponent
Main shopping cart screen component.

**Location:** `ShoppingCartComponent.kt`

```kotlin
interface ShoppingCartComponent : UiComponent {
    val uiState: StateFlow<UiState>

    data class UiState(
        val isEmpty: Boolean = true
    )
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext
    ): ShoppingCartComponent
}
```

## UI State

| Property | Type | Description |
|----------|------|-------------|
| `isEmpty` | Boolean | Whether cart is empty |

## Navigation

This component is standalone and doesn't emit navigation events.

## Dependencies

- `core-navigation` — Decompose base classes
