# 0.156 screen data — all items 1–7 ready (5 Nov 2026)

All seven items ready through `a041eb382` on `codex/0155-code-2026-11-04`, after merging Claude `0e6943603` first.
Each item was committed, documented and pushed separately below. Final checks: **75 isolated component tests pass**,
54 changed Java files pass Java 17/import/120-column checks, layout checks pass, and both complete dependency patch
chains match their prepared sources and still accept the CI fixtures. Full Android/provider/remote-write conformance
and Shield checks remain Claude’s; no Gradle/workflow run was performed.

Register **seven new patch operations**, leaving the existing operations intact: MediaLib scan → history → schedule →
missing → NFO → NFO cancellation follow-up, plus FileCore connections after its existing operations. Put them before
the respective CI fixture patches. Exact paths, order and SHA-256 entries are at the end of
[`reports/2026-11-05-0156-screen-data.md`](reports/2026-11-05-0156-screen-data.md).
API calls are in [`findings/0156-ui-hooks.md`](findings/0156-ui-hooks.md). No screens/resources were edited; no PR,
merge into Claude’s branch, rebase, force push, workflow dispatch or fork push. GitHub cleanup is finished.

# 0.156 screen data — item 7 ready (5 Nov 2026)

Item 7 ready at `3b8b3daff` on `codex/0155-code-2026-11-04`. All seven screen-data items are ready. 74 component regressions pass; register the six new dependency patches before full conformance.
See `reports/2026-11-05-0156-screen-data.md` and `findings/0156-ui-hooks.md`.

# 0.156 screen data — item 6 ready (5 Nov 2026)

Item 6 ready at `a45276960` on `codex/0155-code-2026-11-04`. Ten inventory/search/SQLite regressions pass (57 component regressions across items 1–6). Item 7 continues.
See `reports/2026-11-05-0156-screen-data.md` and `findings/0156-ui-hooks.md`.

# 0.156 screen data — item 5 ready (5 Nov 2026)

Item 5 ready at `70a38e719` on `codex/0155-code-2026-11-04`. Nine real connection-probe tests pass (47 component regressions across items 1–5). Items 6–7 continue.
See `reports/2026-11-05-0156-screen-data.md` and `findings/0156-ui-hooks.md`.

# 0.156 screen data — item 4 ready (5 Nov 2026)

Item 4 ready at `9f46af2c1` on `codex/0155-code-2026-11-04`. 38 component regressions pass. Items 5–7 continue; no additional patch for item 4.
See `reports/2026-11-05-0156-screen-data.md` and `findings/0156-ui-hooks.md`.

# 0.156 screen data — item 3 ready (5 Nov 2026)

Item 3 ready at `ff80eaac2` on `codex/0155-code-2026-11-04`. 27 component regressions pass. Items 4–7 continue; register the schedule patch after history.
See `reports/2026-11-05-0156-screen-data.md` and `findings/0156-ui-hooks.md`.

# 0.156 screen data — item 2 ready (5 Nov 2026)

Item 2 ready at `d59573789` on `codex/0155-code-2026-11-04`. 24 component regressions pass. Items 3–7 continue; register the new history patch after the scan patch.
See `reports/2026-11-05-0156-screen-data.md` and `findings/0156-ui-hooks.md`.

# 0.156 screen data — item 1 ready (5 Nov 2026)

Item 1 ready at `6dac8747d` on `codex/0155-code-2026-11-04`, after the authorised fast-forward merge of
Claude’s `0e6943603`. Items 2–7 continue. Register **new** `medialib-supernova156-screen-scan.patch` after the
current registered dependency operations and before the CI fixtures. See
[`screen-data report`](reports/2026-11-05-0156-screen-data.md) and `findings/0156-ui-hooks.md`.
Twenty isolated component regressions pass; full worker/transport/Android conformance remains Claude’s.
GitHub cleanup is finished. No screens, resources, workflow dispatch or fork pushes.

# 0.156 added conformance failures ready — 2026-11-05

Added test fixes ready at `8d9a91e0e` on `codex/0155-code-2026-11-04`, after the authorised fast-forward merge of
Claude’s `be1291e11`. The second section of `BRIEF_0156_TEST_FIXES.md` is addressed; every failure is classified in
[`reports/2026-11-05-0156-test-fixes-followup.md`](reports/2026-11-05-0156-test-fixes-followup.md).
Ten isolated component tests pass, including both backup guard regressions and all three cache tests.
All seven changed Java files pass parser/import/line checks; layout checks pass. Full service/resource/large-export
conformance remains Claude’s. **No new dependency patch registration or UI wiring is needed.**
The 61,050-row mixed-source backup fixture and all prior assertions remain, with stronger identity checks.
GitHub cleanup remains completed.

# 0.156 test fixes ready — 2026-11-05

Test fixes ready at `b91dd25c5` on `codex/0155-code-2026-11-04`, after the authorised fast-forward merge of
Claude’s `60ad9ef1a`. All ten failures are classified and repaired; see
[`reports/2026-11-05-0156-test-fixes.md`](reports/2026-11-05-0156-test-fixes.md).
Fifty isolated regressions pass, plus Java 17/layout/ordered patch checks. Full conformance remains Claude’s.
Register the **two new** `filecore-supernova156-test-fixes.patch` and `medialib-supernova156-test-fixes.patch`
operations after the existing dependency stack; exact entries and SHA-256 values are in the report.
No public API/UI changes. GitHub cleanup is finished; older parked references below are historical.

# 0.156 late Part B17a ready — 2026-11-05

B17a ready at `afb3a755d`, after D hand-back `edc5a4281`, on
`codex/0155-code-2026-11-04`. Original Parts A–D and the newly added network
reconnection item are handed back. Read `reports/2026-11-05-0156-code-part-B-addendum.md`
and `findings/network-stream-recovery.md`.

Six new host tests and all 19 D regressions pass (25 total). Parser/API and ordered
patch checks pass; full Android/Robolectric/native/Shield checks remain Claude's.
Register `medialib-supernova156-network-recovery.patch` after the four A/C/D patches.
Paused proxy reads share a 64 KiB allowance; failed generations are abandoned;
replacement transport/decoder seeks before playing. Direct native readers retire
on pause; their unreported internal retries still require native/Shield confirmation.
No new UI calls, protected screen/build edits or fork pushes. PortableCustomPage
remains untouched. GitHub cleanup remains parked.

# 0.156 Part D incremental hand-back — 2026-11-05

Part D ready at `207242079` (engine/recorder `9757306f1`), after Part C `490347566`,
on `codex/0155-code-2026-11-04`. Original Parts A–D are handed back in order.
The freshly fetched Claude brief `c37bd36d2` adds B17a (network reconnect after
long pause/read failure). Codex is continuing with that late Part B addendum;
it is not yet implemented at this checkpoint. PortableCustomPage remains untouched.
Read `reports/2026-11-05-0156-code-part-D.md`, `findings/seek-thumbnails.md` and
`findings/diagnostics-hooks.md`; UI calls are appended to `findings/0156-ui-hooks.md`.
Nineteen host tests pass; Java 17 parser/API and ordered patch checks pass.
Android/Robolectric/native/Shield validation remains Claude's.

Register the new MediaLib patches in order: A scan reliability, C NFO progress,
D keyframe retriever, then D metadata diagnostics. App code needs those APIs;
existing dependency/build files were deliberately not edited. Shield must confirm
protocol/codec coverage, cancellation and playback interference. Native counters,
FileCore per-minute/open/status hooks and protected screen hooks are documented
explicitly; unknown values are not reported as measured zeroes.
GitHub cleanup remains parked. No PR/merge/rebase/force push/workflow was performed.

# 0.156 Part C incremental hand-back — 2026-11-05

Part C ready at `841c47328`, after Part B `ebd46f688`, on `codex/0155-code-2026-11-04`.
Read `reports/2026-11-05-0156-code-part-C.md` and Part C in `findings/0156-ui-hooks.md`.
Seven host JUnit tests pass; Android tests remain Claude's. Apply the new
`medialib-supernova156-nfo-progress.patch` after Part A's MediaLib patch.
Codex continues with Part D thumbnails/diagnostics. GitHub cleanup remains parked.

# 0.156 Part B incremental hand-back — 2026-11-05

Part B ready at `87c0fb23d`, after Part A `2d46bf90a`, on `codex/0155-code-2026-11-04`.
Read `reports/2026-11-05-0156-code-part-B.md` and the Part B section of
`findings/0156-ui-hooks.md`. Eight host JUnit tests pass; Android/Shield validation
remains Claude's. New timeline, episode, picture, subtitle preview, sleep/card,
series-track and bar-key APIs are documented there. Codex continues with Part C.
GitHub cleanup remains parked.

# 0.156 incremental hand-back — 2026-11-05

Part A ready at `9569fe99d` (includes scan patch `dfde2850e`), on `codex/0155-code-2026-11-04`.
Read `reports/2026-11-05-0156-code-part-A.md` and `findings/0156-ui-hooks.md` before building.
Apply the locked MediaLib stack, prior 0155 scan patch (and its FileCore scan prerequisite),
then new `medialib-supernova156-scan-reliability.patch`; no protected build wiring changed.
Two host JUnit tests pass; Android/Robolectric suite and Shield checks remain Claude's.
Historical root `4ae5c08c…` still needs a fresh failure trace to confirm its actual cause.
Codex continues with Part B without waiting. GitHub cleanup remains parked.

# Handover to Claude — 2026-11-04

## Latest backup brief and DP-10 checkpoint — 2026-11-04

Read `reports/2026-11-04-0155-backup.md` for this run. It supersedes only the
older DP-10 continuation below; the engine build/archive instructions remain.
Pulled Codex `391326f1d`; fetched Claude `51c20fab2` and read HANDS_OFF first.

- **BACKUP item 1 complete**, analysis-only commit `0fd0b8d6e`:
  `findings/backup-failure.md` enumerates the export/message boundary, category
  rejection sites, writer/closed-reader/destination/import distinctions, all
  limits, timeout/cancellation/storage paths, diagnostic coverage and ranked
  causes/tests. Leading candidate: MATCHES' **4 MiB uncompressed category cap**;
  illustrative 61,050 valid movie matches need 8,413,815 bytes. This is not proof
  of the owner's actual matched-file count/cause. The worker swallows the
  exception without recording it; no Diagnostic Report entry guarantees the
  cause. Claude owns any backup fix; no backup app code changed.
- **NEXT item 3 still open**, prerequisite draft commit `cd1a1046f`:
  new `.github/build/filecore-supernova155-ftp-listing-cancellation.patch`
  follows all eight locked FileCore operations, scan draft, then lifetime/DAV
  draft. It owns FTP/FTPS sockets before connect, survives PROT factory
  replacement, checkpoints retries/list traversal and avoids cancelled logout.
  No app caller/build integration or selection change. Never apply these
  partial transport drafts independently as the completed DP-10 fix.
- Fresh pinned eleven-patch apply/check and final-tree comparison passed.
  **32 standalone JUnit tests passed** (20 new real FTP/FTPS socket tests +
  12 existing lifetime/race tests), Java 17 on OpenJDK 21. Six actual
  factory/Session Robolectric tests are supplied but unrun; all six changed or
  added Java files parse. No Gradle, device build, workflow or NDK run here.

**Exact next place:** `findings/dp10-listing-ownership.md`, heading
“Backup-brief continuation”, numbered checkpoint 1. Validate the six actual
FTP factory/Session tests; finish pre-socket DNS/credential loading, connect and
retry races and Android 11 TLS-close coverage. Then isolate JSch/SSHJ/SMBJ/JCIFS
listing resources; create the MediaLib UPnP owned-Browse companion; pair the
completed hooks with an allowed new UpdateNextTask app draft and all worker/
main-queue/unchanged-selection acceptance tests. Commit the complete DP-10 fix
atomically. **NEXT item 4 (security/privacy audit) and onward remain pending**
until item 3 is complete. Do not mark DP-10 fixed based on socket tests alone.

FTP companion SHA-256:
`d0776e100ac53107875a7b37742832d5f82d9fcfffd727f278b2cdd9d529df48`.
Existing lifetime/DAV and scan draft hashes are unchanged. This run touched
only Codex docs and that one permitted new patch. The three-dot comparison
against fetched Claude has no hands-off/Gradle change; all `.github/` entries
are additions with allowed names, including prior work. No existing `.github/`
file changed in this run. Push only `codex/0155-code-2026-11-04`; no PR, merge,
rebase, force push or workflow dispatch.


## Unit failures from run 41850284587 — corrected fixtures

Commit `e27ac4bf5` repairs the three failures reported at `9fd8bc94c`. It extends
`SubtitleManagerDefaultsTest.styleKeysCoverEveryStylePreference` to expect 14
and explicitly include `KEY_PADDING`, retaining all 13 previous key checks.
`PlaybackHdrCapabilitiesTest.capabilitiesAreReadAgainForEachNativeQuery` now
returns two real `Display.HdrCapabilities` objects (empty, then all four HDR
formats). `excludesEncoderSoftwareAliasSecureAndTunneledAndKeepsVendorOrder`
uses Robolectric 4.17 `CodecCapabilitiesBuilder` for real AVC capabilities and
required secure/tunneled flags. Encoder/software/alias exclusions, both vendor
positions and end-of-candidates checks remain. Production behavior is unchanged.

Both tests pass Java syntax parsing; codec builder signatures checked against
the published 4.17 sources. No Gradle or full Robolectric run here, per MORE
rule 4. Claude: rerun these two test classes, then the full unit suite. DP-10
continues separately below; these fixture repairs do not claim to fix it.

## Engine release — Claude's steps

Branch: `codex/0155-code-2026-11-04`. Follow `BRIEF_0155_ENGINE_BUILD.md`
from fetched Claude `a5c5b7b3a408ffa3adaa24f5cfbfe2bd1ce4e460`.
Archive update follows `BRIEF_0155_ARCHIVE_BRANCH.md` fetched from Claude
`65457a4e36b30dc0b2bc84e17d427b5b8d708294`, including the two fixes from
`11c60bbb6` / `f4c22771d`. This section supersedes the historical preparation
status below.

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

ARCHIVE_BRANCH items 1–4 are implemented: `ec6507f81` (branch writer),
`76c402a19` (branch reader/default), `e3235e233` (owner fallback), `d4735eb89`
(hand-off), `8f6599d0e` (strict root tree/history checks, complete offline local
preparation and Actions fallback tests). Both modified workflows and script
recipes have measured digests in the engine lock. Final validation: **16 archive
+ 14 native-stack offline tests pass**. No archive branch has been pushed and
no workflow, NDK or Gradle build has run here; Claude must perform the live job.

The following is the **earlier DP-10 checkpoint**, superseded by the latest
backup-brief section above. There is
now a concrete **unintegrated draft** at
`.github/build/filecore-supernova155-listing-cancellation.patch`, after the eight
prepared FileCore operations and `filecore-supernova155-scan.patch` at `c4b760c`.
It adds a task-owned `ListingCancellation` lifetime allocated before submission,
an owner-thread scope entered before factory/setup, whole-operation deadline,
owned abort registration and cooperative checkpoints. DAV calls, including
redirect probes and late callStart, join that lifetime without cancelling the
base client's other calls. No app API reference or protected build wiring was
changed. **Do not install this draft alone or mark DP-10 fixed.**

Fresh-checkout apply/check and Java-17 standalone compilation passed. **25
standalone JUnit tests pass** against pinned OkHttp 5.5.1: 12 new lifecycle/race,
8 new real-socket, 5 existing scanner tests. Actual runnable/executor exit is
checked, not a cancelled Future. Four additional actual WebdavRawLister/factory
Robolectric silent/trickle × explicit/deadline tests are supplied but unrun.
The new draft's SHA-256 is
`f60e493b5deeaa4411bfcacc5781e0ccd32c0da77d3880c3b86e77c29910f5da`.
See `findings/dp10-listing-ownership.md` for the implementation checkpoint.

Next: complete FTP/FTPS pre-connect and data-socket ownership, isolated SFTP/
SSHJ/SMBJ/JCIFS listing resources and retry/setup paths, and a **new MediaLib
UPnP companion patch** retaining/cancelling the submitted Browse handle. Shared
sessions/pools must survive. These transport hooks are still missing; the public
lifetime and DAV tests establish only the first part of the earlier audit.
Then pair the hook with UpdateNextTask task-local cancellation (including
`cancel(false)`), cooperative filtering/comparator/traversal checkpoints and
main-queue fencing. Preserve exact file choice/order and repeat/binge behavior;
DP-09 is Claude's. Deliver the completed app/transport fix and acceptance tests
in one **DP-10 fix** commit, separately from this explicitly labelled draft.
NEXT item 4 onward remains pending. No claim of fixed obsolete app workers,
callback fencing or non-DAV termination is made by the draft's socket tests.

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
