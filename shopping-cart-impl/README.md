# Shopping Cart Impl Module

Implementation of the shopping cart feature.

## Overview

Basic shopping cart implementation with empty/non-empty state management.

## Architecture (TEA Pattern)

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │────▶│ Intent  │────▶│ Reducer │────▶│  State  │
│ Action  │     │         │     │         │     │         │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
                                                    │
                                                    ▼
                                              ┌─────────┐
                                              │  toUi() │
                                              │ mapping │
                                              └─────────┘
                                                    │
                                                    ▼
                                              ┌─────────┐
                                              │ UiState │
                                              └─────────┘
```

## Screen

### ShoppingCartScreen

**Component:** `presentation/component/ShoppingCartComponentImpl.kt`

**TEA Structure:**
```kotlin
// State
data class State(
    val isEmpty: Boolean = true
)

// Intents (currently empty - no user actions)
sealed interface Intent

// Effects (currently empty - no side effects)
sealed interface Effect

// Events (currently empty - no navigation)
sealed interface Event
```

## State Mapping

**Location:** `presentation/mapping/StateMapping.kt`

```kotlin
internal fun ShoppingCartTea.State.toUi(): ShoppingCartComponent.UiState =
    ShoppingCartComponent.UiState(isEmpty = isEmpty)
```

## Screen Structure

```
┌─────────────────────────────────────────────────────────────────┐
│                      ShoppingCartScreen                          │
│                                                                  │
│  ┌────────────────────────────────────────────────────────────┐ │
│  │                                                            │ │
│  │         isEmpty = true:                                    │ │
│  │         ┌─────────────────────────────────┐               │ │
│  │         │      🛒 Корзина пуста           │               │ │
│  │         │                                  │               │ │
│  │         │    Добавьте товары для          │               │ │
│  │         │    оформления заказа            │               │ │
│  │         └─────────────────────────────────┘               │ │
│  │                                                            │ │
│  │         isEmpty = false:                                   │ │
│  │         ┌─────────────────────────────────┐               │ │
│  │         │  Product 1          $99.00      │               │ │
│  │         │  Product 2          $49.00      │               │ │
│  │         │  ─────────────────────────────  │               │ │
│  │         │  Total:             $148.00     │               │ │
│  │         │      [ Оформить заказ ]         │               │ │
│  │         └─────────────────────────────────┘               │ │
│  │                                                            │ │
│  └────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Data Flow

```
ShoppingCartComponentImpl
    │
    │ store.state (StateFlow<State>)
    │
    │ .map { it.toUi() }
    ▼
StateFlow<UiState>
    │
    │ Compose collects
    ▼
ShoppingCartScreen renders UI
```

## Module Dependencies

- `shopping-cart-api` — interfaces
- `core-architecture` — TEA framework
- `core-navigation` — Decompose base classes
- `uikit` — shared UI components

## Future Enhancements

This module is a template. Future features may include:
- Add/remove items
- Update quantities
- Checkout flow
- Price calculations
- Promo codes
