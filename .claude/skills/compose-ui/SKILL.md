---
name: compose-ui
description: Глубокая экспертиза по работе с Jetpack Compose. Используй при создании UI компонентов, экранов, реализации state management, анимаций, тем оформления или ревью Compose кода. Так используй этот skill если тебе нужно заняться оптимизацией работы Compose кода
---

# Что делать

Применяй:
- state hoisting
- unidirectional data flow
- remember / rememberSaveable
- derivedStateOf (если есть вычисляемые значения)

Минимизируй recomposition:
- избегай создания объектов в composable без remember
- выноси логику наружу

Используй Material3 API по умолчанию

# Работа со skill

- Для описания принципов которые лежат в Compose и какие нужно использовать при написании кода, смотри [principles.md](references/principles.md)
- Для понимания как писать код с высоким performance и лучшие практики по оптимизации performance в Jetpack Compose, смотри [performance.md](references/performance.md)
- Когда тебе нужно использовать код из дизайн-системы, лучшие практики смотри в [design-system.md](references/design-system.md)
- Для работы со state management в Compose (remember, rememberSaveable, derivedStateOf, snapshotFlow, @Stable/@Immutable, strong skipping и др.), смотри [state-managment.md](references/state-managment.md)
- Для работы с модификаторами (порядок цепочки, padding/size, clickable, Modifier.Node, graphicsLayer, semantics, чеклист ревью по `modifier`-параметру и др.), смотри [modifiers.md](references/modifiers.md)
- Для работы с анимациями (animate*AsState, AnimatedVisibility/AnimatedContent, updateTransition, Animatable, spring/tween/keyframes, shared elements, gesture-driven анимации, рецепты — shimmer, swipe-to-dismiss, parallax, predictive back и др.), смотри [animations.md](references/animations.md)
- Для понимания как лучше не делать UI код, какие существуют антипаттеры, смотри в [antipatterns](references/antipatterns.md)
