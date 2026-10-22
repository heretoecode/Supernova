# Supernova 0.141 candidate report

Previous candidate application source: `5109dd0c83082528b896a8aaa6d0d50f4a930027` (superseded by the duplicate-track focus correction; repeat full validation pending) on `codex/supernova-0.141-implementation-2026-10-22`. [Draft PR 19](https://github.com/heretoecode/Supernova/pull/19) continues from preserved 0.140 tip `cf257aaa59ad8d59b2ab7252d5beae174f5879c5`; the delivered baseline application source is `866f21f11f27dea4846ddd3cac57d98b281eecbb`.

All ten approved implementation workstreams are represented in [the requirements ledger](IMPLEMENTATION.md). Every FD-002 control/action/indicator and B01–B14 has a disposition in [the playback migration register](PLAYBACK_MIGRATION.md). This is a development candidate, not completed physical acceptance.

## Validation

| Evidence | Result |
|---|---|
| Targeted Android regressions | 58 cases, 14 classes, zero failures/errors/skips; see LOCAL_TARGETED_TEST_RESULTS.json |
| Additional language/wizard regressions | 28 cases, four classes, zero failures/errors/skips; overlaps the targeted group |
| Put.io focus/link lifecycle | Five cases, zero failures/errors/skips; simulated provider and secure-store boundary, no real authorisation |
| Tooling tests | 95 passed |
| Release lint | Zero errors, 1,785 warnings; unrelated warning remediation is outside this release |
| Local packaging probe | R8/resource shrinking and unsigned assembly passed; all 249 region assets and 60 notice texts retained; CRC/alignment pass. **Not a candidate:** only 16/88 native libraries and old checkout metadata; see LOCAL_ASSEMBLY_PROBE.json |
| Previous complete local suite | **1,067 tests / 226 classes; zero failures, errors or skips**, BUILD SUCCESSFUL in 21m 8s; see LOCAL_FULL_TEST_RESULTS.json |
| Previous exact-source unsigned CI | [Run 39861343693](https://github.com/heretoecode/Supernova/actions/runs/39861343693), attempt 2 in progress |
| Native chapter component | All four wrapper ABIs compiled; actual API28/x86 FFmpeg demux and AVOS metadata parcel passed with the generated 12-second MKV; see NATIVE_CHAPTER_PROBE.json |
| Legal disclosure | 138 runtime artifacts, 135 unique, 64 archive notices, all 60 catalogue entries retained; all notice texts losslessly partitioned |
| Drive changelog | Updated existing HTML file in place; complete 32,785-byte readback matched; earlier history/CSS and sharing retained; see DRIVE_HTML_VERIFICATION.json |
| View evidence | Actual native Android software renders under visual-evidence; playback scene explicitly synthetic |
| Delivered 0.140 baseline | Full Drive APK checksum, ZIP CRC, identity and original certificate independently verified |

The local native component used the pinned original prebuilt FFmpeg inputs. The final CI native rebuild/comparison stage has now passed. Complete native APK payload checks and final unsigned binary conformance remain pending until the application stage completes. No complete player-to-HUD chapter test or physical decoded output is inferred from the component probe.

The first full local run completed 1,058 cases with four stale assertions and one API33-only licence-read lint error. Assertions were reconciled with approved punctuation/HUD wording and deterministic row settling; the reader now uses bounded API23-compatible streaming. Corrected cases passed targeted reruns. The receipts retain these initial failures. An additional Put.io success simulation initially waited on the wrong executor task; the harness now awaits the actual operation Future. An intermediate full run was interrupted for the final lifecycle correction and is not a pass.

The first attempt of final run 39861343693 was cancelled to inspect its unusually slow preparation step: Ubuntu Azure mirror downloads were only tens of kilobytes/second for 96.2 MB of host packages. No application stage had run. Attempt2 retries the same source and checks on a fresh runner.

Earlier unsigned CI runs: `39856341666` failed on missing historical 0.140 proof fields, corrected by carrying forward the independently verified existing receipt; `39856708422`, `39858300413` and `39860471321` were cancelled when their source was superseded. No conformance gate was weakened.

## Signing and delivery

No signed 0.141 APK or Drive APK upload is currently claimed. The existing `supernova-foundation-signing` environment permits seven earlier branches and excludes this isolated branch. Adding only `codex/supernova-0.141-implementation-2026-10-22` returned HTTP 403, “Resource not accessible by integration”. The repository owner must add that exact branch under deployment branches while preserving the existing rules.

The original protected signing procedure and pinned certificate are retained. Only a complete passing exact-source conformance result can reach key preparation. No replacement key, old-branch bypass, main merge or public release is authorised or performed. If signing becomes available, verify the complete APK, unsigned/signed payload parity, package/version, native hashes, CRC/alignment and original certificate before uploading; then download the complete Drive copy and compare its checksum.

## Physical acceptance

No NVIDIA Shield is attached, and no real Put.io account authorisation is available. All [physical acceptance cases](SHIELD_QA.md), including the 22 reconciled audit cases, remain owner QA. Real Put.io sign-in is release-blocking for integration acceptance. Also pending: signed upgrade, actual AV sync/subtitle output and provider/storage mutations, hardware/PiP/stereo/passthrough, hero frame timing and final visual approval. No owner-reported issue is declared physically fixed from compilation or software renders.

The approved HUD image and referenced owner videos/photos were not found in the repository or connected Drive searches. Implementation follows the complete approved written contract; software fixtures are not substitutes for the original evidence.

Main, 0.140, the historical 4.1.7 branch and planning branch remain preserved; see PRESERVATION_READBACK.json. The separate maintenance programme has not started.

## Final duplicate-label focus correction

A new regression reproduced focus returning to the first track after selecting the second identically labelled track. Refresh now restores the matching occurrence; uniquely named tracks still follow insertions. The original seven menu tests, the new regression and four-tab native render pass (nine cases total). `PreviewPlaybackMenusTest` is now explicitly required by the0.141 unsigned and signing gates. See DUPLICATE_TRACK_FOCUS.json. The full local suite and exact-source CI are being repeated for this correction; the earlier1067-case result is not attributed to the new source.

Only the new0.141 workflows now select Ubuntu's official signed archive for host preparation, following the observed Azure mirror slowdown. Package signature verification, dependency pins and all native/binary/test/signing gates are unchanged. No0.140 workflow is modified.
