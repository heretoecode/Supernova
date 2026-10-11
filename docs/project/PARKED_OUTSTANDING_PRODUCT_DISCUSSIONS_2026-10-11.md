# PARKED — Outstanding Supernova product discussions and future planning
**Recorded:** 2026-10-11  
**Status:** PARKED / DISCUSSION REGISTER — not an implementation brief  
**Repository:** `heretoecode/Supernova`

> This is a durable restart point for the user's future product-planning conversations. It deliberately excludes the features already installed in Supernova 0.135 QA, work assigned to Codex's current refinement release, and separately parked redesigns. Nothing in this document authorises code changes, scope expansion or a new release.

## 1. Context and scope boundaries

The user requested a retrospective review of Supernova discussions, GitHub planning and implementation status, **disregarding features Codex is currently implementing**. The previous review found no additional major forgotten approved interface redesign. Most remaining matters are future product features, incomplete integration audits and longer-term engineering decisions.

At the time of this register:
- User tests Supernova on **NVIDIA Shield TV**, with **0.135 QA** as the reported installed testing version.
- Codex is independently working on `codex/supernova-next-refinement-2026-10-10`, PR #10. The post-0.135 refinement brief/register governs its scope; do not infer completion from assignment.
- **Movie/TV Details page** and **Playback HUD redesign** are separately parked; do not treat as part of this discussion list or approved current-release work.
- **NOVA upstream 6.4.64→6.5.5 selective-upgrade research** is separately parked at [its comprehensive handover](PARKED_NOVA_UPSTREAM_UPGRADE_HANDOVER_2026-10-11.md) on the docs-only branch; no implementation authorised.
- **Playback resume / Up Next** should not be reopened or silently bundled into a future feature without the user's approval.
- Discussions may produce mockups, written requirements and GitHub documentation, but **must not automatically trigger Codex implementation**.

## 2. Four primary outstanding product discussions

### P1 — Library Health and Recovery (partially planned; recommended first)
Already discussed: integrate Library Health into **Network & Files**. The larger recovery behaviour remains undecided.

Decisions to make:
- Distinguish temporarily disconnected SMB/NAS shares, unplugged drives, moved/renamed media, genuinely deleted files and transient scan failures.
- Whether to retain library entries, watched status, posters, collections and resume positions when storage goes offline.
- Detecting duplicate media and conflicting scanner results; explain identity/matching and false-positive policy.
- Whether recovery is entirely manual, guided or can perform narrowly scoped automatic repairs; require clear preview, confirmation, undo/rollback and safe defaults.
- What the Library Health screen displays (missing/offline items, broken paths, duplicate candidates, scan errors, recovery actions and progress).
- Rescan/relink workflows and preservation of metadata, backups and watch history.
- Tests for unplugged storage, intermittent NAS, failed scans and reconnect; avoid destructive removal.

**Not yet approved:** a full repair UI, automatic deletion/relinking policy or implementation scope.

### P2 — Smart Collections (needs decision; recommended second)
Possible generated sets: unwatched films, recently added series, specific genres, recently watched, continuing series and custom criteria.

Decisions to make:
- Fixed built-in collection types versus user-defined rules; whether AND/OR logic and nested groups are worthwhile.
- Rule fields: media type, genre, year, rating, date added, watch status, resolution, language, source/location, etc.; verify data availability before committing.
- How generated collections coexist with manual/custom library pages and current sorting/hiding/reordering.
- Whether collections are dynamically evaluated, cached, or refreshed after scanning/watched changes.
- Empty states, rename/hide/pin controls, performance with large NAS libraries, and TV remote focus behaviour.
- Profiles interaction if profiles are added later.

**Not yet approved:** rule engine complexity, UI design or release assignment.

### P3 — User Profiles (parked; larger cross-cutting feature)
Concept: multiple household users with potentially independent Continue Watching, watched state, preferences, collections and recommendations.

Decisions to make:
- Shared versus per-profile library, metadata, sources, downloads, collections, playback history, resume position and settings.
- Profile switching on Android TV, default/guest profile, child-friendly controls if wanted, and optional PIN.
- Migration of existing single-user history into a default profile; backup/restore compatibility and deletion semantics.
- How Trakt and external integrations bind to individual profiles, and how storage/scanning remain shared.
- Performance, offline support, data privacy and scope for a staged implementation.

**Not yet approved:** feature design, migration strategy or release.

### P4 — Dedicated Discovery Page (parked; distinct future product area)
Concept: finding films and shows **outside the local library**, potentially with recommendations and streaming/provider availability. Not a replacement for existing library Search.

Decisions to make:
- Discovery sources and legal/API constraints (TMDB and optional availability providers), geographic coverage, metadata accuracy and refresh.
- Local-library versus external catalogue distinction; actions for titles not owned.
- Recommendation strategy (editorial, genre-based, viewing history, optional AI) and privacy/offline handling.
- Whether to integrate into navigation, create a separate tab, or keep it out of scope.
- TV-remote UX, artwork, loading/empty/error states, and avoidance of advertising or misleading availability claims.

**Not yet approved:** dedicated screen, data-provider integration or implementation.

## 3. Secondary integrations and feature audits

These are **broader future audits**, not a request to redo fixes already in Codex's refinement brief:

| Area | Outstanding discussion |
|---|---|
| **put.io** | Complete real OAuth, secure credential/token storage, refresh/revocation and end-to-end cloud browsing/playback; define failure and privacy handling |
| **Trakt** | Account integration, watched/resume sync policy, conflicts, offline reconciliation, multiple-profile implications and reliability |
| **OpenSubtitles** | Authentication, matching accuracy, subtitle language defaults, selection, download errors, privacy and rate limits |
| **TMDB / metadata** | Field and artwork coverage, provider fallback, matching accuracy, scrape refresh, locale and source precedence |
| **Scanning / media recovery** | Overlaps P1: disconnected drives, stale entries, duplicate/moved files, failed rescan and data retention |
| **List View** | Data columns, sorting, density, remote focus and whether further customisation is useful beyond current functionality |

Avoid assuming any integration is complete solely because it appears in requirements. Verify against the accepted APK, test evidence and current Codex work when un-parking.

## 4. Longer-term technical and release decisions

- **Kotlin / Jetpack Compose:** consider selective modernisation versus keeping Java/XML; assess maintainability, animation consistency, performance, interop, Android TV D-pad focus and migration cost. No wholesale rewrite authorised.
- **AI-assisted features:** possible smart search, recommendations and eventual Ask Supernova; first decide actual user value, offline/privacy requirements, model/network costs, reliability and opt-in behaviour.
- **Branding / release identity:** confirm final icon, splash, TV banner, package/application ID, signing continuity and version numbering before public distribution. Avoid changing these during current refinement.
- **Release engineering:** repeatable CI, native build provenance, tests, signed APK delivery, changelogs, rollback and release acceptance gates.
- **Backup & Restore:** Format 1.0 specification/design was previously approved, but current implementation and real restore testing are not assumed complete. Preserve the requirement that backups exclude credentials/authentication tokens, explain restore clearly and validate backups before applying. Audit independently of P1 recovery design.

## 5. GitHub documentation reconciliation — known issue, not a current release task

Direct inspection of `PROJECT_STATUS.md` on the active refinement branch on 2026-10-11 still described **Preview 4.1.7 Final** as the accepted baseline and fixes-only phase, despite later 0.135 QA and current refinement work. `docs/project/DECISIONS_AND_ROADMAP.md` remains an important historical decision record; some future classifications (including Custom Library Pages) may lag later work.

**Proposed later documentation-only task:** reconcile current status, approved decisions, implemented functionality and parked work while preserving historical documents, audit trail and existing active Codex branch. Verify actual release/acceptance state first. **Do not edit `PROJECT_STATUS.md`, roadmap or current Codex branch automatically** as a consequence of this register.

## 6. Suggested discussion sequence

1. **Library Health and Recovery** — day-to-day data reliability and safe handling of disconnected media.
2. **Smart Collections** — next natural extension of custom library organisation.
3. **User Profiles** — requires foundational data/backup/account decisions.
4. **Discovery** — distinct external catalogue product area.

Then address put.io/Trakt/OpenSubtitles/TMDB and List View as appropriate, followed by technology and release decisions. This is a *recommendation for conversation order*, not an approved development schedule.

## 7. Resume instructions and approval gate

When the user wants to resume, open this register and the current GitHub status/roadmap and Codex refinement requirements. Verify which current-release items actually shipped; **subtract implemented and assigned work** before proposing anything new. Start with the user's chosen topic, discuss options and mockups, then explicitly record decisions as Approved / Parked / Needs discussion. Do not promote an idea to approved scope or send it to Codex without explicit permission.

**Final state:** all items above preserved for future discussion. This register itself is documentation only; no code, builds, signing, release identity, PR or active branch changed.
