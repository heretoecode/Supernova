# Supernova Preview 4.1.5 — stability return handover

## Baseline and continuation point

Repository: heretoecode/aos-Video. Branch: codex/apk-build-fixes.
Starting source: aa4337d7a3d9e51161e406f232a6531f25e70c36 (Preview 4.1.4).
APK candidate source: df1ed0185742509917bb180c2c6251220c9dc4a0.
The starting tree was clean. No older handover was merged. The changed files are listed in changed-files.txt. Dependency revisions remain pinned; FileCore and MediaLib corrections are reviewable patches applied after the existing 4.1.4 patches. No additional upstream Nova commit was ported. No UI redesign or deferred feature was introduced.

## Physical startup crash — evidence and limits

The complete supplied diagnostic archive and both audit reports were read before editing. Release R8 mapping identifies VideoCursorMapper.bind:7 as the ARCHOS_MEDIA_SCRAPER_TYPE integer read. At approximately 02:35:04.720 CursorWindow.nativeGetLong/getInt throws IllegalStateException; PreviewLibraryLoader catches, logs and rethrows it. The asynchronous loader/FutureTask then propagates an uncaught failure approximately seven milliseconds later.

Android 11 native source distinguishes this exception from ordinary null/numeric values: inaccessible row/column slots or an unknown native type can throw IllegalStateException; NULL returns zero and BLOB conversion throws SQLiteException. The supplied logs lack the native exception message, field type, row/window coordinates and raw database. Consequently the precise underlying window/data fault, deterministic item, migration involvement, reboot timing and duplicate-record involvement remain unproven. The existing custom cursor's documented refill issue is a plausible mechanism, not an established cause.

PreviewMappingCursor records redacted row/column/type/window coordinates and safe record ID. PreviewLibraryLoader isolates mapping failures with bounded rejection, avoids journey/cache writes after partial loads, retains a previous/empty snapshot after query failures and displays a warning. SQLite errors abort the load and are reported rather than silently treated as corrupt individual records; cancellation remains cancellation. MainFragment handles the Preview error result even when the cursor is null. No database deletion, repair or corruption dismissal was added.

Regression coverage uses injected field-read failure, real SQLite mixed healthy/BLOB records, null/boundary values, query failure and startup/recreation. The BLOB test is defensive hardening, not reproduction of the captured native exception. DIA-001 is MITIGATED, not a proven underlying-data fix. PHYSICAL SHIELD QA REQUIRED with the existing populated database after reboot; export diagnostics immediately if a warning appears.

## Other diagnostic crash

Two scene-transition exceptions in the archive are from the same 4.1.4 build. Preview cards can lack the legacy image view expected by ListingFragment.openDetailsActivity. The transition helper now checks activity state and attached source view; Details opens without shared-element animation when unavailable. Null/detached-source tests cover the boundary. Physical grid/list Details and Back remain PHYSICAL SHIELD QA REQUIRED.

## Corrections and disposition

See AUDIT_DISPOSITION.md for every SNA identifier, severity, disposition, change, validation and Shield requirement. There are 13 FIXED, 7 MITIGATED and 3 DEFERRED WITH REASON findings. These are implementation dispositions, not physical-device acceptance.

The principal changes preserve resume through automatic physical-version selection; distinguish explicit/manual launch intent; make Up Next select a logical next episode instead of another encode; bind the internal media bridge to loopback; share persisted SFTP host-key trust across both engines; add durable restore intent/recovery and process locks; and stop provider skip metadata synthesising completion. Smaller corrections cover preference consumption, source identity, focus movement, failed metadata/artwork recovery, startup artwork gating and invalid ports.

## Known limitations and regression risks

- Historical long WebDAV premature completion remains unresolved. No causal connection with provider skip completion was established, and no fix for that historical failure is claimed.
- SFTP is trust on first use. A first-connection interceptor remains possible. Changed keys are rejected before authentication; legitimate key/algorithm changes require independent verification and an explicit per-server reset.
- Restore recovery is not a filesystem-wide atomic transaction. Power loss, full storage and concurrent non-media writers remain unproven. Failed recovery deliberately prevents providers opening mixed state. Test interruptions only on disposable data.
- Backups remain unencrypted and contain recoverable credentials/tokens. A clear warning is a mitigation, not encryption.
- Explicit Unwatched reconciliation, physical-ID custom-row membership and large-library Search performance remain limited/deferred as described in the disposition table.
- Real player starting position, network-server compatibility, decoder/A/V sync, passthrough/HDR, hardware focus and long-running playback require physical Shield testing.

## Validation and APK

Final CI evidence and APK identity are recorded in BUILD_RESULTS.md. Earlier candidate failures and corrections are retained there; passing compilation alone does not prove the physical crash fixed. No Nvidia Shield validation was performed by this agent.

## Physical acceptance

Follow SHIELD_QA.md in order, starting with a same-package/same-signature upgrade and reboot/cold startup using existing app data. Do not clear the database to make the startup test pass. Every hardware-only or unexecuted check is PHYSICAL SHIELD QA REQUIRED.

## Candidate-only startup regression caught before delivery

The first emulator run timed out in `am start -W`. Inspection found SftpHostTrust.initialise, called during attachBaseContext, dereferenced getApplicationContext before LoadedApk assigned its application. Android 9 LoadedApk/ContextImpl source confirms the lifecycle ordering. The correction uses the supplied base context only to obtain its files directory; no Activity/context reference is retained. A null-application-context regression test was added. The smoke harness now bounds launch to 60 seconds and captures logcat, activity state and a screenshot on failure. The initial timed-out run lacked those diagnostics, so its exact observed stall is not attributed conclusively to this bug.
