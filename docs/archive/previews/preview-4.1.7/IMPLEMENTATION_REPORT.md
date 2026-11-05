# Supernova Preview 4.1.7 — final implementation and validation report

**FINAL development APK accepted for delivery on 5 October 2026.** The three development conformance reviews and automated/signed delivery gates are complete, with the physical and external-service limitations below explicitly retained. This is not physical Shield acceptance or permission to merge into main.

## Frozen implementation and artifact

| Item | Verified value |
| --- | --- |
| Repository / branch | heretoecode/Supernova · codex/preview-4.1.7 |
| Final application/test source | 7efb4195639205a6526281491af40c0dab1776b7 |
| Source tree | e92d340c28fcd85fd2b90d4827dadb2e60ad66c2 |
| APK | Supernova-4.1.7-FINAL-7efb4195.apk |
| APK bytes | 82,738,089 |
| APK SHA-256 | 4070ee3d259b3cf57bb10a26fbc789363cf812e8b81fe7f3101522634705681a |
| Certificate SHA-256 | 89ac087ed6f989c90482d4a999f80511fe6ceee26ef1b9c37a142a9f00d39a5a |
| Application ID | org.courville.nova.markpreview |
| Version | 6.4.63-mark.4.1.7-preview / 6040083 |
| Build | Optimised universal release, full validation mode |
| APK artifact | 11367082061 |
| Full validation evidence | 11366364269 |

The downloaded artifact archive digest matched GitHub's recorded digest, and the extracted APK hash exactly matches CI's `apk-sha256.txt`. CI used `apksigner` to verify the APK and the required certificate. Renaming the delivered file did not modify its bytes. The build record identifies the frozen source above and full/release mode.

This is built after the interim ff955d90 snapshot and all subsequent implementation corrections. Documentation closeout commits record the result without changing application/build/test inputs; the APK's authoritative build source remains the SHA above.

## Validation

| Gate | Evidence / outcome |
| --- | --- |
| Current-source compilation, identity and complete Video suite | [Source CI 37359092502](https://github.com/heretoecode/Supernova/actions/runs/37359092502) — passed |
| Selected stability regressions and WebDAV | Same source run and full run — passed; test sets overlap |
| Full Video suite in APK workflow | [Full CI 37359092413](https://github.com/heretoecode/Supernova/actions/runs/37359092413) — passed |
| Debug/release lint and signed packaging | Full CI — passed with lint enforced |
| Classic/Preview emulator startup | Full CI — passed |
| Native playback and exact Info/Back return before release packaging | Full CI — passed; no interim-known-defect exception |
| Signed development-to-release upgrade/restart | Full CI — passed |
| Release APK signature, pinned certificate and native-library inventory | Full CI — passed |
| Downloaded APK integrity | Exact archive and APK SHA-256 comparison — passed |
| Local structural/tooling checks | 431 XML files parsed; nine Python tooling tests passed |

Failed intermediate runs remain in IMPLEMENTATION_LOG.md. In particular, full lint exposed eleven errors, and new visual tests exposed fixture setup problems followed by a real shared-menu paint-containment defect. The gate and assertions were retained; corrections were validated on later source.

## Conformance reviews

1. **Source/completeness:** all 115 original authority paragraphs are unchanged and mapped to implementation/evidence or an explicit limitation. Thirty-nine stale register entries were reconciled in this continuation; remaining stale test statements were updated after current-source success.
2. **Rendered/behavioural:** existing approved evidence was retained. Incorrect Info, Network and episode captures were corrected. Focused Home delete/keyboard, populated Search return, updated Details portraits/panels and initial/scrolled genre images were inspected. The genre fix now keeps rows inside the middle viewport, with a fixed-heading pixel regression. Real artwork and physical appearance remain classified below.
3. **Regression/scope/delivery:** current source and full/native CI passed. Package/signing continuity, strict playback return, release upgrade, APK bytes and deferred-scope boundaries were reviewed. Release notes and this report complete the delivery record.

## Corrections completed in this continuation

- Failure-time diagnostic snapshots and event-time post-failure routing despite delayed writers.
- Shared monochrome provider rendering without losing internal logo detail; correct local-media rebind state.
- Put.io sync parent/child correlation, terminal outcomes and cancellation retention.
- Friendly protocol labels for actual internal connection schemes.
- Put.io lifecycle-aware Back dispatcher, named typeface constants, compatible custom portraits and correctly placed narrow Settings-adapter annotation.
- Shared menu scroll clipping after visual review found rows painting behind fixed controls.
- Full-mode complete Video-suite execution and explicit retained lint evidence; no blanket lint exclusion or relaxed playback/signature gate.

## Explicit limitations — not reported as passed

- **AWAITING PHYSICAL SHIELD QA:** real remote repeat/focus/scrolling and seeking; real artwork/crops/glow/readability; new-file scan equivalence across manual/startup/resume; artwork retention after scan/restart; actual installed 4.1.6 data/settings/library upgrade; launcher viewing-distance legibility; real network permissions and multi-day diagnostics/export/clean-exit behaviour. The emulator upgrade above is development-to-release, not the user's installed 4.1.6 device.
- **BLOCKED — production put.io OAuth/live account:** registered production configuration and real-account association/outage paths remain the acknowledged external dependency. Original-quality playback remains WebDAV through AVOS/FFmpeg.
- **Live matching/provider scenarios:** platform-specific deep links, account/region availability and real multi-version/NFO failure cases are not proven by synthetic tests. Provider-level tests preserve physical IDs/paths/history/associations. Film correction saves the explicitly selected physical item; series correction carries each file into its batch and migrates Home memberships.
- **Coexistence investigation:** isolated package/providers and signing were verified. The exact official APK/installer error and Shield shared-user state are still needed to establish the reported upstream installation cause. No identity migration was attempted.

The original checkout and its thirteen pre-existing missing symlinks remain preserved. Remote main was confirmed unchanged at 77b2617ad1e48b7a7a85ba28463de0961af5ba4d. No reset/clean, force push, replacement implementation, main merge, Phase 1 or next-release work occurred.
