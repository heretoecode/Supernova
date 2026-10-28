# Supernova 0.144 / code 144 — release candidate delivery record

Application source is frozen at `4e6f1f8f5691c34cd429c17421f92b6290ce3539` on `codex/supernova-0.144-implementation-2026-10-28`. [Draft PR #24](https://github.com/heretoecode/Supernova/pull/24) is stacked on the preserved 0.143 delivery branch. Main is unchanged; no public release is authorized or published.

## Implementation

See the complete [requirement matrix](IMPLEMENTATION.md) and [candidate changelog](RELEASE_NOTES.md). Work includes WebDAV absence/sidecar handling and conservative scoped reconciliation; distinct library removal/source deletion; immediate scan action and coalescing; library focus/artwork isolation; global Settings presentation/copy/entry; compact playback settings and inline formats; HUD, Play/Pause, seek pointer and preparation fallback; and Details ratings/review-entry/metadata spacing. Existing regional PNG flags and established-output age-rating overlay were verified and retained.

## Validation and artifact

The local final release-gate run passed 22 cases with zero failures. Prior focused correction runs passed 40 and 14 cases; the [machine-readable record](LOCAL_VALIDATION.json) keeps individual results without presenting partial runs as full-suite clearance. Tooling passed 120 checks. Latest local release lint: zero errors and 1,796 warnings.

[Protected exact-source run 40735286063](https://github.com/heretoecode/Supernova/actions/runs/40735286063) **passed**: 1,135 Android tests, zero failures/errors/skips; 120 tooling checks; release lint zero errors/1,796 warnings; 138 runtime artifact hashes, 24 rebuilt FFmpeg libraries and 88 packaged native libraries matched. Signing used the existing certificate and verified exact tested-unsigned payload parity; temporary signing material cleanup passed.

[Signed Supernova_0.144.apk](https://drive.google.com/file/d/1bDjcD3Fkl-X3FBxaQ7meVv8VLv6UpwZl/view?usp=drivesdk) — **87,940,129 bytes**. SHA-256:

`a841b6bcb49296b8968a40eef1b305178bfadb5f26aff759e5ee6aef5552ea61`

Independent signature/certificate, version/identity, ZIP CRC, 16 KB alignment, all 88 native hashes and full Drive byte readback pass. APK minimum SDK is 23, target SDK 36; universal ABIs are arm64-v8a, armeabi-v7a, x86 and x86_64. These establish automated package compatibility, not a physical Shield installation or upgrade.

[Delivery receipt](SIGNED_QA_DELIVERY.json), [signed public proof](SIGNED_PUBLIC_QA_EVIDENCE.json), [unsigned conformance](CONFORMANCE_40735286063.json), [independent checks](INDEPENDENT_APK_VERIFICATION.json) and native evidence files record provenance. The corresponding FFmpeg source artifact was retrieved and its archive digest verified.

The existing [Drive HTML notes](https://drive.google.com/file/d/17P05N7Q8c0TQdeZIEjUKYoQ-4xZIz-DW/view) were updated in place with this delivery and complete HTML readback passed. All 12 historical articles and ten earlier APK files remain preserved.

## Evidence limits and unresolved items

- No Nvidia Shield is attached. All [physical checks](SHIELD_QA.md), actual signed upgrade, real WebDAV/storage/account workflows and AV-chain/subtitle/HUD acceptance remain pending.
- [Software visual review](VISUAL_REVIEW.md) and [capture manifest](software-renders/manifest.json) distinguish native fixture renders from hardware evidence. Settings covers 720p/1080p/4K/150% text; playback panel and Details captures are included.
- The original Concept A4 Audio/Video files (`IMG_4968(1).jpeg`, `IMG_4967(1).jpeg`) and Slow Horses/Michael hero photographs are inaccessible. Exact reference comparison and any remaining inline-adjustment/geometry conformance cannot be claimed complete.
- The original `Supernova Diagnostics - 2026-10-27 21-48-16-706.zip` is inaccessible. Source-confirmed defects were addressed; original-report-only events were not re-analysed or claimed reproduced.

The branch environment restriction initially rejected signing. The owner subsequently allowed this exact branch; API readback confirmed the rule. Existing key/certificate, minimum full-suite/native/lint/source/payload gates and temporary-key cleanup remain intact.
