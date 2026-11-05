# Supernova — start here

This file is read automatically by Claude Code. It tells any new session, person or AI, where the project stands
and how the owner works. **If you are new: read this file, then the "Start here" list, before doing anything.**

## What Supernova is

- An Android TV media player for the owner's **NVIDIA Shield** (52" TV), forked from **NOVA Video Player**
  (Archos). TV only: no phone or tablet layouts.
- Package `app.supernova.player`. Current released version **0.155** (NOVA 6.6.4 engine), signed 4 Nov 2026 by
  run 41922310251 of `.github/workflows/supernova-0155-qa.yml`. The APK is the run's artifact; Google Drive
  delivery is not configured.
- Next version **0.156** is being built now: `docs/0.156/RELEASE_LIST.md` (45 approved items) and the GitHub
  milestone `0.156`.

## Start here, in order

1. This file.
2. `PROJECT_STATUS.md` — what is released, what is in progress, what is waiting for the owner.
3. `docs/0.156/RELEASE_LIST.md` — everything in the next version, with a Progress section.
4. `docs/0.155/OWNER_NOTES.md` — the owner's reports, numbered, with each decision and approval.
5. GitHub Issues (milestone `0.156`, labels below) — one issue per item.
6. `docs/codex/HANDOVER.md` and `docs/codex/HANDS_OFF.txt` — what Codex is doing and which files are whose.
7. `docs/REVISIT_LATER.md` — parked ideas and decisions deferred by the owner.

## How the owner works

- **British English.** Plain words. Short, simple replies when asked. Humour welcome.
- **The loop:** the owner sends photos or videos from the Shield → Claude writes each point into the owner notes
  → Claude makes mock-ups → the owner approves → **code only after the owner's go-ahead**.
- **Mock-ups:** send only final ones, as one numbered group, with captions only. No owner notes or annotations
  inside the designs. Panels sized to their content. Up to about five inline choices; more go in a side panel.
- **The owner approves every signing.** Never sign or publish without being asked.
- **Never paste keys, passwords or OAuth secrets** anywhere (chat, commits, issues). The OMDb key is not built into
  the app (owner decision; see `docs/REVISIT_LATER.md`).
- **No AI model names** in commits, code, comments, issues or PRs.
- **Readable code:** one statement per line, lines ≤ 120 characters, no crammed one-line methods.
  `tools/test_code_layout.py` enforces it; `docs/codex/REFORMAT_QUEUE.txt` lists old files still to reformat (their
  counts may only go down).
- No pull requests unless the owner asks.
- **Global look (owner, 5 Nov 2026):**
  - Full pages sit on the Space Black artwork, with content straight on it.
  - Pop-ups, menus and side panels are solid `#16181C` with no outline.
  - The focused item is filled white with black text and icon.
  - Missing information says "Unknown"; rows are never removed.

## Who does what

- **Claude:** everything the user sees (layouts, screens, focus, wording), integration of Codex's work, builds,
  tests, signing (when approved), owner notes and mock-ups. Works on `claude/supernova-github-access-yfhe89`.
- **Codex** (another AI, run by the owner): non-visual work — engine, data, diagnostics, scanning, backup — on its
  own branch (currently `codex/0155-code-2026-11-04`). Never touches main, `.github/`, Gradle or files marked
  hands-off. Its briefs are in `docs/codex/` (current: `BRIEF_0156_CODE.md`). It reports each finished Part in
  `docs/codex/HANDOVER.md`; Claude then builds, tests and merges it.
- Both can work at the same time because `docs/codex/HANDS_OFF.txt` splits the files.

## Branches and repositories

| What | Where |
| --- | --- |
| Working branch (all current work, builds and signing) | `claude/supernova-github-access-yfhe89` |
| Codex's branch | `codex/0155-code-2026-11-04` |
| `main` | The GitHub front page. Same as the working branch since 5 Nov 2026; brought forward after each signed release. Old main kept as `archive/main-2026-11-05`. |
| Native engine archive | `native-archive-0146` |
| `heretoecode/aos-MediaLib` | Fork used by **every** build. Keep. |
| `heretoecode/aos-AVP` | Not used. Archived by the owner on 5 Nov 2026 (read-only). |

Other `codex/*`, `automation/*` and `archive/*` branches are history; leave them alone.

## Build, test, sign

There is no Android SDK in Claude's container: everything builds on GitHub Actions.

1. **Conformance** — `.github/workflows/supernova-0155-conformance.yml` (manual dispatch on the working branch,
   about 35 minutes). Builds the unsigned APK and runs every JUnit/Robolectric test, lint and the tooling checks.
   A new dispatch cancels one already running.
2. **Failures** — `.github/workflows/report-test-failures.yml` with input `run_id` lists the failing tests.
3. **Signing** (only when the owner says so) — `.github/workflows/supernova-0155-qa.yml` with input
   `conformance_run_id` (a green conformance run of the exact same source). It signs with the established
   certificate in the `supernova-foundation-signing` environment. The signed APK is the run's artifact.
4. **Other tools:** `preview-renders.yml` renders real Android screens for mock-up checks; `lint-report.yml`;
   `report-artifact-digests.yml`. The `0146`/`0156` native workflows build the native engine.
5. Local checks Claude can run: `python3 tools/test_code_layout.py`.

The version history the app shows (About › What's new) comes from `assets/foundation/release-history.json`, which
points at `docs/0.135/` … `docs/0.154/`. **Do not move those folders.**

## Where things live

| Folder | What |
| --- | --- |
| `docs/0.1xx/` | Each version's records (release report, QA, owner notes). |
| `docs/design/` | Design rules and every mock-up (`proposals/<topic>/`). |
| `docs/codex/` | Briefs for Codex, its reports, findings and the hands-off list. |
| `docs/foundation/`, `docs/nova/` | Fork set-up, signing set-up, NOVA upstream notes. |
| `docs/archive/` | History only: old Preview-era files and early planning. See its README. |
| `docs/Future Development/` | The old FD-xxx idea register (its open items now live in Issues). |
| `docs/issues/` | The source files for the GitHub Issues, labels and milestones. |

## GitHub Issues

Labels: `bug`, `design`, `player`, `home-details-search`, `network-files`, `settings`, `diagnostics`, `engine`,
`docs`, `parked`, `decision-needed`, `codex`, `claude`. Milestone `0.156` holds the current version's items.
Parked ideas carry `parked` and no milestone; anything waiting for the owner carries `decision-needed`.

## Golden rule — keep it current

Conversations are not stored anywhere else. **At the end of every session** (or whenever something is decided):
update `PROJECT_STATUS.md`, the owner notes, the release list's Progress section and the affected Issues, then
commit and push. A new session must be able to carry on from GitHub alone.
