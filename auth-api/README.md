# Auth API Module

Public interfaces for the authentication feature.

## Screens Overview

```
┌─────────────────────┐     ┌─────────────────────┐
│  PhoneValidation    │────▶│    Registration     │
│     Component       │     │     Component       │
└─────────────────────┘     └─────────────────────┘
        │
        ▼
   Enter phone
   (login or register)
```

## Components

### 1. PhoneValidationComponent
First screen of auth flow — phone number input with login / register actions.

**Location:** `phone_validation/PhoneValidationComponent.kt`

```kotlin
interface PhoneValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onPhoneChanged(text: String)
    fun onLoginClicked()
    fun onRegisterClicked()

    data class Callbacks(
        val onAuthSuccess: () -> Unit,
        val onRegisterClicked: () -> Unit,
    )

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            callbacks: Callbacks,
        ): PhoneValidationComponent
    }

    data class UiState(val phone: String = "")
}
```

---

### 2. RegistrationComponent
Registration form with first name, last name, login, password, phone fields.

**Location:** `registration/RegistrationComponent.kt`

```kotlin
interface RegistrationComponent : UiComponent {
    val uiState: StateFlow<UiState>

    fun onFirstNameChanged(text: String)
    fun onLastNameChanged(text: String)
    fun onLoginChanged(text: String)
    fun onPasswordChanged(text: String)
    fun onPhoneChanged(text: String)
    fun onPasswordVisibilityToggle()
    fun onFieldFocusLost(field: Field)
    fun onRegisterClicked()
    fun onBackClicked()

    enum class Field { FIRST_NAME, LAST_NAME, LOGIN, PASSWORD, PHONE }

    data class Callbacks(
        val onRegistrationSuccess: () -> Unit,
        val onBack: () -> Unit,
    )

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            callbacks: Callbacks,
        ): RegistrationComponent
    }
}
```

---

### 3. RootAuthComponent
Container component that orchestrates auth flow navigation.

**Location:** `root/RootAuthComponent.kt`

```kotlin
interface RootAuthComponent : UiComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed interface Child {
        class PhoneValidationChild(val component: PhoneValidationComponent) : Child
        class RegistrationChild(val component: RegistrationComponent) : Child
    }

    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            onAuthSuccess: () -> Unit,
        ): RootAuthComponent
    }
}
```

## Navigation Flow

| From | Action | To |
|------|--------|-----|
| PhoneValidation | `onLoginClicked()` → `onAuthSuccess()` | Out of auth flow |
| PhoneValidation | `onRegisterClicked()` | Registration |
| Registration | `onRegistrationSuccess()` / `onBack()` | PhoneValidation |

## Dependencies

This module has no external feature dependencies — it only defines interfaces.
