# Supernova 0.141 candidate report

All ten approved workstreams are implemented on `codex/supernova-0.141-implementation-2026-10-22`; [draft PR 19](https://github.com/heretoecode/Supernova/pull/19) remains unmerged. The verified unsigned candidate source is **`4f0937f1fe8826f50d2c0e844b0837e8a2360a46`**. Subsequent delivery/evidence commits change documentation only.

Continuation baseline: complete preserved 0.140 tip `cf257aaa59ad8d59b2ab7252d5beae174f5879c5`, whose application source equals delivered `866f21f11f27dea4846ddd3cac57d98b281eecbb`. Main, 0.140, historical 4.1.7 and the planning branch remain preserved. No maintenance programme or main merge was started.

## Requirements conformance

The [requirements ledger](IMPLEMENTATION.md) covers every section of the approved plan: HUD/four-tab Settings; hero reversal/scrolling; continuous licence disclosure; global language/region icons; Statistics alignment/opacity; compact shared keyboard and Caps Lock; Put.io focus/auth lifecycle; right-pinned Edit Library; centred Library rows; movie/TV Details spacing.

[PLAYBACK_MIGRATION.md](PLAYBACK_MIGRATION.md) explicitly reconciles all five controls, 29 settings, 26 actions, seven indicators and 14 findings: **all 81 audit IDs have disposition rows**. All 22 original audit QA cases are retained with updated visible-control substitutions. [CONFORMANCE_REVIEW.json](CONFORMANCE_REVIEW.json) records coverage; it does not convert source mapping into hardware acceptance. B04 retains the inherited neutral global-preset sentinel semantics rather than introducing a preference migration.

The final focus regression reproduced selection of the first row after choosing the second identically labelled track. Refresh now preserves the matching occurrence while uniquely named tracks still follow insertions. The test failed before the fix and passes afterwards; its entire menu class is explicitly required by the 0.141 conformance/signing gates. No previous gate was removed.

## Confirmed software and binary results

[Exact-source unsigned CI 39869263544](https://github.com/heretoecode/Supernova/actions/runs/39869263544) completed successfully. The complete 104,213,283-byte evidence ZIP was downloaded, its SHA-256 matched GitHub's published digest, and its CRC passed. The APK and public receipts were independently checked; see [INDEPENDENT_UNSIGNED_APK_VERIFICATION.json](INDEPENDENT_UNSIGNED_APK_VERIFICATION.json).

| Check | Result |
|---|---|
| Complete local and CI application suites | **1,068 cases across 226 classes; zero failures, errors or skips** |
| Release lint | **Zero errors**, 1,785 warnings; unrelated warning remediation is outside this release |
| Tooling tests | **95 passed** |
| Required menu regression | Eight passing menu cases, including identical-label focus; four-tab native render also passes |
| Native runtime | **64 decode comparisons passed**, API23 bionic; x86_64 native and other ABIs under QEMU user |
| Rebuilt FFmpeg | **24 libraries matched** reviewed ABI/configuration/dependency guards |
| Packaged native payload | **88 library hashes matched**; all four ABI ELF headers independently checked |
| Runtime dependency inventory | **138 artifacts matched** |
| Legal disclosure | **60 full notice texts byte-matched** in the APK; inventory covers 135 unique runtime artifacts and 64 archive notices |
| Region icons after shrinking | **All 249 source region resources retained** |
| Identity | `app.supernova.player`, `0.141 / 141`, minSdk 23, targetSdk 36, label Supernova, non-debuggable |
| Archive integrity | APK CRC and 16 KiB ZIP alignment passed |
| Signing precondition | Public exact-source signing gate **PASS**; key preparation was not invoked |
| Baseline binary | Complete delivered 0.140 Drive APK, identity, CRC and original certificate independently verified |

Unsigned APK: **87,701,229 bytes**. SHA-256:

`bd3fdd4fec5ce37e1ab3c7689a7b23b416279dfe77cb86eaecda07361fed72c2`

Evidence archive SHA-256:

`58ad0e3edf8e38d5d183fef30e87dd068773c7092a772d00ad11407ffda40549`

The verified unsigned APK is in the `supernova-0141-unsigned-evidence` artifact on the successful CI run (artifact 12466443742; expires 5 November 2026). **It is unsigned, not an installable original-certificate QA delivery. No unsigned APK was uploaded to the live Drive APK folder.** The smaller local assembly probe is explicitly incomplete and must not be distributed.

The local chapter component additionally compiled all four wrapper ABIs and passed actual FFmpeg demux/AVOS metadata parcel checks on API28/x86 with the generated 12-second MKV. This does not establish the complete installed-player-to-HUD callback route or physical decoded output. Software view evidence is labelled synthetic where appropriate.

Host preparation now selects Ubuntu's official signed archive in only the two new 0.141 workflows. Measured preparation changed from 1,488 seconds on the slow Azure-mirror runner to 55 seconds on the new runner; package signature verification, dependency pins and release gates remain intact. See HOST_PREPARATION.json; this is an observed comparison, not a future speed guarantee.

## Signing and delivery blocker

**No signed 0.141 APK or verified Drive APK delivery is available.** The existing `supernova-foundation-signing` environment permits seven earlier branches and excludes this isolated branch. Adding only `codex/supernova-0.141-implementation-2026-10-22` returned **HTTP 403: Resource not accessible by integration**. The owner must permit that exact branch under the environment's deployment branches while preserving existing entries. See SIGNING_ACCESS.json.

The original protected signing procedure and certificate pin remain unchanged: `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e`. No replacement key or old-branch bypass is used. Once access is available, the signed QA workflow repeats exact-source conformance before key preparation; signed payload parity, identity, certificate, native hashes, CRC/alignment and complete Drive download verification must pass before delivery is claimed.

The existing Drive HTML changelog is updated in place at the same file ID, with complete readback verification and unchanged earlier history/CSS, parents and sharing. It reports the signing/acceptance limits; DRIVE_HTML_VERIFICATION.json records the current bytes and hash. Earlier APKs remain untouched.

## Physical acceptance still open

No physical NVIDIA Shield is attached and no real Put.io account authorisation is available. All [Shield acceptance cases](SHIELD_QA.md), including the 22 reconciled audit cases, remain **NOT RUN on physical Shield**. Real Put.io sign-in is release-blocking for integration acceptance. Also pending: signed upgrade, actual subtitle/AV-sync/output behaviour and provider/storage mutations, hardware/PiP/stereo/passthrough, hero frame timing, and final visual/focus acceptance. Automated tests, component decode probes and software renders do not close owner-reported physical defects.

The approved HUD image and referenced owner videos/photos were not found in repository/project assets or connected Drive searches. Implementation follows the complete approved written contract; software fixtures are not substitutes for the original evidence.

## Preserved validation history

The initial complete local checkpoint had 1,058 cases, four stale expectations and one API33-only licence read. Corrected expectations preserve original identity/view/restore checks; the reader now uses bounded API23-compatible streaming. Prior candidate 5109dd0c passed 1,067 local cases and the CI native stage; its application CI was superseded by the reproduced duplicate-label focus correction. The final 4f0937f1 complete local and CI suites pass 1,068 cases.

CI 39856341666 failed on missing historical 0.140 proof fields, corrected by carrying forward the independently verified original receipt. Runs 39856708422, 39858300413, 39860471321 and 39861343693 were superseded/cancelled as documented; no incomplete run is called a final pass. The first 39861343693 attempt was cancelled to inspect slow host downloads, and its retry passed native verification before the final focus correction. An early Put.io success simulation waited on an unrelated executor task; the harness now awaits the actual operation Future. Original checkpoints and receipts remain in this directory and Git history.
