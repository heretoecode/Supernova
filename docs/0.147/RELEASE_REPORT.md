# Supernova 0.147 development QA — 29 October 2026

The three approved priorities are implemented and exact-source software conformance passes. [Draft PR #27](https://github.com/heretoecode/Supernova/pull/27) remains stacked on unmerged 0.146. Signed QA delivery is now complete after the owner authorized the branch. No main merge, tag, public release, application rebuild or signing-protection change was performed.

## Source and authority

- Frozen tested application source: `f4abf7cb880243ba00481f82beee19d20bc72e33`. Subsequent commits contain `docs/0.147/` evidence only.
- Implementation branch: `codex/supernova-0.147-implementation-2026-10-29`.
- Starting point: validated 0.146 application `938885ac2a84a5efecb1674b3e9f1032a56740ed`, with release-documentation-only ancestry through `8e3cf4e1e9ca66d9528598ffdef30c963b194496` (PR #26). Main did not contain 0.146 and was not used as the implementation base.
- Authority: documentation branch `docs/network-files-settings-design-next-version-2026-10-29`, commit `58cac445b1dd66ba8ad0a4b6ddd12008fbb3144a`, complete next-version handover and its referenced FD-003 audits, Network blueprint/assets, Settings blueprint/mapping/row design and age-overlay specification. Historical rail geometry is superseded only for presentation.
- Release history assigns next unused version 0.147/code147. All 145 older history records and the original 0.146 current fields/notes are preserved. Actual existing 0.146 delivery metadata was added when moving that record into history; the source gate was retained.
- Signed 0.146 baseline archive was downloaded completely; its APK bytes match the existing verified delivery receipt. See [baseline readback](BASELINE_BINARY_READBACK.json). This is not a new physical acceptance claim.

## Implemented behavior

Network & Files now opens a full-width 3×2 landing in the approved order: Local Storage, Network Locations, Add Connection, Manage Connections, Network Scanning and Advanced Options. Cards reuse Settings surfaces, gutters, typography, focus treatment and viewport scaling. Nested forms use structured rows and contextual help; actual browsing retains its dedicated file/context workspace. The permanent category rail is removed.

All six existing protocols, connection fields, keyboard punctuation, three browser toolbar controls, six sorts, breadcrumbs, saved-location/library distinction, exclusions and staged review remain reachable. Put.io account, OAuth, association and API browse/search remain under Network Locations and Manage Connections. All five Library Health categories remain under Advanced Options. Scheduling and Boolean form controls reuse the Settings switch style. Deep-folder, provider, form, Back and global-navigation focus transitions are covered by retained and new regressions. Scan indicators retain global ownership while scans continue off-page.

Settings now wraps its existing eight-category workspace in the same `TopNavigation` component. The redundant Settings brand/clock header is removed. Existing registry, preference map, native preference keys and capability/dependency behavior remain intact. Entry into and return from navigation, category Back, initial focus and visible compact navigation hit areas are tested. Compact header typography follows the existing viewport scale; the reference geometry remains unchanged.

The inherited age-rating overlay is corrected rather than duplicated. Its passive upper-left white outlined certification and slim red accent have no panel or focus target. Its ten-second deadline starts with established playback output and ends with a gentle 500 ms fade. Launch generation and URI prevent cross-launch output reuse; late metadata does not extend the deadline. Selection uses configured country, with no foreign-country fallback; conflicting regional classifications stay hidden. Existing movie/episode metadata and playback hooks remain in use.

## Exact-source software validation

[Full conformance run 40945894870](https://github.com/heretoecode/Supernova/actions/runs/40945894870) and [focused/render run 40945894979](https://github.com/heretoecode/Supernova/actions/runs/40945894979) both succeeded at the frozen source.

| Check | Result |
| --- | --- |
| Application | 1,221 tests / 250 classes; zero failures, errors or skips |
| MediaLib | 200 cases / 39 classes; zero failures/errors, two documented skips |
| FileCoreLibrary | 104 cases / 19 classes; zero failures/errors, two documented skips |
| Focused UI/provider/overlay regressions | 88 tests / 14 classes; zero failures/errors/skips; overlap with full suite |
| Tooling | 134 tests pass locally; same suite required by successful CI step |
| Release lint | Zero errors, 1,489 warnings; warnings remain outstanding |
| Unsigned universal release APK | Build and manifest/resource/source checks pass; unsigned signature rejected as required |
| Preserved payload | 84 native hashes, 24 rebuilt FFmpeg hashes, 139 runtime artifacts, 64 offline notice entries match guards |
| Independent complete artifact readback | All three ZIP SHA-256 digests and CRCs match; APK/source/native hashes and public unsigned signing gate pass |
| Software visuals | Native Robolectric renders at 720p, 1080p and 4K; large-text/focus assertions; production Settings activity renders |

All 1,525 application/library cases were accounted for: 1,521 passed and four skipped. The skips are `RealDatabasePruningTest.syntheticUsbBacklogIsPrunedAndLeavesDatabaseConsistent`, `RealDatabasePruningTest.realDatabaseUpgradesCleanlyAndPassesIntegrityCheck`, `WebdavPropfindBenchmarkTest.compareMinimalPropertiesWithAllprop` and `SpeedTestTransferTest.compareTransferRates`. They are retained declared skips, not newly executed hardware/database acceptance.

The 28 Android API23 transport/trust cases and source-built native security provenance are pinned prior 0.146 evidence reused by the conformance guard, not new 0.147 device executions. Native libraries are byte-identical to the independently read signed 0.146 baseline. All native ZIP entries in this unsigned payload are compressed; the local check has zero uncompressed entries to test for 16 KiB ZIP alignment. No local signing or APK rebuild occurred.

Receipts: [full conformance](UNSIGNED_CONFORMANCE.json), [artifact and JUnit readback](FINAL_VALIDATION.json), [independent APK verification](INDEPENDENT_UNSIGNED_APK_VERIFICATION.json), [software image provenance](ui/PROVENANCE.json) and [visual comparison viewer](ui/index.html). APK artifact retention is 14 days; conformance evidence is 90 days. Repository screenshots/receipts retain the review evidence after artifact expiry.

## Safety and traceability

[Source preservation](PRESERVATION_CHECK.json) records 71 unchanged manifest/build/preference/native-patch/signing-input files. Existing signing workflows and scripts, package identity, credential stores, preference map, database schema and pinned dependencies are preserved. No existing user data was accessed or migrated. These source guarantees do not prove an installed upgrade; that remains pending.

[Traceability](TRACEABILITY.md) accounts for all 202 FD-003 entries: 92 control groups, 28 state families, 15 conditional defects and 67 physical cases. Routes are mapped individually and immutable baseline/historical test references retained. A successful suite is not an individual real-provider pass for every ledger row. All 67 physical cases remain NOT RUN.

Earlier validation failures were resolved before freezing: a screenshot helper visibility error, missing receiver permission in the test fixture, a genuine compact-header clipping defect, missing preserved 0.146 delivery fields in the source-history gate, and a test selector that confused the Add Connection heading with the exact Connect action. Required gates were not relaxed. Earlier runs are historical checkpoints, not acceptance for this source.

## Signed QA delivery and outstanding acceptance

The initial branch-policy blocker was resolved by the owner. Live verification confirms the exact implementation branch is now one of 14 selected branches in `supernova-foundation-signing`; custom branch restrictions remain enabled and administrator bypass remains disabled. No protection policy was changed by this task. [Authorization readback](SIGNING_AUTHORIZATION.json) records the observed policy.

[Protected signing run 40955806102](https://github.com/heretoecode/Supernova/actions/runs/40955806102) succeeded at workflow branch head `c4c077197fc882c6dba02ae458b74f6d5e5375f1`. Its source/ancestry gate checked out the frozen application source and signed the exact previously tested unsigned artifact from run 40945894870, without rebuilding or modifying the application. Existing certificate pin, successful-run/source checks, report-only ancestry, payload parity and mandatory signing-material cleanup all passed. No signing keys were downloaded locally.

Delivered [Supernova_0.147.apk](https://drive.google.com/file/d/1TetN852eP2_lMfUuIV6S9Xv6zASprOm8/view?usp=drivesdk) to the existing Drive APK folder: **95,573,687 bytes**. SHA-256: `50cfb990af2ecc5273e90e11195fb73c7e1c457fad39ef6ee1375d36287f0453`. Exact application source: `f4abf7cb880243ba00481f82beee19d20bc72e33`.

Independent local verification confirms v1/v2/v3 cryptographic signatures for the existing certificate `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`, application identity/version, embedded exact source, complete ZIP integrity, all 2,445 non-signature payload entries identical to the validated unsigned APK, all 84 retained native hashes and native ELF load alignment. The complete Drive copy was downloaded and byte-compared to this verified APK. All 14 prior folder entries remain present; no sharing settings or existing APKs were changed. See [public signing receipt](SIGNED_QA_EVIDENCE.json), [independent verification](INDEPENDENT_SIGNED_APK_VERIFICATION.json), [signing steps](SIGNING_RUN.json) and [Drive delivery readback](DRIVE_APK_DELIVERY.json).

Physical Shield appearance/overscan, D-pad and keyboard, HDMI/video/subtitle composition, playback-start/resume timing, real SMB/WebDAV/SFTP/FTP/FTPS, disposable USB unplug/replug, live Put.io OAuth/account lifecycle, offline/partial scans, user-data preservation and signed 0.146→0.147 upgrade remain pending. Follow [QA preparation](QA_PREPARATION.md) and the 67 physical ledger cases. The user's report that 0.146 opens on Shield is retained as context, not a 0.147 acceptance result.

The HTML notes preserve all 15 prior articles byte-for-byte. Their 0.147 entry now identifies the verified signed QA delivery and retains explicit pending physical/device acceptance. The top notice identifies 0.147 as the latest delivered signed build. Drive update/readback status is recorded separately in `DRIVE_HTML_UPDATE.json`.
