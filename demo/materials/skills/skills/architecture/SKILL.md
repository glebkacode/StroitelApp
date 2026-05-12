---
name: architecture
description: Глубокая экспертиза по используемым архитектурным принципам и подходом которые ОБЯЗАТЕЛЬНО нужно использовать при разработке нового кода. Используй когда спрашивают "Разработать новый фукционал", "Спроектировать архитектуру", "Написать новый код" для приложения.
---

# Описание работы
Твоя задача реализация архитектуры придерживаясь принципам Clean Architecture, соблюдая слои:
- data слой
- domain слой
- presentation слой

# КРИТИЧЕСКИЕ ПРАВИЛА

В presentation-слое всех `*-impl` модулей используется **ИСКЛЮЧИТЕЛЬНО MVI на MviKotlin** (`Store<Intent, State, Label>` + `coroutineExecutorFactory` + `Reducer<State, Msg>`).
**Plain Kotlin ViewModel / `StateFlow + Channel` без Store — ЗАПРЕЩЕНЫ.** Один паттерн на все экраны без исключений «для простых случаев».
Эталоны в репо — `phone_validation` и `registration` (`*Store` + `*StoreFactory` + `*ComponentImpl`).
Подробности — `references/presentation-architecture.md`.

# Работа со skill

Всегда СТРОГО следуй принятой архитектуре в проекте и не отступай от неё.
Перед написанием кода читай соответствующий файл из `references/`:
- `presentation-architecture.md` — MVI через MviKotlin Store, Component, Screen
- `clean-architecture.md` — бизнес-логика, слои, направление зависимостей
- `module-structure.md` — раскладка `*-api` / `*-impl`, Android source set'ы
- `di.md` — Metro DependencyGraph
