# Auth Impl Module

Implementation of authentication feature with 3-step login flow.

## Screens

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                              RootAuthComponent                                │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐          │
│  │ PhoneValidation │───▶│ PasswordValid.  │───▶│  SmsValidation  │──▶Products│
│  │    Screen       │    │    Screen       │    │    Screen       │          │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘          │
└──────────────────────────────────────────────────────────────────────────────┘
```

## Architecture (TEA Pattern)

Each screen follows The Elm Architecture:

```
┌─────────┐     ┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │────▶│ Intent  │────▶│ Reducer │────▶│  State  │
│ Action  │     │         │     │         │     │         │
└─────────┘     └─────────┘     └─────────┘     └─────────┘
                                    │
                                    ▼
                              ┌─────────┐     ┌─────────┐
                              │ Effect  │────▶│Effector │────▶ Event (Navigation)
                              │         │     │         │
                              └─────────┘     └─────────┘
```

---

## Screen Details

### 1. PhoneValidationScreen

**Purpose:** Enter phone number for authentication.

**Component:** `presentation/phone_validation/component/PhoneValidationComponentImpl.kt`

**TEA Structure:**
```kotlin
// State
data class State(val phone: String = "")

// Intents
sealed interface Intent {
    data class PhoneChanged(val text: String) : Intent
    data object PhoneApply : Intent
}

// Effects
sealed interface Effect {
    data class PhoneApply(val phone: String) : Effect
}

// Events (Navigation)
sealed interface Event {
    data class OpenPasswordValidation(val phone: String) : Event
}
```

**UI State Mapping:** `State.toUi() → UiState(phone)`

**Navigation:** Emits `OpenPasswordValidation(phone)` → calls `openPasswordScreen(phone)`

---

### 2. PasswordValidationScreen

**Purpose:** Enter password and validate credentials.

**Component:** `presentation/password_validation/component/PasswordValidationComponentImpl.kt`

**TEA Structure:**
```kotlin
// State
sealed class State(open val data: PasswordValidationData) {
    data class Init(override val data: PasswordValidationData) : State(data)
    data class PasswordChanged(override val data: PasswordValidationData) : State(data)
    data class AuthFailed(override val data: PasswordValidationData, val throwable: Throwable) : State(data)
}

data class PasswordValidationData(val phone: String = "", val password: String = "")

// Intents
sealed interface Intent {
    data class PasswordChanged(val text: String) : Intent
    data object ValidatePasswordClicked : Intent
    data class LoginFailed(val throwable: Throwable) : Intent
}

// Effects
sealed interface Effect {
    data class ValidatePassword(val phone: String, val password: String) : Effect
}

// Events (Navigation)
sealed interface Event {
    data class OpenSmsValidation(val phone: String, val password: String) : Event
}
```

**UI State Mapping:**
- `State.Init` → `UiState.Loading`
- `State.PasswordChanged` → `UiState.Content`
- `State.AuthFailed` → `UiState.Error`

**Navigation:** Emits `OpenSmsValidation(phone, password)` → calls `openSmsScreen(phone, password)`

---

### 3. SmsValidationScreen

**Purpose:** Enter SMS code and complete authentication.

**Component:** `presentation/sms_validation/component/SmsCodeValidationComponentImpl.kt`

**TEA Structure:**
```kotlin
// State
data class State(
    val loading: Boolean = true,
    val phone: String = "",
    val password: String = "",
    val smsCode: String = "",
    val throwable: Throwable? = null
)

// Intents
sealed interface Intent {
    data class SmsCodeChanged(val text: String) : Intent
    data object LoginClicked : Intent
    data object AuthSuccess : Intent
    data class AuthFailed(val throwable: Throwable) : Intent
}

// Effects
sealed interface Effect {
    data class Login(val phone: String, val password: String, val smsCode: String) : Effect
}

// Events (Navigation)
sealed interface Event {
    data object OpenProducts : Event
}
```

**UI State Mapping:** `State.toUiState() → UiState(loading, smsCode)`

**Navigation:** Emits `OpenProducts` → calls `openProducts()` → navigates to Products flow

---

## Domain Layer

### UseCases

| UseCase | Purpose | Location |
|---------|---------|----------|
| `ValidatePhoneNumberUseCase` | Validates phone + password | `domain/usecase/ValidatePhoneNumberUseCaseImpl.kt` |
| `AuthUseCase` | Final authentication with SMS | `domain/usecase/AuthUseCaseImpl.kt` |

### Repository

```kotlin
interface AuthRepository {
    suspend fun validatePhone(dto: ValidationPhoneDto)
    suspend fun login(dto: LoginDto)
}
```

**Implementation:** `data/repository/AuthRepositoryImpl.kt`

### DataSource

```kotlin
interface AuthDataSource {
    suspend fun validatePhone(request: ValidatePhoneRequestDto)
    suspend fun login(request: LoginRequestDto)
}
```

**Implementation:** `data/api/AuthDataSourceImpl.kt`

---

## Navigation Summary

```
PhoneValidation
    │
    │ onNextClicked() → Intent.PhoneApply
    │ Reducer → Effect.PhoneApply
    │ Effector → Event.OpenPasswordValidation(phone)
    │ Component → openPasswordScreen(phone)
    ▼
PasswordValidation
    │
    │ onNextClicked() → Intent.ValidatePasswordClicked
    │ Reducer → Effect.ValidatePassword
    │ Effector calls ValidatePhoneNumberUseCase
    │ Success → Event.OpenSmsValidation(phone, password)
    │ Component → openSmsScreen(phone, password)
    ▼
SmsValidation
    │
    │ onContinueClicked() → Intent.LoginClicked
    │ Reducer → Effect.Login
    │ Effector calls AuthUseCase
    │ Success → Event.OpenProducts
    │ Component → openProducts()
    ▼
Products Flow (via RootComponent)
```

## Module Dependencies

- `auth-api` — interfaces
- `core-architecture` — TEA framework
- `core-navigation` — Decompose base classes
- `uikit` — UI components
