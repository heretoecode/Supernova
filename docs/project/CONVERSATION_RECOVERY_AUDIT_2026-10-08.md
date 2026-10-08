# Conversation recovery audit — 7–8 October 2026

**Status: DOCUMENTATION RECOVERY COMMITTED TO MAIN; SOURCE-HISTORY COVERAGE LIMITED.**
**Authority:** `PROJECT_STATUS.md` and the frozen `docs/qa/4.1.7-final-shield-qa.md` remain authoritative for implementation. All decisions here are planning records, not an instruction to start coding, change signing/version, merge branches or expand the fixes-only release.

## Scope and method
Reviewed the recoverable project conversation context, including the recent 7–8 October dialogue and supplied summaries of earlier Supernova design discussions. Compared to `main` copies of `PROJECT_STATUS.md`, `docs/README.md`, `docs/design/{HOME,MOVIES_TV,DETAILS,PLAYBACK,SEARCH_MATCHING,NETWORK_FILES,SETTINGS,PARKED_FUTURE,BACKUP_RESTORE,RECENT_PRODUCT_DECISIONS_2026-10-08}.md`, `docs/project/{DECISIONS_AND_ROADMAP,PLANNING_RECONCILIATION_2026-10-08}.md`. Existing authoritative 4.1.7 QA and preview records remain protected; they were not edited. Verified three backup mock-up assets via direct GitHub file fetch.

**Limitations:** This environment does not expose every message of the historically oversized chat or the prior failed-response audit. A full verbatim audit of the entire chat, all original mock-ups and every earlier approval **cannot be certified**. The recovered content is the available conversation context and repository material; it is not proof of code implementation or physical Shield validation. Main and the separate `codex/post-4.1.7-shield-fixes` planning branch had diverged in documentation; changes below intentionally reconcile recoverable decisions into main without touching the active Codex branch.

## Decisions recovered and saved
- **APPROVED design, NOT current implementation:** single custom local Library Page (one slot, five-step wizard, edit/delete/unsaved-exit safeguards), toolbar icon+label outlined controls, local Search alternate-title/fuzzy/article behaviour, per-item Multiple Versions chooser/default ranking/selected-file header quality, detailed Up Next/Binge Watching/Reset Progress/Segment Skipping, Smart Home Rows.
- **APPROVED preserve current:** existing NOVA-derived subtitle appearance/position/timing/languages/downloads/formats; no unsolicited redesign.
- **APPROVED future identity and release history:** 0.1, 0.2 … 0.10, 0.11, 0.12 by verified Supernova release chronology; 1.0 reserved; current expanded Release Notes with older collapsible entries and monochrome icons. Do not renumber builds, change package or signing yet.
- **APPROVED Backup & Restore:** comprehensive backup/full restore of included data, **no secret export**, integrity/compatibility checks before mutation, rollback, embedded human-readable restoration guide, reauthentication. Source audit shows current credential export and unfiltered preferences conflict with desired behaviour. Feature-by-feature backup compatibility audit required for watch progress, new Home/Settings, multi-version, manual matches, source portability, future custom pages and obsolete NOVA settings.
- **NEEDS DECISION:** precise historical release count and future Android versionCode mapping; final About/Settings and Network & Files layouts; specific candidate legacy NOVA cleanup; full device QA; ratings aggregation provider (MDBList/OMDb/RPDB, rights/quotas/coverage unverified); runtime support for any planned subtitle/segment/provider features.
- **PARKED:** Discovery, Profiles, Anime, AI, seek thumbnails, broad Library Health, Trakt reviews, broader diagnostics viewer and unapproved expansions. Performance/Diagnostics audit direction is retained, not an automatic new feature.
- **SUPERSEDED/REJECTED:** Exclusion Options as a sixth custom-page wizard step; divider-only Movies/TV toolbar focus as later future design; subtitle presets/new position sliders for current work; 0.1.2 as the twelfth release; assuming older NOVA backup formats must import. Historical visual identifiers without actual image bytes are not confirmed images.
- **Deferred discussion order:** finish Backup & Restore compatibility, legacy NOVA cleanup and Performance/Diagnostics; **Settings, Network & Files and full installed APK walkthrough last**.

## Files updated and GitHub commit evidence
1. `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md` — recovered detailed approvals and boundaries; commit `b5814470209aa86269926111973de830b97fef67`.
2. `docs/design/BACKUP_RESTORE.md` — backup compatibility and restore-guide requirement; commit `0f4e1ebe8249c5299dc5b376676a133353354d26`.
3. `docs/design/MOVIES_TV.md` — later boxed-toolbar design supersession; commit `5bdc3654a57ebc2eed835f2a46847da7eb8597bb`.
4. `docs/project/PLANNING_RECONCILIATION_2026-10-08.md` — links to recovered authoritative documents; commit `75984bfe2c9fb6061b5757f3b916ca3c5d958b36`.
5. `docs/project/CONVERSATION_RECOVERY_AUDIT_2026-10-08.md` — this recovery record; its own commit is recorded by GitHub history for this path, as a file cannot know its own final commit SHA in advance.

## Verified mock-up references
- `docs/design/approved-mockups/backup-restore/backup-restore-export-approved.png` — exists.
- `docs/design/approved-mockups/backup-restore/backup-restore-selection-progress-approved.png` — exists.
- `docs/design/approved-mockups/backup-restore/backup-restore-confirmation-reconnection-approved.png` — exists.
- `docs/design/references/details-information-approved.png` — **not found on main** at this path; the conversation summary references it, but it cannot be treated as a verified asset. Recover original bytes/provenance before linking it as authoritative.

## Verification and safety
GitHub direct fetch after writes is required to verify each changed path and the exact `main` content. The only writes requested in this recovery are documentation Markdown files; **no app source, signing configuration, version values, active Codex development branch, build or QA artefact is modified**. No attempt to merge or implement approved future features.

## Outstanding gaps
1. Full historic chat and lost audit response unavailable; therefore **not possible to assert perfect completeness**.
2. Details information approved mock-up missing at expected path; other earlier visual bytes may be unavailable.
3. Actual backup/restore security and new-state compatibility require code-level tracing and Shield tests before implementation.
4. Historical Supernova release/APK chronology and version naming mapping not yet established.
5. Ratings provider implementation/permission and PutFlix/Chill source methods unverified.
6. Other planned design areas remain intentionally deferred; no inferred final approvals.
