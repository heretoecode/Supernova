# Supernova — next release Codex handover (prepared 2026-10-23)

**Status:** READY FOR CODEX DISCOVERY AND IMPLEMENTATION PLANNING. Owner instruction to hand off has been given; Codex should treat this as the requested next-release work package, but must respect unresolved visual approvals and release safety gates.

**Baseline:** Delivered 0.141, app commit `4f0937f1fe8826f50d2c0e844b0837e8a2360a46`. **Expected target:** 0.142, verify versioning before modifying. Existing app branch `codex/supernova-0.141-implementation-2026-10-22` and draft PR #19 are not authority to overwrite or merge; inspect current GitHub state first. Main must remain untouched unless owner separately authorises merge.

## Required discovery before any implementation
1. Inspect repository branches, open PRs, HEADs, release history, source and build/signing workflows; determine authoritative delivered 0.141 source and exact next-release base. Never reset/rebase/discard existing work.
2. Read this handover, [FD-005](../Future%20Development/02%20Parked%20Ideas/FD-005-network-files-visual-redesign.md), [FD-006](../Future%20Development/02%20Parked%20Ideas/FD-006-edit-library-opacity-readability.md), [Network & Files blueprint](../design/NETWORK_FILES_BLUEPRINT_DRAFT_2026-10-23.md), [asset register](../design/assets/network-files/README.md), [global visual system](../design/GLOBAL_VISUAL_SYSTEM.md), [Playback HUD specification](../design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md).
3. Read full functional audit `docs/audits/FD-003-network-and-files-functionality-audit-2026-10-23.md` **from branch `docs/network-files-functionality-audit-2026-10-23`**, not assumed to be present on this branch. Inspect source for baseline functions, tests and focus graph.
4. Create an isolated next-release implementation branch from verified base; retain original branches and docs. Bring handover, assets and audit into implementation context without overwriting unrelated changes. Keep a running evidence log.

## A — Network & Files redesign (approved next-release scope)
- Replace rigid three-panel layout with persistent six-item left navigation and one scrollable workspace.
- Default Overview with Storage, Network Shares, Cloud Services, Library Status cards; no Quick Actions.
- Monochrome icons without icon tiles; approved Space Black, typeface, focus treatment.
- Use three *actual* reference PNGs at `docs/design/assets/network-files/`; README maps R1/R2/R3 and verified Git blob SHAs. The images are reference only: do not implement fictional providers or sample data.
- Implement functional parity across Local Storage, Network Shares, Add Connection/WebDAV, Cloud Services/Put.io device authorisation, All Files, Library Health, scanning, bookmarks, library indexing and associated confirmations/errors.
- Before coding this area, complete source-to-layout token map and **one-row-per-item** FD-003 parity ledger (92 controls, 28 state families, 15 conditional findings, 67 Shield QA cases). Resolve any discrepancies against source and audit. Preserve credentials and mappings safely.
- Do not claim screenshot fidelity without real 1080p screenshot comparison.

## B — Edit Library opacity/readability (approved next-release scope)
- Preserve existing five-step editor, controls and Live Preview; fix excessive translucency so busy Home art cannot interfere with text.
- Provisional targets: editor backdrop 95–98% opaque dark, panels 90–95%; tune based on measured readability and Supernova style; prefer simple scrims over performance-costly blur.
- Test Page Details, Content Type, Filters, Display Options and Review; test modal/keyboard/focus states; before/after Shield screenshots, preserve behaviour.

## C — Playback HUD (candidate, **NOT approved final design**)
- Existing written authority for main HUD: four centred pill controls in exact order Play/Pause, Subtitles, Audio, More; focused white pill; seek bar above buttons; no standalone Info or Settings HUD buttons. Preserve underlying functions.
- Owner has *expressed preference for tabbed Playback Settings* with tabs Subtitles, Audio, Display, Playback, settings below tabs.
- Recent generated illustrations were repeatedly rejected as not matching original approved mock-up; **do not treat them as approved design assets**. Original image is not verified in GitHub. No tab layout dimensions, complete option inventory, focus/back semantics or exact placement approved.
- Therefore: Codex may **audit, document, prototype and propose** the tabbed Settings HUD, but **must not ship a speculative HUD redesign** until owner signs off a faithful mock-up and source-grounded functional inventory. Do not block approved A/B work while awaiting this approval.

## Mandatory release gates
1. Complete per-screen component and focus mapping, parity ledger, traceable tests, security/credential handling.
2. Build and run existing automated tests, add regression coverage for navigation and connection workflows, review performance/TV overscan.
3. Real-device Nvidia Shield QA checklist: D-pad focus, BACK/scroll restoration, USB reconnect, SMB/WebDAV including usernames with @, Put.io device-code and folder associations, library health, editor contrast, long lists and loading/error states.
4. Capture 1920×1080 screenshots for every major screen/state and compare with approved reference assets. Record mismatches and fix before declaring done.
5. Keep change log, release notes HTML and other release artefacts synchronised with actual implementation; do not report untested items as fixed.
6. Produce signed APK and upload to existing Google Drive APK folder **only if signing and Drive access are available and build is fully validated**; report blocker rather than fabricate link/checksum. Do not merge to main without explicit owner approval.
7. Deliver concise evidence report: exact branch, commit SHA, implemented vs deferred items, test results, screenshot locations, APK filename/version code/checksum and Drive link if verified.

## Non-goals
No unrelated feature additions; no imaginary cloud providers; no changes to branding/signing identity; no destructive library migration; no silent PR merge; no replacement of existing 0.141 deliverables. Distinguish confirmed defects from hypothetical ones. Escalate to owner only for personal visual approval, release authorisation or unavoidable credentials.

**Interpretation of current owner instruction:** Prepare everything for Codex and supply a handover prompt. The owner has not yet approved the tabbed HUD mock-up; treat it as a design gate, not permission to implement guesses.
