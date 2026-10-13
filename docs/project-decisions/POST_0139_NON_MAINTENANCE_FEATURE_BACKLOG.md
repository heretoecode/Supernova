# Supernova — post-0.139 feature/design backlog (excluding maintenance)
Updated 2026-10-13. Planning only; no code/build/merge authorisation.

## Strict exclusions from this backlog
1. **All 43 owner-approved 0.139 workstreams** (41 original + 10-title hero rotation + complete Backup & Restore redesign). Authoritative: [0.139 Codex handover](2026-10-13-supernova-0139-codex-implementation-handover.md). Do not reopen those here. Details tab implementation/forensic dependency remains in 0.139.
2. **Stage D comprehensive maintenance**: upstream NOVA/dependencies/Jetty/OpenSSL/FFmpeg/Android target, shared-component centralisation, keyboard/background refactors, cleanup, security/performance/architecture, maintenance test programme. Do not reclassify these as feature backlog.
3. Already completed branding/launcher banner/icon/loading assets/signing are not new work.
4. This document is not a substitute for current Shield QA nor a claim that the entire historical conversation or all mockup images were independently retrieved.

## Prioritised future feature and design work
| Priority | Track | State | What remains to decide or investigate | Boundary |
|---|---|---|---|---|
| 1 | **Playback-start age classification + content advisories overlay** | NEW IDEA; investigation brief prepared, no design approval | Source of authoritative age certificate and **title-specific content advisories**; film/episode/region matching; display timing/duration, missing data, source attribution, D-pad/passive overlay, accessibility and privacy; screenshots and prototype later | **Not** broad 0.139 playback HUD redesign, nor current 0.139 Details work |
| 2 | **Library header and statistics** | PARKED design; partial principles | Shared Movies/TV/custom Library presentation, truthful counts/storage units and computation, visual hierarchy/layout, device screenshots and mockups; do not conflate with 0.139 Library D-pad fixes | Outside 0.139; avoid maintenance refactor |
| 3 | **Shield Home / Android TV launcher channels** | PARKED pending product/platform investigation | Which indexed rows/titles publish, eligibility/consent, on/off controls, channel identity, lifecycle/Android TV support, Shield behaviour, privacy and provider metadata | Not the already-implemented launcher app banner; no Show on Shield Home in 0.139 |
| 4 | **Network & Files screen-specific visual redesign** | PARKED | Current screenshots/behaviour and D-pad graph, layouts and responsive sizing, contextual menus, apply approved Menu Foundation without replacing permanent functionality | No broad maintenance-wide shared UI refactor |
| 5 | **Other Settings and context-menu redesign** | PARKED | Inventory visible screens, prioritise per-area cosmetic/function review, design and mockups, remote focus, use Menu Foundation | 0.139 Backup & Restore UI already included; do not duplicate |
| 6 | **Comprehensive playback HUD redesign** | PARKED | Actual playback screenshots/video, focus/seek/timeouts, controls and safe-area treatment, separate design approval | Age overlay can be designed independently but must coexist; 0.139 playback fixes remain in release |
| 7 | **Put.io integration improvements** | NEEDS PRODUCT DISCUSSION | Current integration contract and desired account/link/file workflow, security/auth boundaries, media matching, source handling; only then revisit whether *non-secret* Put.io associations belong in backups | New 0.139 backups explicitly exclude Put.io associations |
| 8 | **Additional custom Home row icons** | LOW-PRIORITY ENHANCEMENT | Identify missing semantic categories from usage, avoid unnecessary icons, keep canonical monochrome unboxed design | Existing approved 0.139 icon catalogue remains |
| 9 | **Hero More Info versus Resume behaviour** | UNAPPROVED historical suggestion | Check whether already covered by 0.139 hero/playback actions; do not separately schedule without owner request | Avoid scope duplication |

## Non-feature follow-up (not a feature backlog item)
- Historical 0.138 Shield walkthrough and one diagnostics ZIP near end were not conclusively signed off in documentation. Keep as QA context/validation, **not a new feature or an assertion of defects**. The user requested the revised to-do list omit the former answer's Sections 3 and 4; keep QA outside the displayed feature to-do.
- 0.139 Details forensic audit was separately running; its conclusions feed the existing release, not this backlog.
- Older chronological documents contain superseded decisions. This backlog follows latest owner scope and dates.

## Backlog workflow
For each track: (a) source/current behaviour investigation, (b) privacy and technical feasibility, (c) owner design discussion, (d) TV D-pad/mockups, (e) separate explicit implementation approval. No automatic start of future work.
