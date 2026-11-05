# GitHub cleanup — done by Claude, 5 November 2026

The owner asked Claude to do `BRIEF_GITHUB_CLEANUP.md` while Codex was busy. Documentation only. No app code,
workflows or Gradle files changed.

## New and rewritten
- `CLAUDE.md` (new, read automatically by Claude Code) and `AGENTS.md` (new): the front door.
- `README.md`: a plain GitHub front page.
- `PROJECT_STATUS.md`: rewritten for 0.155/0.156. The old page is now
  `docs/archive/root-history/PROJECT_STATUS_0.140-0.142.md`.
- `docs/README.md` (index) and `docs/archive/README.md`.

## Moved (only files nothing in the app, tests, workflows or tools reads)
- To `docs/archive/previews/`: `PREVIEW-*.md`/`.json`, `PREVIEW-DELIVERY.md`, `preview-4.1.4/`, `preview-4.1.5/`,
  `preview-4.1.7/`, `preview-next/`.
- To `docs/archive/early-planning/`: `MARK-EDITION-2.md`, `Releases/`, `Development/`, `project/`,
  `project-decisions/`, `qa/`, `investigations/`, `details-2026-10-27/`.
- `docs/...` links to them were rewritten in 40 current documents. Links inside `docs/0.135`–`0.154` were left
  byte-for-byte unchanged, because `assets/foundation/release-history.json` pins those files' hashes.
  Links in `docs/codex/CLEANUP_*` and pinned-commit URLs were also left alone; they are historical.

## Kept in place, and why
- `docs/0.135/`–`0.155/`: read by the app's What's new page and checked by hash.
- `foundation/`, `nova/`, `audits/`, `design/`, `codex/`, `next-qa/`, `next-refinement/`: read by the signing
  workflow, tests or tools.
- `next-version/`, `handovers/`, `release-notes/`: recent working records and the HTML history builder's inputs.
- `MARK-EDITION-3.md`: named in `res/values/mark_strings.xml`.
- `Future Development/`: allowed by `tools/check_imports_only.py`. Its open items are now Issues.

## Issues
- 13 labels and milestone `0.156` (number 1), from `docs/issues/LABELS.json` and `MILESTONES.json`.
- Tracker #31, with the 46 release items #32–#77 as sub-issues, all in milestone 0.156.
- Parked ideas and open decisions #78–#102.
- Source and numbers: `docs/issues/ISSUES.json`.

## Left for the owner
- Bring `main` up to date (a pull request the owner approves).
- Archive `heretoecode/aos-AVP` in its repository settings.
- The repository description, if wanted ("custom Android TV & Nvidia Shield media player" is still accurate).
