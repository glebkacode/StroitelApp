# Feature: [Feature Name]

**Team:** [activation | hardware | moneta | player | retention | vitrina | core]
**Target module:** [mobile | kion-shared | both]
**Path:** `[real path to the module]`

## Context

[1-2 sentences: what problem it solves and why it's needed now.]

---

## Goal

[One sentence: what should work when the feature is done.]

---

## Requirements

### Functional
- [ ] [User can do X]
- [ ] [System does Y under condition Z]
- [ ] [Edge case handled: ...]

### Non-functional
- [Performance requirement, if any]
- [Expected test coverage — minimum 80%]
- [No new dependencies / specific constraint]

---

## Edge Cases

| Scenario | Expected Behavior |
|---|---|
| [What if X == null?] | [Return Y / show error Z] |
| [Network error] | [Retry / fallback] |
| [Empty state] | [Show placeholder] |

---

## Out of Scope

- [Something that sounds related but is not part of this task]
- [Future improvement — note it, don't implement now]

---

## Acceptance Criteria

- [ ] [Specific verifiable outcome 1]
- [ ] [Specific verifiable outcome 2]
- [ ] All existing tests pass without changes
- [ ] New logic is covered by unit tests (minimum 80%)
- [ ] Detekt passes with no new errors
