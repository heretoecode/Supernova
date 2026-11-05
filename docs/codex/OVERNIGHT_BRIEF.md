# Overnight brief for Codex — TV-only removal map and a deep bug hunt (both read-only)

**Tonight you change no code at all.** Both parts produce reports that the owner and Claude review before
anything is removed or fixed. Write only the files named below.

## Setup
1. Start from the tip of branch `claude/supernova-github-access-yfhe89`.
2. Create a new branch `codex/overnight-YYYY-MM-DD` (today's date; if it exists, add `-2`, `-3`, …). Push only
   to that branch. No pull request, merge, rebase or force-push.
3. Write only: `docs/codex/findings/tv-only-removal-map.md`, `docs/codex/findings/deep-hunt-*.md` and your
   report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`. Change nothing else. No Gradle, builds or tests.

## Part A — TV-only removal map
The owner has decided: **Supernova is an Android TV app only.** It will never run on phones or tablets. Your
previous map (`docs/codex/findings/dead-code-map.md`) found that NOVA's phone/tablet interface is still
reachable because `EntryActivity` routes there when the device is not in leanback (TV) mode, and that the
classic NOVA TV screens are reachable when `try_new_ui` is false (Supernova forces it true). Re-map with these
two facts treated as **removable**:
- **Phone/tablet interface:** everything reachable only from the non-leanback route (e.g. `MainActivity`,
  `browser/` screens, phone layouts in `res/layout*` without `-television`/leanback use, phone menus,
  `SubtitlesWizardActivity` and other phone-only activities, home-screen widgets if phone-only).
- **Classic NOVA TV screens** used only when `try_new_ui` is false or as superseded fallbacks
  (e.g. `VideoDetailsFragment`'s native path, old grid activities), **but check each carefully**: your previous
  map showed `VideoDetailsFragment` hosts the Supernova details page and the player launches it — list exactly
  what can go and what must stay.

For every candidate, give evidence (who references it, how it is reached today, why it is unreachable once
TV-only), size in lines, and what must change with it (manifest entries, `EntryActivity` routing, resources,
strings, tests that only cover it). Keep separate lists of things that **look** phone-only but are used by TV
screens (shared adapters, loaders, models, utils) — those stay.

Propose **removal stages**, safest first, each small enough to verify with one full build: for each stage, the
exact files, manifest/resource edits, tests to delete or move, and the risk. Include the 46 candidates from the
previous map where they fit. Note anything that would change behaviour on the TV (there should be nothing).
Write it to `docs/codex/findings/tv-only-removal-map.md`. Commit: `TV-only removal map (read-only)`.

## Part B — deep bug hunt of Supernova's own code
Last time the hunt was a scan with selected deep reads. This time: **read every line** of the files below,
method by method, in this priority order, and stop cleanly when time runs out (coverage is recorded, so
partial work still counts). Paths are under `src/main/java/com/archos/mediacenter/video/`.
1. `player/` (playback, HUD, subtitles, skip, Up Next, panels);
2. `leanback/details/Preview*` and `leanback/Preview*`, `leanback/Cinematic*`, `leanback/Home*`,
   `leanback/Library*`, `leanback/Custom*`, `leanback/EditPage*`, `leanback/SidePanel*`, `leanback/Subtitle*`,
   `leanback/TopNavigation*`, `leanback/Supernova*`, `leanback/Shared*`, `leanback/Standard*`,
   `leanback/TvButton*`, `leanback/MenuFoundation*`, `leanback/Official*`;
3. `leanback/settings/`, `leanback/filebrowsing/Universal*`, `leanback/Network*`, `leanback/Universal*`;
4. `streaming/`, `metadata/`, `foundation/`, `diagnostics/`, `utils/backup/`, then the rest of `utils/` that
   these call.
Skip anything Part A marks as phone-only or classic-only — it is being removed.

Look for real defects: **crashes** (null/empty assumed present, index out of range, bad casts, unguarded
parsing or I/O, use after a view/activity is destroyed); **leaks** (listeners, receivers, handlers, cursors,
streams, bitmaps, threads never released; static references to Activity/View/Context); **threading** (UI off
the main thread, disk/network/database on the main thread, unprotected shared state); **logic** (inverted
conditions, off-by-one, copy-paste mistakes, ms/s or dp/px mix-ups, state not reset between videos, settings
read but never applied, a setting's switch that cannot actually change); **focus/navigation** (D-pad focus
lost, trapped or landing on hidden views); **security/data** (secrets or credentials in logs, data lost on
error). Ignore style. Already known and recorded — do not re-report: U1, N1, N2, L1, P1, U2, L2 from
`docs/codex/findings/area*.md`.

Write one file per priority group: `docs/codex/findings/deep-hunt-1-player.md`, `…-2-screens.md`,
`…-3-settings-files.md`, `…-4-services.md`. Each has:
- the findings table:
  `| ID | File:line | Kind | Severity (crash / user-visible / minor) | Confidence (high / medium / low) | What goes wrong, for whom, when | Evidence (quote the lines, explain the path) | Suggested fix (described, not applied) |`
- a **coverage table** listing every file in that group: `| File | Lines | Fully read (yes/no) |`.
Rules: one row per distinct problem; confidence "low" when guessing; give the code path that triggers it (how a
user gets there). Commit per file: `Deep bug hunt <group> (read-only)`.

## Before you finish
Add a **new** report `docs/codex/reports/YYYY-MM-DD-<branch suffix>.md`: Part A — number of candidates, total
lines, number of stages; Part B — per group, files fully read / total, findings by severity and confidence;
anything odd you noticed. Confirm `git diff --name-only claude/supernova-github-access-yfhe89` lists only the
files named in Setup step 3. Commit and push.
