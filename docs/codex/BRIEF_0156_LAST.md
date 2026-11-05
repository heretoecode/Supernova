# Codex brief — the last data pieces for 0.156 screens (5 Nov 2026)

Four jobs, in order. Hand back once at the end.

**Rules** as `BRIEF_0156_FINAL.md`: your branch only; merge `claude/supernova-github-access-yfhe89` first and again
before you hand back; no PR, merge into Claude's branch or workflow dispatch; no screens, layouts, wording on screen,
`res/` or `.github/` workflow/Gradle edits; one statement per line, lines ≤ 120; `python3 tools/test_code_layout.py`
passes; a test for every change, never weaken one. **Compile every changed file (app and modules) before you hand
back** and list anything you could not compile. Claude builds every screen from what you deliver.

## 1. About › Credits, licences and version history (data only)

For the approved About page (owner notes 0.155 #32, mock-ups `docs/design/proposals/settings/26`–`32`):
- Build one complete, checked inventory of **every** open-source component in the shipped APK (app, MediaLib,
  FileCoreLibrary, native engine and its third-party libraries, Gradle dependencies) and **every** data service
  (TMDB, IntroDB, ChaptersDB, OpenSubtitles, Trakt, OMDb, JustWatch via TMDB, Put.io, and any others you find in the
  code). For each: name, version actually shipped, licence name, the full licence text (or the exact upstream file
  it comes from), the project or service web address (for the QR code), and what Supernova uses it for (one plain
  line). Derive versions from the lock files and dependency declarations, not from memory.
- Deliver it as a new file `assets/supernova/credits.json` (a new folder; `assets/foundation/` stays hands-off) with
  a small reader class (`about/AboutCredits.java`, no UI) and a test that checks every shipped dependency appears.
- **Version history:** add entries for 0.155 and 0.156 to the data the app's What's new page reads
  (`assets/foundation/release-history.json` — this one file may be edited for this), pointing at `docs/0.155/` and
  `docs/0.156/`. Do not move or rename any `docs/0.1xx/` folder.

## 2. Files not in the library — the online lookup

For Local Storage (owner notes 0.155 #13, decision of 4 Nov): Details on a file that is not in the library looks the
film or show up online from its file name.
- An asynchronous call, e.g. `UnlibraryLookup.start(context, fileUri, callbackExecutor, consumer)` returning a Job,
  with a result: kind (film / episode / show / unknown), TMDB id, title, year, season/episode when known, poster and
  backdrop URIs, and a confidence (confident / possible / none). Use the existing scraper and Fix match code paths.
- No library or database writes, no credentials in results, bounded time, cancellable, works offline (returns none).
- Separately, confirm which existing call "Add to library" should use for that one file, and say it in the hooks file.

## 3. Backup — the three choices

For Settings › System › Backup (owner notes 0.155 #29, mock-up `docs/design/proposals/settings/21`):
- Ready-made selections: `Everything` = every category except the private ones (`BackupCategory.isPrivate()`),
  `Settings only` = the settings categories, `Custom` = the current per-category choices. Expose them from the backup
  code (e.g. `BackupPresets`), with a persisted current choice (default Everything) and a migration that keeps an
  existing user's custom choices as Custom if they differ from Everything.
- A test that each preset contains exactly what it should and that private items are never in Everything.

## 4. Focus memory — the audit (report only, no screen code)

For the approved rule "every page returns focus to where you left it" (0.156 #43):
- Go through every page and panel in `leanback/`, `player/` and `settings/`: Home, Movies, TV Shows, custom pages,
  Details (each tab), Search, Network & Files and each section, Local Storage, Library Health, Library Scanning,
  Settings (each section), Playback Settings, Edit Page, Customise Home, every side panel and choice list.
- For each way in and out (Back, opening Details, opening a panel, switching tab, returning from the player, app
  resume), say what focus should return to and what the code actually does, with file:line.
- Write it as `docs/codex/findings/0156-focus-audit.md`: a table of the cases where focus is lost, each with a ready
  Robolectric test written in the report (not added to `src/`, so the build stays green). Claude fixes and adds them.

## Hand back

One line in `docs/codex/HANDOVER.md`, one report in `docs/codex/reports/`, the new APIs in
`docs/codex/findings/0156-ui-hooks.md`, and any patch registrations (add / replace) with SHA-256s. Then stop.
