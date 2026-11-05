# Preview 4.1.5 — build and validation evidence

APK source: df1ed0185742509917bb180c2c6251220c9dc4a0.
CI: https://github.com/heretoecode/aos-Video/actions/runs/35875536264
Job: 107230337719. Validation mode: preview. Java 17; Android emulator API 28, x86_64, 1920×1080.

## Executed commands

The complete exact workflow and test selectors are retained in Evidence/build-preview-apk.yml. Principal commands:

- `git apply --check` and `git apply` for the existing and new dependency patches.
- `./gradlew assembleNoamazonDebug -Puniversal -PmarkPreview -x lintNoamazonDebug --build-cache --no-daemon`
- `./gradlew testNoamazonDebugUnitTest` with the workflow's selected Preview, startup, diagnostics, playback policy, streaming preference, backup/restore and bridge test classes; `-Puniversal -PmarkPreview --build-cache --no-daemon`.
- `./gradlew :FileCoreLibrary:testDebugUnitTest --tests 'com.archos.filecorelibrary.webdav.WebdavSecurityAndRangeTest' -Puniversal -PmarkPreview --build-cache --no-daemon`
- `bash .github/build/check-preview-startup.sh <debug-apk> preview`
- `python3 .github/build/check-preview-playback.py`
- `./gradlew assembleNoamazonRelease -PmarkSigning -Puniversal -PmarkPreview -x lintNoamazonRelease --no-daemon --stacktrace`: PASS.
- `bash .github/build/check-preview-startup.sh <release-apk> release`: PASS.
- `apksigner verify --verbose --print-certs`, pinned-certificate comparison, native-library listing and `sha256sum`: PASS.

Local checks: all 492 tracked XML files parsed; Python smoke-script AST and Bash syntax passed; source diff whitespace check passed excluding unified dependency patch context. Patch-context whitespace originates in legacy tab-indented source and patch application was checked in CI. Local Gradle bootstrap was attempted but blocked by services.gradle.org network access; local Android SDK/emulator unavailable. Compilation and Android tests therefore ran in CI.

## Scope of passing checks

Debug compile/package; selected app tests; 17 WebDAV security/range tests; empty-library full-process cold launch with Preview enabled, reinstall with saved Preview preference, force-stop restart and warm relaunch; Home, Movies, TV Shows, Settings categories, Network & Files/Internal Storage and no-match Search routes; synthetic two-minute local clip launch, HUD end-clock and technical information route with Back and live process checks.

Tests additionally exercise resume/version launch policy, logical Up Next, database source identity, settings replacement, durable restore recovery and locking, loopback socket binding, malformed-row isolation and previous-snapshot retention. These are not full physical engine/network/restore proofs. Details transition and page rendering are tested at host/Robolectric boundaries, not a populated real-device movie/episode workflow.

NOT TESTED — USER QA REQUIRED / PHYSICAL SHIELD QA REQUIRED: populated Shield database cold start after reboot; exact historical cursor-window reproduction; real-engine resume across versions; full Details/playback/Home return stack and focus; long WebDAV playback/false EOF; SFTP real-server key changes and both backend algorithms; actual restore process-kill/power failure/storage-full scenarios; full external-player and protocol bridge matrix; decoder recovery, A/V synchronisation, HDR and passthrough. Broad/full CI mode and lint were not run; no claim of exhaustive coverage.

## Earlier candidate results and corrections

- 35840906597: compile error in browser focus helper (View versus ViewGroup), corrected.
- 35841396876: unreachable legacy logging following new invalid-port return, corrected.
- 35871481296: debug package passed; 71/72 tests passed. Existing startup test incorrectly expected navigation as root despite 4.1.4 launch wrapper. Corrected to find and validate navigation/clock/status inside the actual wrapper, including recreation.
- 35872425148: debug package, all 73 app tests and all 17 WebDAV tests passed. Initial emulator launch timed out; release not built. Identified/fixed early SFTP application-context dereference and added a regression test plus bounded failure diagnostics. The missing first-run crash log prevents conclusively assigning the observed stall to that defect.

## Final outcome

**PASS:** 74 selected app tests, zero failures/skips; 17 WebDAV tests, zero failures/skips. Debug and optimised release compilation/package passed. Full-process emulator smoke passed; signed debug-to-release upgrade and subsequent restart passed with Preview still enabled. Release and HUD screenshots were inspected. This is not a 4.1.4-to-4.1.5 populated physical-library migration test.

APK: `org.courville.nova.markpreview-6040081-6.4.63-mark.4.1.5-preview-universal-release.apk`

Version: `6.4.63-mark.4.1.5-preview`; code `6040081`; package `org.courville.nova.markpreview`.
Size: 82449258 bytes.
SHA-256: `59bc8e2606728ce77932cb143c52ab9593c28651cb323d13dda02dd5953e96b7`.

The local APK matches CI’s recorded SHA-256. Downloaded archive CRCs passed. The first diagnostics download was incomplete; a fresh download matched the full artefact size/hash and passed CRC. The complete CI diagnostic archive is included for source manifest, reports, emulator evidence and release R8 mapping.
