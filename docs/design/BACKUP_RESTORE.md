# Backup & Restore — approved product specification (8 October 2026)

Status: **APPROVED DESIGN / TECHNICAL AUDIT PENDING / NOT AUTHORISED FOR IMPLEMENTATION**.
This is future work; do not add to the frozen fixes-only release without explicit authorisation.

## Goal
Device-to-device migration: export Supernova data to a user-chosen folder (e.g. attached M.2 USB media drive), move the drives to a new supported Android TV box, install Supernova and restore a familiar setup. Restore the saved library immediately, then refresh metadata and artwork quietly in the background. No need to re-identify previously matched titles.

## Export
- Settings three-panel design: left expandable **Backup & Restore** with indented **Export Backup** and **Restore Backup**. Centre shows selected task; right provides contextual information.
- Full backup, not individually toggled categories. Export opens Android's document/file picker to choose the output destination and filename after the user starts Export. Do not invent a separate location/filename setup step.
- Preserve library entries, filename-to-metadata IDs (including manually corrected movie/show/season/episode associations), watched status, playback progress, bookmarks, app preferences, custom pages, layouts, sorting, source configuration and any relevant non-secret state. Metadata/artwork may be re-fetched; do not require it all in the archive.
- **Never export** passwords, API/OAuth tokens, login credentials, session cookies or reusable secrets. Audit preference files called caches: some may hold actual choices. Do not migrate obsolete NOVA UI preferences that could disrupt the Supernova interface.
- **No encryption or password protection**. Before the export file picker, clearly warn that an unencrypted backup may expose filenames/paths, library, viewing history, settings and source names/addresses. Make clear that credentials are excluded. A privacy notice is also visible in the Export UI.

## Restore
- User selects a Supernova backup through Android's file picker; validate file integrity and compatibility **before** replacing data.
- Confirmation screen shows filename, date, originating Supernova version, included categories, compatibility and replacement/re-sign-in warnings. Explicit Restore action.
- Preserve existing setup safely until validation; recovery snapshot / rollback on failure is the agreed design direction and must be audited against existing implementation.
- Restore all saved data first, keeping the library usable; fetch missing artwork and metadata in background using preserved TMDB/IMDb or other verified provider IDs rather than rematching by filename.
- Automatically recognise and reconnect confidently matched relocated USB drives despite Android mount-path differences. Prompt to locate a media folder only if matching is uncertain. Never silently attach an ambiguous source.
- Missing drives do not block restore: retain library entries, matches and progress, mark media unavailable; reconnect later without destructive library clean-up or metadata rematching.
- Reauthentication is expected for services and protected network shares because secrets are excluded. Backup may contain readable RESTORE_INSTRUCTIONS.txt with these limitations.

## Compatibility baseline
- New **Supernova-specific backup format** starts compatibility support. No requirement to import legacy NOVA backups or older Supernova backup formats.
- Record backup-format version and originating application version. Newer releases should retain backward compatibility with this baseline when feasible; newer-format backups are accepted only if explicitly supported.
- Reject incompatible backups before any change to current data. Test actual full export/restore cycles, empty/invalid/0 KB archives, relocated paths, missing drives, rollback, no secret leakage and retained user choices.

## Approved 16:9 visual references
- [Export screen](approved-mockups/backup-restore/backup-restore-export-approved.png)
- [Selection and progress](approved-mockups/backup-restore/backup-restore-selection-progress-approved.png) — the storage browser depicted is illustrative; use Android file picker as approved later.
- [Confirmation and reconnection](approved-mockups/backup-restore/backup-restore-confirmation-reconnection-approved.png) — example drive counts/paths are illustrative.
- Generated images are authoritative only for expressly approved layout/content; no accidental sample numbers, menus or unrelated navigation are requirements.

## Technical audit still required
Trace existing export/import and backup manifests, real preferences vs caches, path mapping, source identities, metadata IDs, all current Supernova choices, versioned migration and secret filtering. Test on Android TV/Shield. Do not claim these behaviours already work until verified.

## Compatibility audit — explicit outstanding acceptance criteria (8 October recovery)
**APPROVED:** Preserve existing comprehensive backup/restore workflow, with full restore of all included non-secret data, no per-category checkbox workflow. `RESTORE_INSTRUCTIONS.txt` must be embedded inside the ZIP (and can additionally be made available separately where supported); document steps, version, excluded credentials and reconnection. The older vague “may contain” wording above is **SUPERSEDED** by this required instruction file.

**NEEDS VERIFICATION, before implementation:** Trace the real persistence and export/import path for each: (1) movie/episode watched status and resume positions, including version-switching and manually reset progress; (2) current Supernova Settings and named preferences; (3) custom Home rows, dynamic genre rules, visibility, ordering and sorting; (4) Movies/TV remembered Grid/List, filters/sort/order; (5) eventual single custom Library Page definition and wizard choices, only when implemented; (6) manually corrected title/episode metadata IDs and multi-version file associations; (7) network source/shortcut identities, portable volume matching and missing-source retention; (8) personal artwork vs safely refetchable art; (9) obsolete NOVA UI keys that should not restore into redesigned Supernova UI; (10) account/token/credential copies inside *all* databases, named/default preferences, source URIs and cached fields. Preserve only implemented data; future approved designs are not yet guaranteed backed up.

**Source audit evidence:** `MediaLibraryBackupService` currently writes `settings.json`, `named_preferences.json`, `media.db`, `credentials_db`, shortcut databases and selected artwork. `SettingsBackup.encode` serialises every default preference without secret filtering. Thus existing exports **do not satisfy** the approved no-credentials requirement. `VerifiedBackup` checks archive entries/CRC, export destination copy uses a digest, `SafeBackup.stage` checks size/database integrity and version, and `RestoreJournal` handles rollback. Preserve these defences and test actual end-to-end restore; do not infer complete compatibility from a ZIP containing `media.db`. Legacy backup format support remains **NOT REQUIRED** as stated above, so old secret-bearing imports may be rejected rather than reimported unsafely.

## 9 October 2026 — compatibility decision closed (authoritative clarification)
**CONFIRMED, no further compatibility design discussion needed:** **backup archive format 1.0** is the first supported compatibility baseline, regardless of Supernova app version. There is **no backward import requirement whatsoever** for NOVA archives or any pre-format-1.0 Supernova archive. From backup format 1.0 onward, subsequent Supernova releases must preserve the ability to restore supported older format-1.0-and-later backups, with explicit format migrations as required; don't treat “when feasible” above as permission to break that supported baseline. A newer unsupported archive may be rejected safely. The archive must still include the complete supported non-secret app state and embedded RESTORE_INSTRUCTIONS.txt; never include credentials. The source audit's credential-leak and feature-by-feature coverage findings are **implementation/validation tasks, not unresolved product decisions**. No implementation authorised.
