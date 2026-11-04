# Handover to Claude — 2026-11-04

## Engine release — Claude's steps

Branch: `codex/0155-code-2026-11-04`. Follow `BRIEF_0155_ENGINE_BUILD.md`
from fetched Claude `a5c5b7b3a408ffa3adaa24f5cfbfe2bd1ce4e460`.
This section supersedes the historical preparation status below.

1. Archive first: Claude starts `supernova-0156-native-archive.yml` with no inputs
   from the reviewed recipe branch (manual dispatch, or a push changing only its
   own workflow path on `claude/supernova-github-access-yfhe89`). It verifies run
   `40843685431` and preserves the four original ABI ZIPs plus lock, verification
   receipt and manifest in the **orphan branch `native-archive-0146`**, one commit
   and nothing else. Each file is strictly below 100 MB. An existing branch must
   match exactly; no overwrite. Check the final fresh-fetch hash verification.
   No Release is needed. If job-token push is also denied, the owner follows
   the exact one-time computer commands in `findings/native-archive-plan.md`.
2. Native build: manually dispatch `supernova-0156-native-stack.yml` on the
   reviewed recipe branch with `third_party_source=branch` (now the default).
   It checks the complete archive tree/manifest against the local reviewed
   lock and all four ZIP hashes, then the selected tarball/receipt/library bytes.
   `actions` is an explicit fallback
   while the old run is available, never an unverified fallback. Expect artifact
   `supernova-0156-native-arm64-v8a`: `Supernova_0.156_native_arm64-v8a.tar.gz`,
   `native-stack.json`, `native-supernova156.lock.json`, `archive.json`,
   `SHA256SUMS`, and `native156-build.log`. The tarball contains rebuilt
   avos/JNI/nativehelper/FileCore DSOs and verified unchanged FFmpeg/codec DSOs,
   with the complete library SHA-256 inventory. Review run head SHA and receipts;
   output hashes/run ID in the new lock deliberately say `pending-first-build`.
   Those placeholders are never accepted as package hashes.
3. App build: Claude owns the changes below to existing protected build files.
   Use the reviewed native run's exact head SHA and download only its
   `supernova-0156-native-arm64-v8a` artifact (pin run ID, not latest successful).
   Check Actions provenance, then verify `SHA256SUMS`, `archive.json`, the tarball,
   native source/patch tree, and each library against `native-stack.json`.
   Do not fill any hash from inference or reuse the 0.146 wrapper hashes.

   Existing files Claude must update together:
   - `.github/build/nova-ci.xml` and `prepare-supernova146-workspace.py`: replace
     avos `2cf21c4` with `5cf3ae9c1f70189d4180a62722150fa76b405a0f`.
   - `.github/build/supernova146-dependency-patches.json` and
     `prepare-supernova146-dependencies.py`: retire **all five** old avos
     operations listed in `native-supernova156.lock.json.retired_avos_operations`;
     apply **only** the five `avos-supernova156-*.patch` entries in lock order
     at the new tip. Check upstream tree `4feb35f62e2e355070ba5e82896650f7e367b413`
     and patched tree `50b756e3f96db29d9f0914be268fd066ef7c5b21`. Preserve
     non-avos operations, Foundation identity preparation, Jetty build and test
     fixtures. The old helper rejects a changed pin; do not run it unchanged
     and then layer the new avos patches over its historical stack.
   - In the existing app/conformance workflow (currently
     `supernova-0154-conformance.yml`, or its Claude-owned successor), replace
     the old run-40843685431 download/package steps with the new run artifact
     and `package-supernova156-native.py` below. Do not invoke the old native
     packager or another ndkBuild afterward; it would overwrite the new DSOs.
     Keep `-Parm64only` and `-x :FileCoreLibrary:ndkBuild -x :MediaLib:ndkBuild`.
     Both module `libs/arm64-v8a` directories must be empty before installation.
   - `.github/build/verify-supernova146-native-apk.py`, existing conformance
     recorder/scope/source-tree checks and signing QA verifier: teach them the
     new arm64 `packaging-inputs.json.engine` receipt, the new avos/JNI hashes,
     app/test trees, version 0.155 and untouched third-party provenance. Do not
     skip their checks or use the old four-ABI receipt format for the new output.
   - `build.gradle:232–234` reads the Foundation version from the hands-off
     `assets/foundation/release-history.json`. Claude must make the resulting
     **versionName `0.155`, versionCode `155`**, application ID `app.supernova.player`
     under `-PfoundationVerify` / `-PfoundationRelease`; current branch is still
     `0.154` / `154`. No Codex version/Gradle/identity edit was made. Preserve
     the existing release history and verify both APK manifest values. The
     `0156` filenames are an engine recipe label, not APK version `0.156`.

   Run from the app workspace using the reviewed first-build values (replace
   the uppercase placeholders with the actual run receipts; they are required):

   ```sh
   python3 nova/Video/.github/build/package-supernova156-native.py \
     --root nova \
     --archive native-artifacts/Supernova_0.156_native_arm64-v8a.tar.gz \
     --archive-sha256 REVIEWED_TARBALL_SHA256 \
     --source-commit REVIEWED_NATIVE_WORKFLOW_HEAD_SHA \
     --evidence native-evidence/packaging-inputs.json
   ```

   This copies the verified native libraries into `MediaLib/libs/arm64-v8a`
   and `FileCoreLibrary/libs/arm64-v8a`, the same module directories Gradle
   already packages. Java/app patches are now real source commits; **do not
   apply their preserved draft patch files again**. New native exports and Java
   must ship together. The three unchanged draft files/manifest remain a record.
4. After app build, run full app/MediaLib/FileCore unit tests and lint. New app
   coverage is 30 tests: four ranking JUnit tests passed offline; 26 Robolectric
   tests (3 HDR producer, 7 HDR/candidates, 8 padding, 8 overflow) await Claude.
   Verify native metadata/chapter JNI parcels, padding export, API23 load,
   ELF dependencies/16 KiB alignment and source/library SHA matching in the APK.
   Run conformance only after the tests pass; preserve logs, runtime/SBOM/legal
   evidence, APK and receipt. Then owner/Claude run
   `findings/engine-0156-shield-tests.md` including actual SRT/ASS/PGS Fit width,
   dialogue versus positioned events, HDR/DV, resume/seek and display reconnect.
   Signing and delivery remain Claude's after acceptance.

ENGINE_BUILD items 1–4 complete on this branch:

| Item | Commit | Validation / limit |
|---|---|---|
| 1 native workflow/lock/packager | `ecab7477d` | 10 new offline tests + 9 archive tests pass; actual five-patch apply/tree and ABI/HLS/buffer probes + 13 upstream host tests pass. No NDK/CI run. |
| 2a `5c721514` HDR producer | `07b4afbb7` | Exact draft 01, 3 Robolectric tests; syntax parsed, Android tests unrun. |
| 2b `9ec1bdc2` decoder/HDR | `80f9504d4` | Exact draft 02; 4 Java-17 JUnit tests pass, 7 Robolectric tests unrun. |
| 2c `73df6e4d` padding | `2c263b591` | Exact draft 03; 8 Robolectric tests unrun. Legacy spacing/zero preserved; no Settings change. |
| 3 `8bbfb666` canvas | `5f26cf861` | Exact upstream overflow/sizing block, 8 new Robolectric tests unrun; syntax parsed. |
| 4 build hand-off | `86136fe9b`, current report | Archive → native → app → tests → conformance. No protected wiring edited. |

**Horizontal overflow remains open**: upstream `95f233f9` clips the canvas
width and adds negative-x video-box reporting (including HDMI and Auto combined
with vertical letterbox). It is outside this brief's one permitted layout port.
It can affect landscape zoom/Fit too when `dcw` exceeds visible width, not just
portrait. Existing behavior is retained; Claude must evaluate supported TV cases
and obtain any needed authorization before porting it. Do not claim vertical
coverage proves horizontal behavior.

No workflow, NDK or Gradle build has run here. Exact continuation:
**ENGINE_BUILD item 5 → NEXT item 3, DP-10**; then NEXT item 4 onward.

DP-10 continuation checkpoint: re-read `UpdateNextTask.java:342–390` and
`:400–443`. Its code is unchanged in this run. The prepared FileCore wrapper
operations in the new native lock apply cleanly at `c4b760c`; the earlier
`filecore-supernova155-scan.patch` also applies after them. That draft supplies
`webdav.ScanIoScope`, which owns HTTP calls/deadline/worker interruption, but
does not establish a generic owned listing contract across the factory's
transports. Next add **a new** `.github/build/filecore-supernova155-*.patch`
after that scan patch, supplying the public owned listing/cancellation hook.
Do not edit the existing patch or reach into private/shared clients from Java
app code. Register ownership before a blocking connect/list can start; fence
cancel-before-register and close/deadline races. Pair it with task-local
cancellation and cooperative filter/sort/traversal checkpoints, preserving the
same candidate order and repeat/binge behavior (DP-09 is Claude's).

Complete in one DP-10 commit with tests: real silent/trickling WebDAV listings,
repeated replace/cancel (including `cancel(false)`), actual worker termination,
no late callback after main-queue drain, cancellation during traversal and an
unaffected concurrent shared playback call. Audit FTP/SFTP/SMB/UPnP connection
ownership before claiming the generic contract. No DP-10 code fix or new
FileCore cancellation patch is delivered by this run. NEXT 4 onward is pending.

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

Earlier reports in `docs/codex/reports/` retain preparation details and earlier
MORE/NEXT findings. `reports/2026-11-04-0155-engine.md` describes the earlier
unapplied drafts; `reports/2026-11-04-0155-engine-build.md` describes their real
integration and this run's exact continuation. The release section above is the
current build authority. Existing preparation reports/patches were preserved.
