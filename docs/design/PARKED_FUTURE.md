# Parked & Future Product/Design Work

Status: **PARKED / NEEDS DECISION — NOT CURRENT IMPLEMENTATION SCOPE**

This file preserves future work so it is resumable rather than reduced to feature names. It must be read together with `docs/project/DECISIONS_AND_ROADMAP.md`.

## Future identity transition — APPROVED direction, mechanism NEEDS DECISION
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
Exact package/version/signing mechanism remains a future decision.

## About/versioning/branding — PARKED
Clean fresh-baseline versioning later. About should present Supernova cleanly while retaining required legal/upstream attribution and useful build/version identity. Remove obsolete AOS/Nova-facing branding where legally/technically appropriate; do not erase attribution.

## Profiles — PARKED
Profiles are a future product feature, not part of the fixes-only release. Before activation define library/history/watch-state/provider/settings scope per profile, switching UX, migration/default profile and interaction with external services.

## Smart Collections — NEEDS DECISION
Concept is retained but not approved for implementation. Before activation define rules versus manual collections, editable criteria, membership refresh, interaction with existing Collections and Home rows, empty/error states and performance on large libraries.

## Discovery / custom pages — PARKED
Future custom-page/Discovery concepts remain outside current scope. They must not silently turn Home Featured or More Like This into an advertising/recommendation feed. Define data sources, local-vs-streaming boundaries, provider-country behaviour, page customisation and navigation before implementation.

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
