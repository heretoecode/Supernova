# FD-005 — Network & Files visual redesign

> Execution update (2026-10-23): the owner subsequently authorised this work in the current release-preparation instruction. Candidate 0.142 implementation is on `codex/supernova-0.142-implementation-2026-10-23`; see [release evidence](../../0.142/RELEASE_REPORT.md). The planning-only restrictions below are historical. Physical acceptance and signed delivery remain open; no main merge is authorised.

**Status:** SCHEDULED FOR NEXT SUPERNOVA VERSION (owner decision 2026-10-23).
**Target:** Next version after delivered 0.141 (expected 0.142, subject to release/version confirmation).
**Scope:** Network & Files redesign, preserving all existing functionality.
**Coding authorisation:** **NOT YET GRANTED**. Scheduling for the next version is a roadmap decision, not permission to begin implementation, merge, or issue an APK.

## Implementation authority and approved direction
- [Detailed Network & Files blueprint](../../design/NETWORK_FILES_BLUEPRINT_DRAFT_2026-10-23.md) on branch `docs/network-files-blueprint-2026-10-23`.
- [Three original uploaded visual references](../../design/assets/network-files/README.md) (Overview, Add WebDAV, Connect Put.io).
- [FD-003 completed functionality audit](../../audits/FD-003-network-and-files-functionality-audit-2026-10-23.md) on branch `docs/network-files-functionality-audit-2026-10-23`. The audit is not necessarily present on this branch: retrieve from that explicit ref.
- [Global visual system](../../design/GLOBAL_VISUAL_SYSTEM.md).

## Locked owner-facing requirements
1. Permanent left rail with exactly six destinations: Overview, Local Storage, Network Shares, Cloud Services, All Files, Library Health.
2. No rigid three-panel layout; one flexible scrollable workspace.
3. Overview four stacked sections: Storage, Network Shares, Cloud Services, Library Status; no Quick Actions bar.
4. Monochrome icons with no background tiles; Space Black Blend and existing Supernova typography/focus language.
5. WebDAV and Put.io flows follow approved visual references **without inventing unsupported features**.
6. Preserve network scanning, library scanning, source/bookmark distinctions, USB, protocols, credential safety, Put.io authorisation/associations, file browser and Library Health.
7. D-pad focus, navigation restoration, keyboard punctuation, asynchronous updates and unavailable-source safety must be tested on Shield.

## Pre-implementation gates
- Resolve source-to-blueprint typography/layout token mapping.
- Build full FD-003 functionality/control/state/QA parity ledger, not just count-level claims.
- Inspect actual uploaded PNGs and map all screen states, buttons and focus routes.
- Resolve source-confirmed audit defects or document individual disposition.
- Obtain explicit owner instruction to begin the next release; do not assume this scheduling decision authorises coding.

## Historical context
Previously PARKED / HISTORICAL DISCUSSION ONLY with unassigned release and no approved replacement. Owner reopened, approved three mock-up directions and requested the blueprint; the visual reference PNGs are now committed on the documentation branch. Preserve prior discussion provenance in linked backlog/roadmap records; this note supersedes the earlier parked status for FD-005 only.

**Release discipline:** No edits to delivered 0.141 application source, no merge to main, no APK generation and no next-release implementation under this documentation update.
