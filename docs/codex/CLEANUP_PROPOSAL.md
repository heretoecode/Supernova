# GitHub clean-up proposal — for the owner to approve

Prepared 2026-11-03. **Nothing here has been done.** Tick, cross out or edit each section; only approved
sections are carried out (by Codex overnight, or by Claude where noted). Every deletion is recoverable from
git history unless marked otherwise.

## 1. Close the 21 open pull requests (#10–#30)
All are finished history from the ChatGPT/Codex era (0.136–0.150). Their work either reached our branch or was
deliberately superseded. Closing a PR keeps it readable on GitHub; it deletes nothing.

| PR | Title | Branch |
|---|---|---|
| #10 | Next Supernova refinement: provisional 0.136 QA | `codex/supernova-next-refinement-2026-10-10` |
| #11 | Prepare manually approved GitHub Releases + Google Drive publisher | `automation/github-release-drive-publisher-2026-10-11` |
| #12 | Supernova 0.137 QA | `codex/supernova-0.137-qa-2026-10-11` |
| #13 | Supernova 0.138 | `codex/supernova-0.138-qa-2026-10-12` |
| #14 | docs: preserve 0.138 owner decisions | `docs/project-decisions-2026-10-12` |
| #15 | docs: maintenance investigation against signed 0.138 | `docs/maintenance-investigation-2026-10` |
| #16 | docs: 0.138 UI reference atlas | `docs/ui-reference-atlas-2026-10-12` |
| #17 | Supernova 0.139 | `codex/supernova-0.139-implementation-2026-10-13` |
| #18 | Supernova 0.140 | `codex/supernova-0.140-implementation-2026-10-16` |
| #19 | Supernova 0.141 | `codex/supernova-0.141-implementation-2026-10-22` |
| #20 | Supernova 0.142 | `codex/supernova-0.142-implementation-2026-10-23` |
| #21 | docs: verify Settings handover, Details stability | `docs/handover-investigation-2026-10-27` |
| #22 | Details: stabilise metadata/artwork, TMDB reviews | `codex/details-improvements-2026-10-27` |
| #23 | Supernova 0.143 | `codex/supernova-0.143-implementation-2026-10-27` |
| #24 | Supernova 0.144 | `codex/supernova-0.144-implementation-2026-10-28` |
| #25 | Supernova 0.145 | `codex/supernova-0.145-maintenance-2026-10-28` |
| #26 | Supernova 0.146 | `codex/supernova-0.146-upstream-completion-2026-10-28` |
| #27 | Supernova 0.147 | `codex/supernova-0.147-implementation-2026-10-29` |
| #28 | docs: Edit Library audit and proposal | `docs/edit-library-audit-and-proposal-2026-10-30` |
| #29 | Supernova 0.148 QA | `codex/supernova-next-implementation-2026-10-30` |
| #30 | Agreed fixes and Subtitles settings for 0.150 | `codex/agreed-fixes-2026-11-01` |

**Who:** Claude, after approval (closing PRs is visible on GitHub, so it is not left unattended).

## 2. Branches (52 today → 5)
**Keep:** `claude/supernova-github-access-yfhe89` (our working branch), `main` (default branch),
`previews/claude` (render tool), `v6.4` and `archive/pre-github-cleanup-2026-09-24` (deliberate archives).

**2a. Delete — already fully contained in our branch (nothing lost) — 27 branches:**
`codex/apk-build-412`, `codex/preview-4.1.6`, `codex/preview-4.1.7`, `codex/foundation-release`,
`codex/supernova-0.135`, `codex/supernova-next-refinement-2026-10-10`, `codex/supernova-0.137-qa-2026-10-11`,
`codex/supernova-0.138-qa-2026-10-12`, `codex/supernova-0.140-implementation-2026-10-16`,
`codex/supernova-0.141-implementation-2026-10-22`, `codex/supernova-0.142-implementation-2026-10-23`,
`codex/details-improvements-2026-10-27`, `codex/supernova-0.143-implementation-2026-10-27`,
`codex/supernova-0.144-implementation-2026-10-28`, `codex/supernova-0.145-maintenance-2026-10-28`,
`codex/supernova-0.146-upstream-completion-2026-10-28`, `codex/supernova-0.147-implementation-2026-10-29`,
`codex/supernova-next-implementation-2026-10-30`, `codex/agreed-fixes-2026-11-01`,
`codex/overnight-2026-11-03`, `codex/overnight-2026-11-03-2`, `codex/overnight-2026-11-03-3`,
`project/authoritative-cleanup`, `project/design-knowledge-preservation`, `project/full-history-reconciliation`,
`project/main-build-alignment`, `project/post-promotion-housekeeping`.

**2b. Not contained in our branch — keep their contents as tags, then delete the branch — 20 branches:**
A tag (`archive/<branch name>`) preserves every commit permanently without cluttering the branch list.
`codex/preview-4.1.4`, `codex/apk-build-fixes`, `codex/post-4.1.7-shield-fixes`,
`codex/supernova-0.139-implementation-2026-10-13`, `feature/supernova-app-analysis-and-mockup-6358464526881152498`,
`automation/github-release-drive-publisher-2026-10-11`, and the 14 `docs/…` branches
(`docs/maintenance-investigation-2026-10`, `docs/nova-upstream-deep-audit-2026-10-11`,
`docs/ui-reference-atlas-2026-10-12`, `docs/backup-restore-investigation-2026-10`,
`docs/details-tab-forensic-audit-2026-10-13`, `docs/playback-hud-functionality-audit-2026-10-16`,
`docs/network-files-blueprint-2026-10-23`, `docs/network-files-functionality-audit-2026-10-23`,
`docs/project-decisions-2026-10-12`, `docs/settings-functionality-audit-2026-10-26`,
`docs/handover-investigation-2026-10-27`, `docs/settings-from-scratch-blueprint-2026-10-26`,
`docs/edit-library-audit-and-proposal-2026-10-30`, `docs/network-files-settings-design-next-version-2026-10-29`).

**Who:** Claude, after the PRs are closed (a branch with an open PR must not be deleted first).

## 3. Old build workflows (58 files → 12)
Deleting a workflow file only stops it appearing in the Actions list; past runs, logs and their evidence stay.
**Keep:** `preview-renders.yml`, `report-artifact-digests.yml`, `report-test-failures.yml`,
`supernova-0152-*`, `supernova-0153-*` (templates for the next version), `supernova-0146-native-stack.yml`,
`supernova-0146-native-security.yml`, `supernova-0146-native-host.yml` (the recipe for the video engines —
see the warning below), `foundation-signing.yml`, `nova.yml` (upstream NOVA CI, already disabled).
**Delete (the other 46):** `supernova-0138` … `supernova-0151` conformance/qa/regression/ui files,
`supernova-0146-android-integration.yml`, `supernova-0146-api23.yml`, `supernova-0146-runtime-review.yml`,
`supernova135-*`, `supernova-next-*`, `supernova-pr30-conformance.yml`, `next-qa*.yml`,
`next-refinement-*.yml`, `agreed-fixes-validation.yml`, `subtitles-qa-validation.yml`,
`validate-preview417.yml`, `build-preview-apk.yml`, `build-apk.yml`.
The old version-specific scripts in `.github/build` and `.github/signing` stay for now: the build's safety
check protects them, and removing them needs that check redesigned (Claude, in a later version).

**Who:** Codex.

## 4. Old screenshots and renders in `docs/` (about 110 MB of 145 MB)
Tests and the build use only JSON/Markdown files in `docs/` — never the images — so all text stays.
**Delete images (png/jpg/webp/gif/mp4) only, in:** `docs/0.138` … `docs/0.149` (about 89 MB),
`docs/next-version` (14 MB), `docs/details-2026-10-27` (6 MB).
**Keep:** `docs/design` (the approved designs and current mock-ups), `docs/foundation`, every `.md`/`.json`.
Where a Markdown file links to a deleted image, Codex replaces the link with
`(image removed 2026-11; in git history)` rather than leaving a broken link.

**Note:** the files disappear from the project, but git keeps a copy in its history, so the repository download
(about 406 MB) does not shrink unless history is rewritten — a separate, irreversible decision not proposed here.

**Who:** Codex.

## 5. Leftovers from upstream NOVA and loose files
- Delete `icon_sources/unused/` (115 icons, unused by name).
- Delete `src/noamazon/play/` (21 MB: NOVA's Google Play listing text and phone/tablet screenshots — not
  Supernova, not part of the APK).
- Delete `ic_launcher-playstore.png`, `ic_launcher-web.png`, `nova-playstore.png` (NOVA store icons, unused).
- Move `doc/` (NOVA's technical notes) to `docs/nova/`, and the top-level `buffer.md` to
  `docs/nova/buffer-supernova.md` (it differs from `doc/buffer.md`); update the two test comments that
  mention `Video/doc/…`.

**Who:** Codex.

## ⚠ Not clean-up, but found while preparing this
The ready-made video engines every build downloads (GitHub run 40843685431) **expire on 27 January 2027**;
after that, builds fail. Fix: store them permanently (e.g. a GitHub Release) and point the build there.
Added to the 0.154 list for Claude — not a Codex job.
