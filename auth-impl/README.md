# Auth Impl Module

Implementation of the authentication feature.

## Screens

```
┌──────────────────────────────────────────────────────┐
│                  RootAuthComponent                    │
│  ┌─────────────────┐    ┌─────────────────┐          │
│  │ PhoneValidation │───▶│  Registration   │          │
│  │     Screen      │    │     Screen      │          │
│  └─────────────────┘    └─────────────────┘          │
└──────────────────────────────────────────────────────┘
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

**Purpose:** Enter phone number and choose login or register.

**Component:** `presentation/phone_validation/component/PhoneValidationComponentImpl.kt`

**Navigation:**
- `onLoginClicked()` → `Callbacks.onAuthSuccess()` (exit auth flow)
- `onRegisterClicked()` → `Callbacks.onRegisterClicked()` (push Registration)

---

### 2. RegistrationScreen

**Purpose:** Collect registration data (first name, last name, login, password, phone).

**Component:** `presentation/registration/component/RegistrationComponentImpl.kt`

**Navigation:**
- `onRegisterClicked()` → on success → `Callbacks.onRegistrationSuccess()` (pop)
- `onBackClicked()` → `Callbacks.onBack()` (pop)

---

## Domain Layer

### UseCases

| UseCase | Purpose | Location |
|---------|---------|----------|
| `ValidatePhoneNumberUseCase` | Validates phone number | `domain/usecase/ValidatePhoneNumberUseCaseImpl.kt` |

### Repository

```kotlin
interface AuthRepository {
    suspend fun validatePhone(dto: ValidationPhoneDto)
}
```

**Implementation:** `data/repository/AuthRepositoryImpl.kt`

### DataSource

**Implementation:** `data/api/AuthDataSourceImpl.kt`

---

## Module Dependencies

- `auth-api` — interfaces
- `core-architecture` — TEA framework
- `core-navigation` — Decompose base classes
- `uikit` — UI components
