# Parked & Future Product/Design Work

Status: **MIXED — APPROVED FUTURE DESIGNS AND PARKED IDEAS; NONE ADDED TO CURRENT FIXES-ONLY SCOPE**

This file preserves future work so it is resumable rather than reduced to feature names. It must be read together with `docs/project/DECISIONS_AND_ROADMAP.md`.

## Foundation Release identity — APPROVED direction; production execution pending
Later fresh Supernova identity:
- fresh-install model;
- new application/package ID;
- new signing key;
- complete Supernova branding;
- clean app data;
- no requirement to upgrade from the old Preview identity;
- ideally coexist with old/upstream Nova on Shield;
- validate from first launch.
Do not assume Mark can generate/manage a desktop keystore; development workflow must accommodate iOS-only project ownership.
Identity direction and branding are approved; verify actual package ID, signing-key custody, release version/build mapping and platform assets during authorised Foundation Release implementation. Do not reopen the approved visual design.

## About/versioning/branding — APPROVED future design, implementation pending
All five About subsection designs are approved in `docs/design/ABOUT_SETTINGS.md`; preserve upstream and legally required attribution. The canonical branding visual reference and Space Black Blend are committed. Production asset export, installed version display and final binary/licence checks are execution tasks, not open visual decisions.

## Profiles — PARKED
Profiles are a future product feature, not part of the fixes-only release. Before activation define library/history/watch-state/provider/settings scope per profile, switching UX, migration/default profile and interaction with external services.

## Smart Collections — NEEDS DECISION
Concept is retained but not approved for implementation. Before activation define rules versus manual collections, editable criteria, membership refresh, interaction with existing Collections and Home rows, empty/error states and performance on large libraries.

## Discovery — PARKED; one custom local Library Page — APPROVED future design
The single custom local Library Page has an approved five-step wizard and edit/delete safeguards in `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md`; it remains outside the current fixes-only release. The separate Discovery Page remains parked and must not silently turn Home Featured or More Like This into an advertising/recommendation feed.

## Library Health / broader library audit — PARKED
Broader Unscraped Media / Library Health work is parked beyond the first-class Unmatched workflow already implemented. Future design should distinguish actionable problems (unmatched metadata, missing artwork, inaccessible file/source, duplicate/version issues, stale provider data) and avoid destructive “repair” without explicit review.

## Broader audits — PARKED
Movies/TV controls, List View columns, sorting, Settings, metadata, providers, artwork, Trakt, OpenSubtitles, TMDB and inherited library architecture may receive broader audits later. Current fixes do not authorise redesign outside identified defects.

## Network/provider futures — PARKED
Do not add NFS unless genuine implementation support exists. Put.io native API/OAuth work is separate from generic WebDAV playback; production OAuth configuration remains an external dependency. WebDAV replacement is not approved. Do not infer speculative transfer/sync capabilities.

## Person/cast discovery — PARKED
Person pages/cast discovery are future work. Current Cast/Crew rows do not authorise person-profile navigation.

## Design preservation rule
When any parked item is activated, create/update its design authority with final behaviour, geometry and mock-ups before implementation. A feature name is not an implementation specification.

## Current strict non-goals
The current fixes-only release must not introduce identity migration, Smart Collections, Profiles, Discovery/custom pages, broad audits, put.io transfer/sync expansion, WebDAV replacement, NFS, speculative capabilities, branding redesign or dependency-pin movement.

## 9 October 2026 — confirmation and cross-references (reflected above)
- **About** is no longer an undecided design: all five About subsections and flexible three-panel layout are approved in `docs/design/ABOUT_SETTINGS.md`. **Implementation and release-specific licence verification remain outstanding**.
- **One custom local Library Page** has an approved future five-step wizard design in `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md`; it is **not** approved for the current fixes-only implementation. **Discovery Page remains parked.** Earlier “all custom pages parked” wording is superseded for design status only.
- **Branding visual decisions** are closed; see the later entries in `docs/project/CURRENT_CHAT_DECISIONS_2026-10-08.md` and approved asset references. Platform-ready production assets and app integration are separate execution tasks, not a reason to reopen visual design.
- **Backup archive format 1.0 onward** is the sole supported compatibility baseline; no legacy NOVA or pre-1.0 Supernova import. See `docs/design/BACKUP_RESTORE.md`.
- **Diagnostics** expansion is confirmed as future work using the existing logging toggle/export; keep all useful fault data while redacting credentials. No in-app viewer approved.
- **Wider Settings, Network & Files and installed-APK walkthroughs are temporarily set aside at user's request.**
