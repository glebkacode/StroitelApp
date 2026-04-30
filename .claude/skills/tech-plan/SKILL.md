---
name: tech-plan
description: >
  Generates a phased technical implementation plan with parallelizable tasks
  for Android mobile and KMP (kion-shared) projects. Use when the user wants to
  break down a feature into concrete, executable tasks before implementation.
  Triggers: "create tech plan", "make implementation plan", "break this down into tasks",
  "plan implementation", "how should I implement this", "tech plan",
  or when starting a complex feature that benefits from parallel execution.
---

# Tech Plan Generator

Creates a phased implementation plan with parallel task groups, optimized for execution by Claude Code agents running simultaneously.

## Key Difference from Spec Generator

|            | Spec (`/spec-generator`)         | Tech Plan (`/tech-plan`)            |
|------------|----------------------------------|-------------------------------------|
| Purpose    | **What** and **Why** to build    | **How** to build it                 |
| Output     | Requirements, acceptance criteria | Phases, tasks, execution commands   |
| Focus      | Completeness of requirements     | Parallelism and execution order     |
| Audience   | Decision-making                  | Implementation (Claude Code agents) |

A spec can serve as input for a tech plan. They complement each other.

---

## Workflow

### Step 1: Input

Accept one of:
- A **spec file** (`spec_*.md`) — extract tasks from it
- A **user description** — ask clarifying questions (reuse question bank from `../spec-generator/references/interview-questions.md`)
- A **ticket** description — ask user to paste the content from Jira

**Must know before planning:**
- Feature name
- Feature team (activation / hardware / moneta / player / retention / vitrina / core)

---

### Step 2: Codebase Research

Explore the codebase to find:
- Existing files to modify (real paths only)
- Module structure and naming conventions
- DI modules to extend
- Similar features to use as reference implementation

Platform-specific patterns:
- **mobile** — `../spec-generator/references/platform-mobile.md`
- **kion-shared** — `../spec-generator/references/platform-kmp.md`

---

### Step 3: Decompose into Phases and Tasks

**Core principle:** A phase boundary exists where there is a hard dependency. Within a phase, tasks MUST be independent — no shared files, no ordering assumptions.

#### Standard phase structures

**Mobile only:**

| Phase           | Content                          | Execution |
|-----------------|----------------------------------|-----------|
| 0               | API module: interfaces, models   | Sequential |
| 1               | Impl: data, domain, presentation | **Parallel** |
| 2               | DI, navigation, feature toggle   | Sequential |
| 3               | Tests                            | **Parallel** |

**KMP only:**

| Phase   | Content                                             | Execution |
|---------|-----------------------------------------------------|-----------|
| 0       | Shared API: interfaces, models, abstract use cases  | Sequential |
| 1       | Shared Impl: data layer, domain layer               | **Parallel** |
| 2       | DI module, build.gradle.kts                         | Sequential |
| 3       | Tests (commonTest)                                  | **Parallel** |

**Both (KMP + Mobile):**

| Phase   | Content                                  | Execution |
|---------|------------------------------------------|-----------|
| 0       | Shared API + Mobile API modules          | **Parallel** (if independent) |
| 1       | Shared Impl: data, domain                | **Parallel** |
| 2       | Mobile Impl: data, domain, presentation  | **Parallel** |
| 3       | DI wiring (shared + mobile)              | Sequential |
| 4       | Tests (shared + mobile)                  | **Parallel** |

> **Key insight:** Once interfaces are defined in Phase 0, all implementation layers can proceed in parallel because they depend only on abstractions, not on each other's implementations.

#### Parallelism rules

1. Two tasks are parallel ONLY if they touch **completely different files**
2. If tasks share a file (even build.gradle.kts), they go in the **same task** or **sequential phases**
3. DI registration always goes to a **separate sequential phase** after all implementations
4. Tests run **after** the code they test, but test tasks for different layers run **in parallel**

---

### Step 4: Write the Plan

Use the template from `references/tech-plan-template.md`.

**Rules for each task:**
- **Self-contained:** an agent can complete it without knowing about other parallel tasks
- **Real paths:** lists exact files to create or modify
- **Scoped:** includes a mini "done when" criteria
- **Sized:** S (1-2 files), M (3-5 files), L (6+ files)
- **No cross-task file overlap:** parallel tasks must NOT touch the same files

---

### Step 5: Generate Execution Commands

Output ready-to-run commands for each phase.

**Sequential phase:**
```bash
claude "Read tech_plan_<name>.md and execute Phase 0 completely. After each file — confirm what was changed."
```

**Parallel phase — option A (separate terminals):**
```bash
# Terminal 1:
claude "Read plan_<name>.md and execute ONLY Task 1.1 (Data Layer). Do not modify files listed in other tasks."

# Terminal 2:
claude "Read plan_<name>.md and execute ONLY Task 1.2 (Domain Layer). Do not modify files listed in other tasks."

# Terminal 3:
claude "Read plan_<name>.md and execute ONLY Task 1.3 (Presentation Layer). Do not modify files listed in other tasks."
```

**Parallel phase — option B (subagents within one session):**
```
claude "Read tech_plan_<name>.md. Execute Phase 1 tasks in parallel using Agent tool with isolation: worktree."
```

---

### Step 6: Save

File name: `plan.md`
Location: `specs/feature-name/` if the directory exists, otherwise project root.

---

## Quality Checklist

Before delivering the plan, verify:

- [ ] All file paths are real or explicitly marked as `(NEW)`
- [ ] Every parallel task touches **completely different files** (no overlaps)
- [ ] Phase dependencies are explicit and correct
- [ ] Each task has: file list, steps, done criteria, size
- [ ] DI registration is covered in a dedicated phase
- [ ] build.gradle.kts changes are listed where needed
- [ ] Execution commands are provided for every phase
- [ ] Out of scope section is present and non-empty

---

## Reference Files

- `references/tech-plan-template.md` — plan template
- `../spec-generator/references/interview-questions.md` — question bank
- `../spec-generator/references/platform-mobile.md` — Android patterns
- `../spec-generator/references/platform-kmp.md` — KMP patterns
