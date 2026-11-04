# Handover to Claude — 2026-11-04

## Engine release — Claude's steps

Branch: `codex/0155-code-2026-11-04`. Follow `BRIEF_0155_ENGINE_BUILD.md`
from fetched Claude `a5c5b7b3a408ffa3adaa24f5cfbfe2bd1ce4e460`.
This section supersedes the historical preparation status below.

1. Archive first: manually dispatch `supernova-0156-native-archive.yml` on this
   branch, no inputs. It verifies run `40843685431` and publishes the four old
   ABI ZIPs/tarballs and lock/verification/manifest files to Release
   `native-archive-0146`. Check the Release receipts and downloaded asset hashes.
2. Native build: manually dispatch `supernova-0156-native-stack.yml` on this
   branch with `third_party_source=release`. `actions` is an explicit fallback
   while the old run is available, never an unverified fallback. Expect artifact
   `supernova-0156-native-arm64-v8a`: `Supernova_0.156_native_arm64-v8a.tar.gz`,
   `native-stack.json`, `native-supernova156.lock.json`, `archive.json`,
   `SHA256SUMS`, and `native156-build.log`. The tarball contains rebuilt
   avos/JNI/nativehelper/FileCore DSOs and verified unchanged FFmpeg/codec DSOs,
   with the complete library SHA-256 inventory. Review run head SHA and receipts;
   output hashes/run ID in the new lock deliberately say `pending-first-build`.
   Those placeholders are never accepted as package hashes.
3. App build: Claude must replace old native acquisition/preparation/packaging
   in the existing app workflow; do not run `package-supernova146-native.py`
   afterward. Exact protected-file changes and app integration status are being
   completed in ENGINE_BUILD items 2–4. Keep the native workflow output pinned
   by its reviewed run ID, source SHA and tarball SHA, with arm64-only packaging.
4. Run app/MediaLib/FileCore tests, native/JNI/ELF/API23/load/alignment tests,
   lint, then conformance and the Shield matrix before signing/delivery.

Current ENGINE_BUILD item 1: new manual-only workflow, builder, package installer,
shared validation and real-source lock. Ten offline recipe/package tests and nine
archive tests pass. All five avos patches apply in order and match the measured
tree; native ABI/HLS/buffer probes and thirteen upstream host tests pass.
Item 1 commit: `ecab7477d`. No workflow or NDK/Gradle build has run.

ENGINE_BUILD item 2a: app draft 01 applied as production Java with its three
Robolectric tests (upstream `5c7215144f72312b086eb314f514c7605d8e6c5f`).
No HDR SurfaceView transaction remains; the existing HDR window request stays.
Commit `07b4afbb7`; Robolectric tests written, not run. Player syntax parsed.

ENGINE_BUILD item 2b: app draft 02 applied with `VideoCodecSelectionTest`
and `PlaybackHdrCapabilitiesTest` (upstream `9ec1bdc26a4ec7086b7bc2b89722a46ef6858d48`).
Four actual ranking JUnit tests pass at Java 17 with ECJ/JRE21; seven
Robolectric tests await Claude. Player/utils Java syntax parsed.
Commit `80f9504d4`.

ENGINE_BUILD item 2c: app draft 03 applied with eight Robolectric padding tests
(upstream `73df6e4d714eed203237d045b20bac41f4799764`). Legacy preferences
retain the existing line/block padding, including zero; no Settings control was
added. New `nativeSetPadding` must ship with this engine, never the old DSOs.
Player production and test syntax parsed; Robolectric unrun. Continue at item 3,
upstream `8bbfb666` vertical subtitle canvas port.

The following table records **earlier engine preparation** and MORE/NEXT work;
its unapplied-draft and Claude-only overflow statements are historical, superseded
by this release section as integration proceeds.

| Item (engine release first) | Commits | Files | Tested how | How to apply | Open questions / next action |
|---|---|---|---|---|---|
| ENGINE 1 — NOVA 6.6.4 plan | `28253b6f9` | `findings/engine-0156-plan.md` | Offline local `git show` of every named commit; 51-commit native range counted; current app and prepared MediaLib file:line comparison | Documentation only. Freeze avos `5cf3ae9c1f70189d4180a62722150fa76b405a0f` | Item 3 needs ranking/HDR and padding plumbing; retry, association and cache fallback already exist. Claude owns subtitle canvas layout and any visual default/control changes. |
| ENGINE 2 — native patch stack | `e5cb41247` | New `.github/build/avos-supernova156-01-metadata.patch` through `05-test-harness.patch`, `native-supernova156.lock.draft.json`, `verify-native-supernova156-draft.py`; engine plan | All five pass sequential `git apply --check` in a clean disposable clone; result tree locked. Thirteen upstream host tests plus native ABI/HLS and buffer probes passed | New avos tip first, **only** these five patches in JSON order. Replace old avos operations atomically during Claude's later protected-build integration. Reuse verified old third-party stack and rebuild wrappers | Production build/NDK, real JNI chapter parcel, full libass/timebase/sanitizer and device acceptance pending. No existing `.github/` file edited; no Gradle run. |
| ENGINE 3 — HDR producer correction, upstream `5c721514` | `4d59e399d` | New `.github/build/video-supernova156-01-hdr-producer-5c721514.patch`, bundling Player/SurfaceController internal HDR deletion and new `PlayerHdrProducerContractTest` | Clean `git apply --check`, exact post-apply tree and all three Java files syntax parsed; three Robolectric tests written but not run | First app draft, apply in the engine integration checkout, then compile/run tests with new native HDR signalling | Current production app unchanged. No layout/control/geometry changes in the patch. |
| ENGINE 3 — ranked candidates / live HDR, upstream `9ec1bdc2` | `6f04ae3ff` | New `.github/build/video-supernova156-02-codecs-9ec1bdc2.patch`: Player open hook, CodecDiscovery, VideoCodecSelection and two new tests | Clean ordered apply/exact tree and five Java syntax checks. Four actual ranking JUnit tests passed at Java 17 using ECJ/JRE21. Seven Robolectric display/API/filter tests written but not run | Apply after app draft 01; pair native JNI callbacks/candidate loop with the new avos build | Existing audio/spatializer helpers, source generations and DP-08 redaction retained. |
| ENGINE 3 — box padding plumbing, upstream `73df6e4d` | `93803fe7b` | New `.github/build/video-supernova156-03-padding-73df6e4d.patch`, new app draft manifest; SubtitleEngine/SubtitleManager and eight new tests bundled in patch | Clean ordered apply/exact complete draft tree; three Java files syntax checked. Eight Robolectric tests written but not run | App draft 03 after 01/02, **only with rebuilt new avos JNI exports**. Existing source/native build untouched | Missing key preserves both legacy box modes, widths and zero without preference writes; explicit key separates padding. No Settings/model/preview/resource/control changes. Native rendering/JNI acceptance and full Android test run pending. |
| ENGINE 4 — owner Shield test plan | `2cb013522` | `findings/engine-0156-shield-tests.md` | 23 plain-language steps checked against requested format/file matrix; device tests not run | Owner runs after Claude's engine/native/Java integration, with old-build baseline and saved settings | Includes DV 5/7/8/9, AV1/profile10 limits, HDR10/HDR10+/HLG/SDR, TS resume, multivideo, SRT/ASS/PGS boxes, Fit width/zoom, controlled network reopen, 3D and display reconnect. ASS layout integration is Claude's gate. |
| NEXT 1 — durable old native archive | `56789688a` | New `native-archive-supernova156.py`, offline test and `supernova-0156-native-archive.yml`; `findings/native-archive-plan.md` | Nine offline Python tests passed; existing lock/recipe hashes verified; read-only run metadata checked | Review, then manually dispatch before 27 Jan 2027. Do not substitute a new engine for the preserved old stack | Workflow unrun, Release unpublished by Codex. Existing acquisition unchanged; follow the migration plan and verify Release bytes before switching. |
| NEXT 2 — DP-08 source log privacy | `46c8cc64e` | `player/Player.java`; `src/test/.../player/PlayerSourceLoggingTest.java` | Java syntax passed; Robolectric written but not run | Already in this branch; compile and run with existing player tests | Verify native source opening and captured logs on the full Android workspace. |
| MORE 1 — compatibility intent test | `ece96827b` | `src/test/.../browser/QueryCompatibilityTest.java` | Test-only fix; JUnit/Robolectric not run by Codex | Already in branch: pass a copied Intent to Robolectric | Claude's full test run is the validation authority. |
| MORE 2 — network scan compensation/cancellation draft | `7a26c7488` | New `filecore-supernova155-scan.patch`, `medialib-supernova155-scan.patch`; `findings/stuck-network-scan-draft.md` | Ordered `git apply --check` on prepared dependencies; five standalone JUnit/MockWebServer tests passed; 20 Android tests written but not run | Existing reviewed module patches first; new FileCore draft, then MediaLib draft. Existing build lock intentionally not edited | Remaining integration/termination gates are listed in the findings/report. New DP-10 FileCore hook belongs in a companion patch per ENGINE 6; don't reach into FileCore internals from the app. |
| MORE 3 — Library Health counts | `5988e19c4` | `findings/library-health-counts.md` | Analysis only, proving tests described | Claude/owner read the count/cleanup reconciliation contract | Health implementation/UI remain hands-off. |
| MORE 4 — TV-only stage-5 device checklist | `f1edb7184` | `findings/tv-only-stage5-device-checks.md` | 18 steps, named candidate files checked; device checklist not run | Owner runs on Shield; record reachable routes before removals | No UI/resource removal authorized in these briefs. |
| MORE player hunt progress | `81a22f705`, report `3cd1a00fb` | `findings/deep-hunt-1-player.md`; `reports/2026-11-04-0155-code-hunt1-more.md` | 24 additional complete file reads; historical ledger 82/85 at recorded snapshots | Preserve provenance; ENGINE 6 resumes NEXT after engine work | NEXT 6 still needs complete PlayerActivity/PlayerController/PlayerService reads. DP-09 belongs to Claude; DP-10 is NEXT 3. |

Reports already in `docs/codex/reports/` give the detailed scope and validation
limits for each earlier run. No previous draft is automatically wired into the
current dependency preparation or approved as a production build.

For engine integration, use the exact native pin and five new avos patches in
the native draft lock, replacing the old avos operation sequence together.
Retain the verified old third-party stack; its libass/font stack is static,
while FFmpeg/media runtime dependencies are shared. Rebuild wrappers. Apply the
three app patches in their manifest order and ship the new padding export and
Java callers together. The app result-tree record describes the exact Codex
fixture, not a request to overwrite newer Claude code. Then run the bundled
22 Java tests (four ranking tests already passed offline; 18 Robolectric tests
remain unrun), full native/JNI/conformance checks and the Shield plan.

Claude's remaining engine coordination: subtitle canvas overflow geometry from
`8bbfb666` (layout, not edited here); any supported horizontal overflow case;
actual native/Java installation and build-pin/receipt integration. Padding UI,
visual resource defaults and the IntroDB library change are excluded from the
internal engine drafts. All delivered patch hashes were rechecked against both
new manifests. Existing `.github/` and protected files remain untouched.

Exact continuation: **ENGINE item 6, NEXT item 3 (DP-10)**. Add a new
`.github/build/filecore-supernova155-*.patch` supplying an owned listing
cancellation/deadline hook after the existing reviewed FileCore patches and
earlier scan draft. Do not edit either existing draft or existing build lock,
and do not reach into FileCore's private clients from app code. Complete the
UpdateNextTask cancellation/traversal fence and silent/trickle/repeated-replace
worker-exit/no-late-callback/shared-client tests in one DP-10 commit. Preserve
file selection; DP-09 is Claude's. Then continue NEXT item 4 and onward.
