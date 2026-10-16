# Supernova 0.140 release handover

**Pending final validation and delivery.** This report must be completed with actual signed-artifact and Drive readback evidence; it is not presently a delivery claim.

- Implementation branch: `codex/supernova-0.140-implementation-2026-10-16`.
- [Draft PR 18](https://github.com/heretoecode/Supernova/pull/18), based on the preserved 0.139 implementation branch. No merge into main, tag or public GitHub Release.
- Candidate APK source: `866f21f11f27dea4846ddd3cac57d98b281eecbb`.
- Accepted signed 0.139 baseline: `a4841c1dbaac5e1037fb19f9f11882848b435e0b`, independently verified from the full Drive APK. [Receipt](BASELINE_0139_BINARY_VERIFICATION.json).
- Planning authority: `9f0a81efcf0a6065787186b2763439fa372aced5`; planning documents imported separately without replacing the application tree.
- Target identity: `app.supernova.player`, version **0.140**, Android code **140**. Established upgrade certificate SHA-256: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`.

## Feature status

| Area | Engineering result | Remaining limitation |
|---|---|---|
| FD-007 | Continuous non-focusable two-row panel; all nine page-scoped metrics, logical-title and physical-storage calculations, completed-scan journal and truthful unknown/failed states. Compact text fit and equal reference gaps tested. | Original clean/annotated references missing; physical reference acceptance unavailable. |
| FD-033 | Description, Key Information, Reception, Cast, Crew and Technical in the approved upper/lower composition; continuous active-tab outline, offset dividers, three Reception rules, circular 4×2 portrait grids, bounded text and single-line path. | Device visual acceptance and annotated v3 comparison unavailable. Existing IMDb wordmark, generic trophy and provider text fallbacks differ from sample artwork. |
| OMDb | API/licensing feasibility audit; exact-ID/type checked, bounded HTTPS adapter and attributed four-slot presentation prepared. Production binding hard denied as owner instructed. | No authorised account/entitlement/binding. No live requests or borrowed keys. See [exact activation requirements](OMDB_FEASIBILITY.md). |
| FD-001 | Passive cached country-specific age overlay tied to fresh actual playback-output evidence, 500 ms fades and ten-second full-opacity dwell; missing/unrated values omitted. | No verified descriptor feed, so descriptors omitted. Device playback/scene timing not tested. |
| FD-008 | Existing secure account/browser/sync/WebDAV architecture retained; public client 9874 and device approval route verified against official provider source; public begin/pending checks passed; exact Supernova grant revocation added. | Account-authorised browse/sync/revoke/WebDAV playback not exercised; no account token obtained. |
| Backup & Restore | Existing export categories on the main three-panel page in a vertical list, retained privacy/persistence, contextual text, direct Export and existing Restore review/recovery handlers. | Real SAF destination/save/readback and physical remote validation unavailable. |
| Stability | Existing cold time-formatter correction retained; provider focus, Extras heading recovery, compact navigation paint overflow and Statistics text clipping corrected; diagnostic incident capture allocation reduced with separate queue rejection counters. | Historical event-to-version attribution and device memory/GC measurements require the missing diagnostic ZIP/device. |

## Details and Extras conformance

[Reference archive/provenance](references/README.md), [measured visual register](VISUAL_QA.md), [side-by-side comparison](visual-evidence/FD033-reference-comparison.png), [geometry overlay](visual-evidence/FD033-geometry-overlay.png).

| Approved correction | Result and evidence boundary |
|---|---|
| Restore genres | Provider genres with indexed fallback remain visible in Details. |
| Redundant resolution | Human metadata deduplicates resolution; technical video/audio rows retain available track details. |
| Page background | Existing approved Supernova utility background retained. No illustrative nebula copied as a production asset. |
| Continuous tab border | One compound fill/perimeter, no base stroke under active tab and no double outline; white default, user accent retained. |
| Extras scrolling/headings | Horizontal rails reveal their headings, remember cards and return Up to the active tab with scroll reset. |
| Trailer aspect/size | Shared Home card renderer with 172dp card, 97dp 16:9 artwork and 125dp rail. |
| Home row behaviour | Uses the actual Home CONTINUE card renderer, caption/focus behaviour and bounded zoom. |
| Provider buttons | D-pad Play→provider→More route, measured Play height with minimum 44dp target and retained launch callback. Fixture dispatch does not prove external app launch. |
| Hero synopsis | Balanced vertical spacers improve its position; existing hero actions and metadata remain functional. |

Normal-text fixture frames at 720p/1080p/4K are `(51,120,1178,571)`, `(77,180,1766,857)` and `(154,360,3532,1714)` (left, top, width, height). Four-percent written margins override the narrower raster margins. At 1080p/150% text the frame stays fixed. Software-rendered portraits are 40/64/128 px, or 48 px at enlarged 1080p text, with measured caption bounds. Fixture ratings are illustrative, not live OMDb data; neutral silhouettes represent missing photos.

Final metrics: at a 960×540 logical reference, headings 15sp, facts 8–10sp, synopsis 11–14sp, score up to 16sp, people captions 9sp and portraits up to 34 reference dp. These scale to the available logical viewport and retain Android density/font-scale handling; exact measured rectangles/fonts are saved alongside the renders. Existing indexed/TMDb Reception data remains source-labelled. Hardware fonts, real portraits, provider artwork rights and missing-reference comparisons remain open; no pixel-perfect or physical Shield approval is claimed.

## Diagnostic findings

The original ZIP was not found; all six reported quantities remain leads. [Full source/version/interpretation matrix](DIAGNOSTIC_FINDINGS.md).

| Lead | Outcome |
|---|---|
| Four fatal formatter exceptions | Already corrected in the accepted baseline; cold-initialisation/recreation regressions retained. Historical Shield timestamps cannot be correlated without logs. |
| 652 focus/navigation warnings | Heuristic warnings are not automatically bugs. Independently confirmed provider route, Extras/Backup navigation and current 720p paint/clipping defects corrected. No claim to resolve 652 incidents individually. |
| 17 scan failures | Queue-start failure can be reported as metadata failure. No record-specific raw evidence; preserve safe retry/import behaviour and truthful partial/failed state. |
| 59 unclean markers | Can reflect Android process termination/force-stop; not proof of 59 crashes. Exact split remains unverified. |
| 1,271 dropped events | Bounded queues retained; routine/important rejection counters now distinguish overload. Historical rejection/retention causes unavailable. |
| Memory/GC trends | Repeated incidents reuse an immutable producer-time capture array; underlying records/counters/privacy preserved. No leak, performance percentage or Shield soak claim. |

## Tests and delivery

[Executed test ledger](TEST_RESULTS.md). The preceding complete local suite passed 1,047 tests, and the corrected portrait code passes its 17 affected tests plus zero-error lint (1,775 warnings). The superseded unsigned run passed native rebuild/runtime checks before deliberate cancellation. Full corrected-source gates run again inside [authorised signing workflow 38959928321](https://github.com/heretoecode/Supernova/actions/runs/38959928321); final results are pending. [Local validation receipt](LOCAL_VALIDATION.json). Earlier test/operational failures and corrective checks remain recorded, including the clock pixel test's verified failing-before/passing-after result.

Signed APK filename, size, SHA-256, workflow, independent certificate/native/payload verification and complete Drive download readback: **pending**. No 0.140 file has yet been uploaded to the existing APKs folder. The normal in-app changelog is updated in `assets/foundation/release-history.json`; HTML notes are at `docs/release-notes/Supernova Development.html`, with in-place publication to the existing Drive HTML file pending final status.

No physical Shield testing, emulator/instrumentation, actual install/signed upgrade, hardware playback/resume/HDR/passthrough, real account/provider launch, multi-drive scan or SAF round trip is claimed. OMDb enablement and unavailable references/logs remain external dependencies. Unified Playback Settings, broader HUD redesign and the separate 14-finding HUD audit remain excluded for 0.141.
