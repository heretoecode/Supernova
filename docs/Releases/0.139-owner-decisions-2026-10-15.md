# Supernova 0.139 — owner decisions resolving Backup / Details blockers

**Approved by owner: 15 October 2026.** These are explicit implementation authorisations for the **existing 0.139** work; not new 0.140 features. Preserve approved visual identity and existing branch work. The previous Codex report identified 996 tests, 995 passed, 1 failed (Backup 640×360), and GitHub signing branch policy rejected with HTTP 403.

## Decision B-640: Backup screen at 640×360
**APPROVED:** Permit a **responsive layout at 640×360** while retaining the **approved fixed sidebar width at normal TV resolutions**. Only adapt the compact/insufficient-width configuration as necessary to keep categories, navigation and controls on-screen, readable and operable by D-pad. Do not globally change the normal-resolution sidebar width or replace the owner-approved Backup/Restore design. Choose the least disruptive responsive treatment, validate at 640×360 and 960×540, 1280×720, 1920×1080 and 4K where available. Update the failing Backup test to assert the approved behaviour, not simply disable it.

## Decision D03: Selected tab versus current focus
**APPROVED:** Maintain a **persistent visual selected-state cap/tab** for the currently selected Details section while using **distinct additional focus emphasis** for the item currently receiving D-pad focus. Selected and focused states must remain distinguishable even when focus moves; no accidental double-selected indication. Follow existing approved design styling and test navigation/state persistence.

## Decision D04: Long Details content
**APPROVED:** Allow **vertical reading overflow** to access complete information when content exceeds the available reading area. Keep the approved design visually unchanged when content fits. Implement accessible, predictable D-pad reading/scrolling behaviour for overflow; preserve focus, tab selection and Back behaviour. Do not truncate essential content or require a separate full-information route merely to see long content.

**Scope reconciliation:** D04 applies to genuine long-content overflow in the original Details requirements for 0.139. FD-033's future **0.140** redesign still has its own approved default layout/interaction constraints (including no independent scrolling in the normal six-section mock-up). Codex must preserve both release boundaries and document any genuine conflict instead of silently importing 0.140's redesign into 0.139.

## Existing external blockers
- **Signing:** existing-certificate workflow is prepared but GitHub branch-policy update failed with **403 Resource not accessible by integration**. An authorised administrator must allow the required branch/workflow or use an already-authorised signing path; do not bypass protections or change signing identity.
- **Shield:** no connected Shield/ADB endpoint for physical device acceptance and signed-upgrade tests. Record unperformed checks accurately; do not claim they passed.
- **Delivery:** no signed 0.139 APK and no Google Drive APK delivery verified at time of owner approval.

## Execution guard
Continue existing 0.139 branch/PR #17 from latest checkpoint. Inspect current head and `CONTINUATION.md` before changing anything. Finish responsive Backup fix and D03/D04, run full tests, update 0.139 release-note and HTML sources, build/sign using authorised workflow, and upload verified signed `Supernova_0.139.apk` to the existing Supernova APK folder in Google Drive. If signing or physical acceptance remains externally blocked, preserve work and provide exact administrative steps. Do not merge into main or begin 0.140.
