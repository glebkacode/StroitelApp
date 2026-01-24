# Auth API Module

Public interfaces for the authentication feature.

## Screens Overview

```
┌─────────────────────┐     ┌─────────────────────┐     ┌─────────────────────┐
│  PhoneValidation    │────▶│  PasswordValidation │────▶│   SmsValidation     │
│     Component       │     │      Component      │     │     Component       │
└─────────────────────┘     └─────────────────────┘     └─────────────────────┘
        │                           │                           │
        ▼                           ▼                           ▼
   Enter phone              Enter password               Enter SMS code
                                                               │
                                                               ▼
                                                      ┌─────────────────┐
                                                      │    Products     │
                                                      └─────────────────┘
```

## Components

### 1. PhoneValidationComponent
First screen of auth flow - phone number input.

**Location:** `phone_validation/PhoneValidationComponent.kt`

```kotlin
interface PhoneValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onPhoneChanged(text: String)
    fun onNextClicked()

    data class UiState(val phone: String = "")
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        openPasswordScreen: (String) -> Unit  // Navigation callback
    ): PhoneValidationComponent
}
```

---

### 2. PasswordValidationComponent
Second screen - password input and validation.

**Location:** `password_validation/PasswordValidationComponent.kt`

```kotlin
interface PasswordValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onPasswordChanged(text: String)
    fun onNextClicked()
    fun onForgotPasswordClicked()
    fun onBackClicked()

    sealed class UiState(open val password: String) {
        data class Loading(override val password: String = "") : UiState(password)
        data class Content(override val password: String = "") : UiState(password)
        data class Error(override val password: String = "", val throwable: Throwable) : UiState(password)
    }
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        phone: String,                              // From previous screen
        openSmsScreen: (String, String) -> Unit     // Navigation callback
    ): PasswordValidationComponent
}
```

---

### 3. SmsValidationComponent
Final auth screen - SMS code verification.

**Location:** `sms_validation/SmsValidationComponent.kt`

```kotlin
interface SmsValidationComponent : UiComponent {
    val uiState: StateFlow<UiState>
    fun onSmsChanged(text: String)
    fun onContinueClicked()

    data class UiState(
        val loading: Boolean = true,
        val smsCode: String = ""
    )
}
```

**Factory:**
```kotlin
interface Factory {
    operator fun invoke(
        componentContext: ComponentContext,
        phone: String,
        password: String,
        openProducts: () -> Unit    // Navigation to Products flow
    ): SmsValidationComponent
}
```

---

### 4. RootAuthComponent
Container component that orchestrates auth flow navigation.

**Location:** `root/RootAuthComponent.kt`

```kotlin
interface RootAuthComponent : UiComponent {
    interface Factory {
        operator fun invoke(
            componentContext: ComponentContext,
            openProducts: Lazy<() -> Unit>    // Lazy callback to avoid circular deps
        ): RootAuthComponent
    }
}
```

## Navigation Flow

| From | Event | To |
|------|-------|-----|
| PhoneValidation | `openPasswordScreen(phone)` | PasswordValidation |
| PasswordValidation | `openSmsScreen(phone, password)` | SmsValidation |
| SmsValidation | `openProducts()` | Products (via RootComponent) |

## Dependencies

This module has no external feature dependencies - it only defines interfaces.
