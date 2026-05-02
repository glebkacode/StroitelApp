# Question Bank by Feature Type

Use these to fill in gaps before writing the spec. Don't ask questions that can be answered by exploring the codebase.

---

## Universal (any feature)

- What should we name the feature? (for the file name)
- What problem does it solve?
- Target module: **mobile** (Android UI + logic) or **kion-shared** (KMP, shared business logic for Android/ATV/iOS)?
- Is there a deadline or target release?
- Is there existing code worth looking at?
- What does "done" look like for you?
- Which team owns the feature? (activation, hardware, moneta, player, retention, vitrina)

---

## Authentication / Sessions

- Where are tokens stored now? (EncryptedSharedPreferences, preferences module?)
- Is there a refresh token flow?
- What happens on token expiry — silent refresh or forced logout?
- Single session or multi-device?
- Are tokens managed by the existing auth module (activation/auth) or does it need a new flow?
- Is biometric or SSO needed?

---

## UI / Screen (Android)

- New screen or modification of an existing one?
- Fragment with ComposeView (setComposeContent) or pure Compose?
- Navigation: via AppNavigator + NavigationArguments or nav_graph.xml?
- Which feature team will own the screen? (activation, hardware, moneta, player, retention, vitrina)
- Is there a design mockup (Figma)? Does it use the KdsTheme design system?
- Is Dark Mode support needed (via KdsTheme)?
- Is tablet layout support needed?

---

## UI / Compose Component

- New Compose component or modification of an existing one?
- Component in the design-system module or local to the feature?
- Is there an existing design-system component for reference?
- Are animations needed (Compose Animation API)?
- Is a Preview needed for the component?

---

## API / Network

- New endpoint or using an existing one?
- **Mobile:** Retrofit via MgwRetrofit or another client?
- **kion-shared:** Ktor HttpClient via TvhClientProvider / MgwClientProvider?
- Is authorization required on the request?
- What is the payload shape? (Kotlinx Serialization models)
- Error handling: which status codes matter?
- Is a retry mechanism needed?

---

## Data / Persistence

- Local only, remote only, or sync?
- Room, SharedPreferences (preferences module), or DataStore?
- Is data needed only on Android or also in kion-shared (KMP)?
- Cache invalidation strategy?
- Is offline support needed?

---

## Notifications

- Local or push (FCM)?
- Is FCM already configured via the retention module?
- Is a new NotificationChannel needed?
- Deep link on tap — via the existing HandleDeeplinkUseCase?
- Permission request (POST_NOTIFICATIONS) — already handled?

---

## Background Tasks

- Triggered by event or on a schedule?
- WorkManager or Coroutines sufficient?
- Is a foreground service needed?
- Retry on failure?
- How to reflect completion in the UI?

---

## Tests

- Unit, integration, or UI?
- Existing test structure in the module to follow?
- **Mobile:** JUnit 5 (for new tests) or Kotest (for existing)? MockK for mocks, Turbine for Flow
- **kion-shared:** kotlin.test + runBlocking (commonTest source set)
- Minimum coverage — 80%?

---

## Android / Kotlin (mobile-specific)

- api/impl modular structure — new module or extension of an existing one?
- Is a Switcher (feature toggle) needed via ConfigGetter/ConfigRegistrar?
- Navigation: AppNavigator + NavigationArguments or Navigation Component (nav_graph)?
- Is integration with kion-shared (KMP) needed?
- Koin DI: where to register the module? (existing DI.kt or new)
- API: Retrofit via MgwRetrofit or another client?
- Compose or XML for UI? (current pattern: Fragment with ComposeView)
- Convention plugins: setup.androidapp.feature.api / setup.androidapp.feature.impl?

---

## KMP / kion-shared (shared business logic)

- Is the logic needed only for Android or also for ATV/iOS?
- New shared module or extension of an existing one?
- Network: Ktor HttpClient via TvhClientProvider or MgwClientProvider?
- Use case result: KionResult<T> (required for shared)
- Does the UseCase extend BaseCoroutineUseCase<Params, ReturnType>?
- Are expect/actual declarations needed for platform-specific code?
- Who is the consumer: mobile module (dependency on shared api) and/or iOS framework (export)?
- Source sets: commonMain only or androidMain/iosMain needed?
- Convention plugin: setup.kmp.core
- Tests: commonTest with kotlin.test + runBlocking
