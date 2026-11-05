# Supernova — for Codex and other agents

Read `CLAUDE.md` first: it is the project's front door (what Supernova is, how the owner works, branches, builds).
This file adds only what an agent working beside Claude needs.

## Before you start

1. `CLAUDE.md` → `PROJECT_STATUS.md` → `docs/0.156/RELEASE_LIST.md`.
2. Your current brief in `docs/codex/` (today `BRIEF_0156_CODE.md`; parked briefs say so on their first line).
3. `docs/codex/HANDS_OFF.txt` — the files you may and may not touch. Claude edits screens at the same time.

## Rules

- Work only on your own branch (today `codex/0155-code-2026-11-04`). Never main, never Claude's branch.
- No pull requests, merges, rebases, force-pushes or workflow dispatches. Claude builds and tests.
- No UI: no layouts, drawables, colours, sizes or wording on screen. End each task in a small documented Java API
  and describe the call Claude must make in `docs/codex/findings/0156-ui-hooks.md`.
- Java 17, full imports, a JUnit/Robolectric test for every change.
- Readable code: one statement per line, braces as in the surrounding file, lines ≤ 120 characters.
  `tools/test_code_layout.py` must pass.
- Never paste keys or secrets. No AI model names in commits or code.

## Handing back

After each Part: commit, push, and add a line to `docs/codex/HANDOVER.md` ("Part A ready at <sha>"), plus a report
in `docs/codex/reports/`. Claude then builds, tests and merges it.

## Keep it current

At the end of every session update `PROJECT_STATUS.md` (your line under "In progress"), `docs/codex/HANDOVER.md`
and the affected GitHub Issues' notes in `docs/issues/`.
