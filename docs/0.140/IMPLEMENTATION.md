# Supernova 0.140 implementation and release evidence

Status: signed development/QA build **delivered and independently verified**, exact APK source `866f21f11f27dea4846ddd3cac57d98b281eecbb`. [Release report](RELEASE_REPORT.md). No main merge or physical Shield access. External acceptance limitations remain explicit.

## Baseline and authority

- Exact signed corrective 0.139 source: `a4841c1dbaac5e1037fb19f9f11882848b435e0b`.
- PR 17 head at inspection: `d173e8dd9ea41834118a32e240518d91e15e11d1`. Its changes after the APK source are documentation and runtime evidence only (`git diff --name-status` verified).
- Signing run 38908109847 completed successfully at the exact APK source. No GitHub Releases exist; development delivery is recorded in PR 17 and Drive.
- Latest Drive APK: `1CPyP8pK1ggEjTl1BPepAvDxzsrz_ocgb`, 87,874,512 bytes, existing folder `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`. Independently verified SHA-256: `9421a982296816f1ef53aa062ea608ed213dadc8475069fa9039202e5ee7898b` The complete Drive APK was subsequently downloaded and independently verified: digest/bytes, v1/v2/v3 signature, installed identity/version, ZIP CRC and embedded source SHA all match. See `BASELINE_0139_BINARY_VERIFICATION.json`.
- This owner's authorised 0.140 handover selects the latest corrective 0.139 development baseline. This is not a new claim that all historical 0.139 workstreams passed physical acceptance.
- Branch `codex/supernova-0.140-implementation-2026-10-16` created directly at the exact signed source, without resetting or modifying any prior branch.
- Planning source: `9f0a81efcf0a6065787186b2763439fa372aced5` on `docs/project-decisions-2026-10-12`. Only planning documentation copied; no application/workflow merge. Current release scope overrides historical unscheduled/parked wording.

## Implementation checklist

| Area | Current engineering result | Acceptance still required |
|---|---|---|
| FD-007 | Two-row continuous panel; nine metrics; logical-title/page scope; physical indexed storage and unknown-size bounds; calendar-month added timestamp; scan completion journal | Final exact-source suite and native renders pass; the FD-007 source-reference comparison is blocked by missing assets. Shows spanning sources count in both source sections. Series-added timestamp is earliest indexed episode, not latest episode arrival. |
| FD-033 | Six fixed read-only regions; reference-calibrated v3 proportions, three Reception separators, one compound user-accent border, reference-calibrated top placement without the application bar, circular 4×2 portrait grids and one-line middle-ellipsized path | Final full-suite/native checks pass; device acceptance and annotated v3 PNG comparison remain unavailable. Recovered JPEG comparison and geometry measurements are recorded. Existing IMDb artwork and a generic awards trophy reused/rendered; other provider names stand in for unavailable approved Reception artwork. |
| OMDb | Official API/licensing feasibility audit and disabled exact-ID adapter prepared; source/type/range validation, budget seam, bounded HTTPS and attributed Reception rendering | Owner confirms no authorised account/binding. Production remains denied. Activation requirements in [OMDB_FEASIBILITY.md](OMDB_FEASIBILITY.md); no live requests. |
| FD-001 | Cached exact-country classification overlay tied to actual playback-established callback; white outlined text/red accent, 500ms fades and ten-second full-opacity dwell; passive and omitted for missing/unrated metadata | Full software suite passes; playback/device/scene QA remains open. No verified descriptor feed: descriptors omitted. Series-wide versus episode classification semantics are documented; the passive overlay displays the classification only. |
| FD-008 | Existing native account/browser architecture retained; public client 9874 configured with provider-source-verified approval URL; initial public code/pending checks pass; explicit revoke action added | Real account authorisation, browse/sync/revocation/WebDAV playback and Shield focus validation. No token obtained or account accessed. |
| Backup & Restore | Export categories directly in main three-panel page; vertical list, existing consent/persistence model, contextual descriptions; Export starts existing verified archive flow | Software regressions pass; real SAF file-provider round trip and physical D-pad verification remain open. |
| Details/Extras corrections | Provider focus route, genre fallback, resolution deduplication, canonical background, compound border, synopsis balance, shared Home card renderer, horizontal Extras rails, heading-aware scroll and return to tabs | Software focus/render checks pass; real provider launch and physical visual acceptance remain open. |

## Visual dependencies

The approved overall FD-033 JPEG is now archived byte-for-byte at [references/FD-033-approved-goonies.jpg](references/FD-033-approved-goonies.jpg), with dimensions/provenance/checksum in [references/README.md](references/README.md). The received copy is 1280×720; earlier planning describes a 1536×864 copy. The separately annotated v3 PNG and FD-007 reference images are still missing and are not replaced by this JPEG. Native comparisons use the recovered reference plus written v3 corrections, including 4% margins; physical/device and pixel-perfect acceptance remain unclaimed. See [visual register](VISUAL_QA.md).

Draft PR: https://github.com/heretoecode/Supernova/pull/18 (base is the preserved 0.139 implementation branch; no main merge).

## External-source audit

- OMDb: https://www.omdbapi.com/ documents exact IMDb-ID requests, types movie/series/episode and API key requirement; https://www.omdbapi.com/legal.htm restricts use and does not establish this project's entitlement. No key or redistribution permission is inferred. Its US `Rated` field is not a substitute for the selected country's certification.
- Put.io's linked legacy documentation URL returned HTTP 404. The provider's own Android TV source `putdotio/putio-android` at `232a4e573b7668d8b562bb830f9e7ecec99cffab`, `tv/src/main/kotlin/io/putdotio/android/tv/TvLinkScreen.kt:137`, supplies `https://app.put.io/link?code=<encoded-code>`.
- Official SDK `putdotio/putio-sdk-typescript` at `7625b80c5a07a1a97225e6b5412f671b3a608fdb`, `src/domains/auth.ts`, confirms code GET/poll and app-grant revocation POST endpoints. Public initial-flow observations are in `PUTIO_PUBLIC_FLOW_CHECK.json`. Temporary codes, QR payloads and tokens were not retained in evidence.
- Existing Put.io storage is AES-GCM in Android Keystore-backed no-backup storage. WebDAV remains the existing original-quality playback transport. No native transfers/downloads or invented direct-streaming API is introduced.

## Validation and release

Exact-source signed CI [38959928321](https://github.com/heretoecode/Supernova/actions/runs/38959928321) passed **1,047 application tests without failures/errors/skips, all 88 tooling tests, and lint with zero errors / 1,775 warnings**. Full native comparisons pass (64 paired decode cases across four ABIs); all 88 packaged native hashes and 24 rebuilt FFmpeg libraries match. Earlier local failures, superseded runs, compatibility correction and their focused checks are recorded in [TEST_RESULTS.md](TEST_RESULTS.md), with no weakened lint/test/signing gate.

The owner resolved the environment's exact branch rule; readback preserves all six earlier entries and the selected-branch policy. The protected workflow signs only after its full conformance gate. Independent verification confirms the established certificate, application/version, embedded source, offline assets, native hashes, CRC and 16 KiB ZIP alignment. No signing key was exported to this workspace.

**Supernova_0.140.apk**, **87,890,896 bytes**, SHA-256 `b6d59e82c03cd99844f49d4647d2f2a985c484c819d92064542eec84f33788f5`. [Verified Drive download](https://drive.google.com/file/d/1JEYAXNKw4gGPgEx0xUjATDXES1bI158T/view?usp=drivesdk); full downloaded bytes match and all six earlier APKs remain intact. [Delivery receipt](DRIVE_DELIVERY_VERIFICATION.json). [HTML notes](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view) were updated in place and downloaded/verified, preserving older history. Source remains `866f21f11f27dea4846ddd3cac57d98b281eecbb`; later branch changes are documentation/evidence only.

No physical Shield/emulator/instrumentation, actual install/signed upgrade, real hardware playback/scan/provider/account or SAF round trip is claimed. [Device checklist](SHIELD_QA.md). OMDb production remains denied, and missing reference assets/original diagnostics remain external limitations. Main, prior implementation and planning refs remain unchanged by this work.
