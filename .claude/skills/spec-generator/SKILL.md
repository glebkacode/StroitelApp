---
name: spec-generator
description: >
  Generates a structured feature specification (.md file) for Claude Code consumption.
  Use this skill whenever the user wants to plan, document, or hand off a feature for
  AI-driven development. Triggers: "write a spec", "generate spec", "create specification",
  "spec for Claude Code", "plan a feature", "document a feature", "I want to implement X",
  "help me outline X", "let's plan X", "what's the best way to do X",
  or when the user describes a feature and needs a structured implementation plan.
  Always use this skill before starting implementation if the feature has more than 2 moving parts.
---

# Spec Generator for Claude Code

Creates a production-ready feature specification in Markdown, optimized for consumption and implementation by Claude Code.

## Workflow

### Step 1: User Interview

Before writing — clarify everything you don't know from context. Only ask questions that cannot be answered by exploring the codebase.

**Required:**
- Feature name (short, slug-friendly)
- What problem does it solve / why is it needed
- Target module: **mobile** (Android UI + logic) or **kion-shared** (KMP, shared business logic for Android/ATV/iOS)
- Feature team (activation, hardware, moneta, player, retention, vitrina)

**Find in the codebase yourself:**
- Existing files related to the feature
- Naming conventions
- Folder structure patterns
- Existing DI modules to extend

**Optional but valuable:**
- Known edge cases
- What is explicitly out of scope
- Acceptance criteria / definition of done

Question bank by feature type — see `references/interview-questions.md`.

---

### Step 2: Codebase Research

If running inside the project — gather context before writing the spec.

Use the target platform reference guide for file discovery and module structure:
- **mobile** → `references/platform-mobile.md`
- **kion-shared** → `references/platform-kmp.md`

---

### Step 3: Write the Spec

Use the template from `references/spec-template.md`.

**Critical rules:**
- DON'T ADD technical details (HOW) to current spec, only WHAT and WHY. This means: no code snippets, no API signatures or interface definitions (current or target), no file paths to change or delete, no implementation details, architecture diagrams, or migration steps. All of that belongs to the tech plan, not the spec.
- Do NOT copy any meta-instructions or comments from the template into the output spec file. The template is a structure guide, not content to reproduce verbatim.
- Reference only **real file paths** from the codebase (not invented ones)
- The `Out of scope` section is **mandatory** — AI tends to do more than asked
- `Acceptance criteria` — use checkboxes `- [ ]` only
- Keep every section concise — Claude Code reads the entire file as context
---

### Step 4: File Name and Location

```
spec.md
```

Where to save:
- `specs/feature-name/` — if the directory exists
- `./` (project root) — default

---

### Step 5: Launch Command

After creating the file — always finish with the exact command to run:

```bash
claude "Read spec/feature-name/spec.md and implement step by step.
Start with [first logical step from the spec].
After each file — confirm what was changed before moving on."
```

---

## Quality Checklist

Before delivering the spec, verify:

- [ ] All file paths exist in the real codebase (or are explicitly marked as NEW)
- [ ] "Out of scope" section is present and non-empty
- [ ] Acceptance criteria use checkboxes `- [ ]`
- [ ] No invented library names or non-existent APIs
- [ ] Edge cases table is populated (minimum 3 rows)
- [ ] Feature team is specified (activation/hardware/moneta/player/retention/vitrina)
- [ ] Spec conforms to the target platform reference (platform-mobile.md or platform-kmp.md)
---

## Reference Files
- `references/spec-template.md` — spec template to fill in
- `references/interview-questions.md` — question bank by feature type
- `references/platform-mobile.md` — Mobile (Android) reference: structure, DI, build, implementation order
- `references/platform-kmp.md` — kion-shared (KMP) reference: structure, DI, build, differences from mobile
