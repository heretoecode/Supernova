# Supernova 0.140 implementation and release evidence

Status: implementation in progress, not released. No main merge. No physical Shield access.

## Baseline and authority

- Exact signed corrective 0.139 source: `a4841c1dbaac5e1037fb19f9f11882848b435e0b`.
- PR 17 head at inspection: `d173e8dd9ea41834118a32e240518d91e15e11d1`. Its changes after the APK source are documentation and runtime evidence only (`git diff --name-status` verified).
- Signing run 38908109847 completed successfully at the exact APK source. No GitHub Releases exist; development delivery is recorded in PR 17 and Drive.
- Latest Drive APK: `1CPyP8pK1ggEjTl1BPepAvDxzsrz_ocgb`, 87,874,512 bytes, existing folder `1mfHrFgLdJJSJYtTPys1N4Y-_KuE_84mM`. Prior recorded SHA-256: `9421a982296816f1ef53aa062ea608ed213dadc8475069fa9039202e5ee7898b` (prior release evidence; this task has not independently downloaded that APK).
- This owner's authorised 0.140 handover selects the latest corrective 0.139 development baseline. This is not a new claim that all historical 0.139 workstreams passed physical acceptance.
- Branch `codex/supernova-0.140-implementation-2026-10-16` created directly at the exact signed source, without resetting or modifying any prior branch.
- Planning source: `9f0a81efcf0a6065787186b2763439fa372aced5` on `docs/project-decisions-2026-10-12`. Only planning documentation copied; no application/workflow merge. Current release scope overrides historical unscheduled/parked wording.

## Implementation checklist

| Area | Current engineering result | Acceptance still required |
|---|---|---|
| FD-007 | Two-row continuous panel; nine metrics; logical-title/page scope; physical indexed storage and unknown-size bounds; calendar-month added timestamp; scan completion journal | Tests, actual renders, source-reference comparison. Shows spanning sources count in both source sections. Series-added timestamp is earliest indexed episode, not latest episode arrival. |
| FD-033 | Six fixed read-only regions; provisional v3 proportions, three Reception separators, one compound user-accent border, circular 4×2 portrait grids and one-line middle-ellipsized path | Build/tests, supported viewport fit, actual source JPEG/v3 comparison. Provider names currently stand in for unavailable approved Reception artwork. |
| OMDb | Official API/terms audit started; no existing key/configuration found; no live request enabled | Authorised credential/entitlement and permitted-use confirmation. Stable missing-data Reception slots remain visible. |
| FD-001 | Cached exact-country classification overlay tied to actual playback-established callback; white outlined text/red accent, 500ms fades and ten-second full-opacity dwell; passive and omitted for missing/unrated metadata | Tests and playback/device/scene QA. No verified descriptor feed: descriptors omitted. Series scope labelled explicitly. |
| FD-008 | Existing native account/browser architecture retained; public client 9874 configured with provider-source-verified approval URL; initial public code/pending checks pass; explicit revoke action added | Real account authorisation, browse/sync/revocation/WebDAV playback and Shield focus validation. No token obtained or account accessed. |
| Backup & Restore | Export categories directly in main three-panel page; vertical list, existing consent/persistence model, contextual descriptions; Export starts existing verified archive flow | Regression tests, full SAF file-provider round trip and D-pad/device verification. |
| Details/Extras corrections | Provider focus route, genre fallback, resolution deduplication, canonical background, compound border, synopsis balance, shared Home card renderer, horizontal Extras rails, heading-aware scroll and return to tabs | Runtime and visual verification; not yet accepted. |

## Visual dependencies

The scope and FD-033 registers explicitly state the original clean 1536×864 JPEG and annotated 2400×1680 v3 PNG were conversation-only, not archived. FD-007 also lacks its clean approved raster/annotated blueprint. Repository tree and Drive searches did not locate these files. The SVG is a geometry guide, not an approved replacement. No pixel-perfect or source-image conformance claim is made. The owner has been asked for existing links/paths while engineering continues.

## External-source audit

- OMDb: https://www.omdbapi.com/ documents exact IMDb-ID requests, types movie/series/episode and API key requirement; https://www.omdbapi.com/legal.htm restricts use and does not establish this project's entitlement. No key or redistribution permission is inferred. Its US `Rated` field is not a substitute for the selected country's certification.
- Put.io's linked legacy documentation URL returned HTTP 404. The provider's own Android TV source `putdotio/putio-android` at `232a4e573b7668d8b562bb830f9e7ecec99cffab`, `tv/src/main/kotlin/io/putdotio/android/tv/TvLinkScreen.kt:137`, supplies `https://app.put.io/link?code=<encoded-code>`.
- Official SDK `putdotio/putio-sdk-typescript` at `7625b80c5a07a1a97225e6b5412f671b3a608fdb`, `src/domains/auth.ts`, confirms code GET/poll and app-grant revocation POST endpoints. Public initial-flow observations are in `PUTIO_PUBLIC_FLOW_CHECK.json`. Temporary codes, QR payloads and tokens were not retained in evidence.
- Existing Put.io storage is AES-GCM in Android Keystore-backed no-backup storage. WebDAV remains the existing original-quality playback transport. No native transfers/downloads or invented direct-streaming API is introduced.

## Validation and release

Validation is ongoing. Initial Python suite: 81 tests passed. Local build required installing an isolated JDK/SDK and configuring the existing proxy plus system CA trust; initial environment failures do not indicate application defects. Android compilation, complete regression results and any unavailable checks must be recorded before release clearance.

The existing 0.139 signing workflow pins branch/version and cannot sign 0.140 unchanged. Preserve its certificate (`79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`) and exact unsigned/signed payload gates. No 0.140 signing dispatch or Drive publication has occurred. Do not represent a development implementation checkpoint as a completed release.
