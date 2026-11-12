# Supernova — start here

This file is read automatically by Claude Code. It tells any new session, person or AI, where the project stands
and how the owner works. **If you are new: read this file, then the "Start here" list, before doing anything.**

## What Supernova is

- An Android TV media player for the owner's **NVIDIA Shield** (52" TV), forked from **NOVA Video Player**
  (Archos). TV only: no phone or tablet layouts.
- Package `app.supernova.player`. Current released version **0.156** (NOVA 6.6.4 engine), signed 6 Nov 2026 by
  run 42239912490 of `.github/workflows/supernova-0156-qa.yml` (artifact `supernova-0156-signed-qa`). Google Drive
  delivery is not configured.
- The owner is reviewing 0.156 on the Shield: notes in `docs/0.157/REVIEW_0156.md` (order from
  `docs/0.156/REVIEW_BY_PAGE.md`). Next version **0.157**: `docs/0.157/OWNER_NOTES.md`, Codex `BRIEF_0157.md`.

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
- **Mock-up frame (owner, 12 Nov 2026):** every mock-up picture gets a thin light-grey frame round its edge (not part of
  the design) so its edge shows on a dark screen.
- **Fewest mock-ups (owner, 12 Nov 2026; mindful of usage):** one picture covers as many things to assess as can be
  seen together on one screen; never one picture per item. Applying agreed rules (`docs/design/RULES.md`) to an
  approved page needs no mock-up (at most one sample screen); only genuinely new pieces are drawn. With each picture,
  one line in the message (not on the picture) says what is new to look at.
- **The owner approves every signing.** Never sign or publish without being asked.
- **This repository is public** (a GitHub fork of NOVA). Never put the owner's personal details in it: account
  names, email, location (country, city, time zone names), addresses, device names, real storage figures. Say "local
  time". Mock-ups and tests use made-up names (owner, 12 Nov 2026). The owner intends to make it private; it may go
  public again one day, so keep to this rule either way.
- **Never paste keys, passwords or OAuth secrets** anywhere (chat, commits, issues). The OMDb key is not built into
  the app (owner decision; see `docs/REVISIT_LATER.md`).
- **No AI model names** in commits, code, comments, issues or PRs.
- **Readable code:** one statement per line, lines ≤ 120 characters, no crammed one-line methods.
  `tools/test_code_layout.py` enforces it; `docs/codex/REFORMAT_QUEUE.txt` lists old files still to reformat (their
  counts may only go down).
- No pull requests unless the owner asks.
- **Diagnostics before coding (owner, 6 Nov 2026):** whenever the owner gives the go-ahead to code a version, first
  remind them to send the latest diagnostics report from the Shield, and read it for faults to fix in that version.
- **Freedom to rethink options (owner, 6 Nov 2026):** when redesigning, existing choices (from NOVA or earlier
  Supernova) are a starting point, not a constraint. Add, remove, merge or reword options and settings where it
  suits (e.g. Sleep timer becomes "Stop playing at" with clock times). Still propose; the owner approves.
- **Everything new the owner raises (features, bugs, anything) goes into 0.157.** 0.156 is fixed at its 45
  approved items (owner, 5 Nov 2026).
- **Consistency first (owner, 12 Nov 2026):** before asking the owner how something should look, read or behave, check
  whether the same thing is already decided elsewhere in the app (e.g. Details' "Resume S2 E2" wording, the open-in-app
  streaming button) and follow that. Only ask when nothing already settles it.
- **Design rulebook (owner, 12 Nov 2026): `docs/design/RULES.md` holds every agreed design rule.** Read it before
  drawing any page; build from it and only ask the owner what it does not settle; record every new decision there the
  same day. The summary below is kept for quick reference; RULES.md wins if they differ.
- **One keyboard (owner, 12 Nov 2026):** every text box uses the one shared keyboard; changes and fixes are made once
  and apply everywhere. Focus starts on T and stays on the key just pressed.
- **Global look (owner, 5 Nov 2026):**
  - **Direction: every page becomes plain charcoal `#16181C`, content straight on it; Space Black is being retired**
    (owner, 7 Nov 2026). **Not a blanket switch:** each page changes only after it has been reviewed on charcoal and
    its elements adjusted (as done for Files and Details). Pages not yet reviewed keep Space Black until then.
  - Pop-ups, menus and side panels are `#16181C` at 90% opacity with no outline, and the focused item is filled white with
    black text and icon (built in 0.157).
  - Every menu: each item has its own icon (no two alike in one menu), the current choice is a tick on the right,
    and the menu is only as wide as its longest item plus the standard padding (owner, 6 Nov 2026; 0.157).
  - In choice rows the chosen option sits in a small chip; single values are plain white. No blue for values.
  - **Artwork edge (owner, 7 Nov 2026; replaces the 28% edge/white ring):** every piece of artwork — both heroes
    (Home and Details), every row card (Home, Details tabs: Seasons & Episodes, Extras, More Like This), the player's
    Chapters/Episodes strip and library-page posters — has one thin white line exactly on its edge (about 60%) with a
    soft inner shade just inside it, so the edge shows on light and dark artwork alike. Every item has the edge all the
    time. **Focused artwork grows and gets a solid, thick white ring** (owner, 12 Nov 2026), with space around it so
    the grown card never crowds a line or heading. Hero cards show focus on their first button instead. Mock-ups
    `details-background/07-v17-top`, `tab-style/15-v3`. No blue caption bars.
  - **Focus and path (owner, 12 Nov 2026), everywhere incl. top bar and tabs:** white box with black text and icon =
    where the remote is; bright white underline = the step you just came from; dull underline = everything else on
    your path, including the open tab ahead of the remote. Moving along tabs switches the page; the top bar switches
    after a short pause. Mock-ups `tab-style/08`–`15` (v3). Focus mock-ups are checked by the owner reading them
    without captions.
  - **Text on the page (owner, 7 Nov 2026):** label/value pairs everywhere use soft near-white labels (`#C9D3E6`) and
    pure white, bolder values; "Unknown" stays quieter. Difference by weight, not dimness. Mock-up
    `details-background/08-v17-details-tab`.
  - **Sections and headings (owner, 12 Nov 2026):** every section heading has an icon. A heading sits clearly closer to
    its own content than to the section above (about 26 px below it, 64 px above it at 1080p), so it reads as a heading,
    not as part of the list. Sections are split by that gap, never by a line. Lines go only between rows you can select,
    never under the last row; plain information has no lines. Mock-up `search-flat/17`–`21`.
  - One row card size everywhere (16:9, 400×225 px at 1080p; focused 440×248).
  - Home and Details heroes the same size (1776×820 at 1080p), with the next row or tabs peeking below; Home has
    carousel dots. Tabs follow the focus-and-path rule above.
  - Missing information says "Unknown"; rows are never removed.

## Who does what

- **Claude:** everything the user sees (layouts, screens, focus, wording), integration of Codex's work, builds,
  tests, signing (when approved), owner notes and mock-ups. Works on `claude/supernova-github-access-yfhe89`.
- **Codex** (another AI, run by the owner): non-visual work — engine, data, diagnostics, scanning, backup — on its
  own branch (currently `codex/0157-2026-11-06`). Never touches main, `.github/`, Gradle or files marked
  hands-off. Its briefs are in `docs/codex/` (0.157: `BRIEF_0157.md`, `BRIEF_0157_B.md`, then `BRIEF_0157_C.md`, `BRIEF_0157_D.md`). It reports each finished Part in
  `docs/codex/HANDOVER.md`; Claude then builds, tests and merges it.
- Both can work at the same time because `docs/codex/HANDS_OFF.txt` splits the files.
- **Codex paused (owner, 12 Nov 2026):** the owner has no Codex usage for now. Do not assign Codex work; Claude does
  the engine/data jobs too (including unstarted `BRIEF_0157_D.md`) until the owner says Codex is back.

## Branches and repositories

| What | Where |
| --- | --- |
| Working branch (all current work, builds and signing) | `claude/supernova-github-access-yfhe89` |
| Codex's branch | `codex/0157-2026-11-06` (0.157); 0.156 work was on `codex/0155-code-2026-11-04` |
| `main` | The GitHub front page. Same as the working branch since 5 Nov 2026; brought forward after each signed release. Old main kept as `archive/main-2026-11-05`. |
| Native engine archive | `native-archive-0146` |
| `heretoecode/aos-MediaLib` | Fork used by **every** build. Keep. |
| `heretoecode/aos-AVP` | Not used. Archived by the owner on 5 Nov 2026 (read-only). |

Other `codex/*`, `automation/*` and `archive/*` branches are history; leave them alone.

## Build, test, sign

There is no Android SDK in Claude's container: everything builds on GitHub Actions.

1. **Conformance** — `.github/workflows/supernova-0156-conformance.yml` (manual dispatch on the working branch,
   about 35 minutes). Builds the unsigned APK and runs every JUnit/Robolectric test, lint and the tooling checks.
   A new dispatch cancels one already running.
2. **Failures** — `.github/workflows/report-test-failures.yml` with input `run_id` lists the failing tests.
3. **Signing** (only when the owner says so) — `.github/workflows/supernova-0156-qa.yml` with input
   `conformance_run_id` (a green conformance run of the exact same source). It signs with the established
   certificate in the `supernova-foundation-signing` environment. The signed APK is the run's artifact.
4. **Other tools:** `preview-renders.yml` renders real Android screens for mock-up checks; `lint-report.yml`;
   `report-artifact-digests.yml`. The `0146`/`0156` native workflows build the native engine.
5. Local checks Claude can run: `python3 tools/test_code_layout.py`.

The version history the app shows (About › What's new) comes from `assets/foundation/release-history.json`, which
points at `docs/0.135/` … `docs/0.156/`. **Do not move those folders.**

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
