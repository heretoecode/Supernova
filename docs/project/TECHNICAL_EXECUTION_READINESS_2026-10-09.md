# Technical execution readiness — 9 October 2026

**Status:** planning/implementation handover, not implemented. Prepared from approved design decisions and documented source audit findings. No code changes, no branch merges, no builds or QA performed. **Never modify `codex/post-4.1.7-shield-fixes` without separate explicit scope authorisation.** The accepted 4.1.7 final QA baseline and fixes-only work remain protected.

## 1. Backup & Restore — ready for engineering audit and subsequent authorised implementation
**Product decisions closed:** full backup, Android system picker, unencrypted ZIP with clear warning, `RESTORE_INSTRUCTIONS.txt` embedded, no per-category selection, full non-secret app state, restored manual metadata IDs and watch progress, quiet background artwork refresh, cautious USB source rematching, absent sources retained, validation/staging/rollback before mutation. **Archive format 1.0 is the earliest supported baseline. No NOVA/pre-1.0 import. Subsequent releases must restore supported 1.0+ archives.**

**Known source issue from earlier audit:** `MediaLibraryBackupService` exports `credentials_db`; `SettingsBackup.encode` serialises all default preferences without secret filtering. Do not ship a new 1.0 archive until these are corrected. Existing integrity and rollback mechanisms `VerifiedBackup`, `SafeBackup`, `RestoreJournal` must be preserved and verified.

**Engineering sequence:** trace archive producers and every persisted store (default/named preferences, media DB, shortcut/source DB, caches, artwork); define explicit allowlist of non-secret fields; reject/omit credential DB, usernames, passwords, keys, tokens, secret-bearing source URLs and authentication headers; version archive format at 1.0; include manifest/checksum and readable instructions; preserve manual metadata IDs, watched/resume/bookmarks, UI/home settings, source identity and supported custom pages actually implemented; handle paths and missing drives without rematching or destructive clean-up; implement migration handlers for future format changes; test export/import on two clean Shield installs and relocated media; verify invalid/truncated archives leave original data intact; inspect ZIP to confirm no secrets.

**Acceptance evidence:** archive file listing and redacted field inventory, exact format manifest, end-to-end restore report, tests for corrupt ZIP/0-byte/incompatible format/rollback, missing and relocated USB, preserved user choices and no credentials. Do not label completed until verified.

## 2. Existing Diagnostic Logging — broaden fault-finding, not a new viewer
**Approved:** retain the existing ON/OFF toggle and export; when ON collect correlated useful traces for crash/ANR, process/activity/player recreation, memory growth/leak suspicion, GC/heap pressure, UI focus graph and focus loss, card/animation/frame jank, startup and navigation, playback prepare/reprepare/buffer/seek/resume/decoder/media source, audio/subtitle, library indexing and metadata, network I/O, storage/USB, background tasks and provider errors. Time stamps, session ID, event IDs, thread/component and severity where appropriate; bounded logs and minimal overhead. OFF stops optional collection. No automatic upload or new diagnostics viewer.

**Absolute exclusions:** never export usernames, passwords, session cookies, API/OAuth/access/refresh tokens, authentication headers, signing/security keys or secret-bearing URIs; sanitise exception messages and URLs. Single-user test environment permits detailed non-secret technical events, not credential leakage.

**First regression scenario:** MobLand Season 2 Episode 4, ~02:32 Dublin local time on 9 October 2026 (date/time to confirm from report); while playing, returned unexpectedly to **Preparing Playback**. Await user's exported diagnostic ZIP before asserting root cause. Correlate events within a time window before/after with player state, activity lifecycle, decoder errors, source/network interruptions, memory pressure and reprepare reasons. Keep as **unverified observed incident**; no inferred fix.

**Acceptance evidence:** reproduced/fault-injected traces demonstrate causal sequence; toggle OFF/ON and ZIP export work; bounded log overhead; exported sample scrubbed for secrets. Tests are implementation checks, not new product decisions.

## 3. Production branding — design closed, export/build integration pending
**Authoritative files:** `assets/branding/Supernova_Space_Black_Blend_4K_UHD.png` and `assets/branding/Supernova_Double_Ring_Final_Visual_Reference.png`; previous candidates/archive are historical. Approved stationary concentric double-ring symbol, electric-blue glow, centred symbol + Roboto Light-like verified app wordmark lockup with approved size/gap; square app icon is ring alone; splash has stationary ring above wordmark with no artificial delay; library scanning/preparing playback trailing-glow animation is distinct. Do not redesign.

**Execution:** produce pixel-accurate platform assets from approved source/reference, including Android TV 320×180 banner, launcher icon variants/densities and splash resource; check correct image sizing/alpha, padding, contrast, legibility, no bright header flare; use verified app font; commit assets and integration only in authorised Foundation Release work, never the fixes-only branch. Verify actual launcher and first launch on Shield. Do not claim production-complete from mock-up images alone.

## 4. Foundation Release and future approved features
Foundation Release is identity/maintenance-only: unique package ID, new signing identity (verify existing private key custody; never commit key), clean install, app name/number/About, assets, upstream credits/licences and core function tests. Do not silently include custom Library Page, broad UI redesign, backup rewrite, diagnostics expansion or other features unless separately scheduled. The final APK requires runtime/native dependency and licence audit (especially FFmpeg and notices) against packaged binaries before public distribution.

**Future feature backlog:** one custom local Library Page, Search improvements, Multiple Versions, playback refinements, Smart Home Rows and other approved designs. Schedule release-by-release only after QA corrective baseline and Foundation Release sequencing are agreed. Keep Discovery, Profiles, AI, broader Library Health and other parked ideas parked.

## 5. Scope and documentation guardrails
- Current Shield fixes-only branch remains untouched; avoid mixing fixes, new features, identity/signing changes or speculative refactors.
- Wider Settings, Network & Files and full installed-APK walkthroughs are deliberately set aside.
- Read `PROJECT_STATUS.md`, `docs/qa/4.1.7-final-shield-qa.md`, `docs/design/README.md`, `docs/design/BACKUP_RESTORE.md`, `docs/design/ABOUT_SETTINGS.md`, `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md` before starting implementation.
- The user's diagnostic ZIP is not yet provided. **Remind the user conversationally before the next Codex handover; no scheduled reminder.**
