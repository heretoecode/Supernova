# Supernova 0.135 development delivery

Branch: `codex/supernova-0.135`. Latest application checkpoint: `85f1822c50f49bf8dd3a8bfcb23b9f495058b07b`. The application checkpoint has passed available automated local and full native conformance. Signed QA run 38043018820 at cfc2ca23 passed with the existing 0.134 certificate; physical acceptance and installed upgrade remain pending. No main merge or final release is authorised or performed.

## Implemented scope

- Shared three-panel Settings, storage browsing and Library Health; common geometry/type/focus; animated Settings expansion and accessible parent rail. One browser serves compact Home, full Network & Files and appropriate TV browse/source/directory routes. System grant pickers and provider account-identity contracts remain documented exceptions.
- Staged recursive library roots/exclusions, complete breadcrumb carousel and persisted controls, protected exit/atomic Apply & Scan. Existing scanner retained. Offline/missing-media reconciliation preserves IDs, metadata and viewing state, including legacy Android volume paths and mapped network IDs.
- Home welcome/import/artwork/row-membership/focus refinements; symmetric Movies/TV typed Continue Watching and boxed controls; local indexed Search; compact Details/Cast/Crew and existing matching/provider paths.
- Four-action HUD, Restart, membership-only dismissal, viewed-coverage threshold, safe five-second Up Next/segment prompts, actual technical badges and persisted compatible/manual versions. Subtitle/audio controls retained.
- One custom page with five-step protected draft/editor, reliable indexed filters and independent controls/columns. TV watched state uses completed-series counts.
- Nonsecret format 1.0 backup with embedded guide, privacy warning, staged confirmation/revalidation/journal rollback and reauthentication. Bounded redacted diagnostics, local legal text/notes, coloured provider catalogue and locale language marks.

Authority reconciliation: [AUTHORITY_RESOLUTION.md](AUTHORITY_RESOLUTION.md). Individual scope, implementation files, validation and acceptance status: [REQUIREMENTS.md](REQUIREMENTS.md) and [requirements.json](requirements.json). Development release notes: [RELEASE_NOTES.md](RELEASE_NOTES.md). Historical and failed intermediate checks are retained in [CHECKPOINTS.md](CHECKPOINTS.md).

## Commits

| Commit | Checkpoint |
|---|---|
| 73c9f393 | Initial authority/baseline/browser audit and register |
| 1039278b | Main approved implementation and dependency patches; first full native unsigned conformance |
| 3d88a6fa | Metadata/backup/focus/provider hardening and fail-closed signed QA workflow |
| ae34c2ed | Legacy-volume retention, network stable-ID reconciliation and exact top-nav dialog focus restoration |
| 4214232a | Boxed controls, divider/spacer navigation, Settings transitions and typed Movies/TV Continue Watching |
| 1f7ad9bf | Complete-series watched filters for custom TV pages |
| 85f1822c | Compact context/target scrolling and native first-import state with exact focus retention |

## Validation

Application checkpoint 85f1822c passed 491 Java tests across 128 classes (zero failures/errors/skips), release lint zero fatal/errors with 1,465 warnings, and 49 Python tests. Local Gradle elapsed 11m 54s and omitted only the two dependency NDK tasks; this is not an APK/native build. Final [native run 38034702010](https://github.com/heretoecode/Supernova/actions/runs/38034702010) passed complete native Android compilation, four ABI registries/tempo state and 64 decode comparisons, 491 Java cases, 49 Python cases, lint and unsigned universal APK conformance. Gradle elapsed 13m 9s. The APK contains 88 native libraries; every native payload is checked against compiled input, including 24 rebuilt FFmpeg payloads. CI-observed unsigned APK SHA-256: `ebf5726dff2c717fd31b8164394f3b2f7feeeda98a897362c887a0208e3b9d4a`. No fresh local binary verification is claimed. [Public evidence artifact](https://github.com/heretoecode/Supernova/actions/runs/38034702010/artifacts/11663862336) contains JSON/XML/UI/lint evidence, not an APK. Evidence is recorded in [TEST_RESULTS.json](TEST_RESULTS.json); [QA_READINESS.json](QA_READINESS.json) keeps release readiness false until all acceptance gates pass. Earlier source `1039278b` passed full native/FFmpeg compilation/regression, 469 Java tests, 36 Python tests, release lint and unsigned universal APK identity/runtime/legal/native checks in run [38028207604](https://github.com/heretoecode/Supernova/actions/runs/38028207604). The final candidate has its own successful run above.

## Signed QA APK for Shield testing

[Download signed QA artifact](https://github.com/heretoecode/Supernova/actions/runs/38043018820/artifacts/11667311650) (GitHub ZIP, expires 2026-10-24). Extract `Supernova-0.135-QA.apk`; the other file, `public-qa-evidence.json`, contains the signed APK digest and runner verification evidence. [Successful workflow](https://github.com/heretoecode/Supernova/actions/runs/38043018820) at source `cfc2ca2376fb77f79a85a3a5e45333d38b9689e7`; full native build/regression, 491 Java tests, 49 Python tests and release lint passed. The runner verified the APK signature, exact package/version, branding/legal/native content, ZIP integrity and payload equality with the tested unsigned APK. Corresponding [FFmpeg source artifact](https://github.com/heretoecode/Supernova/actions/runs/38043018820/artifacts/11667721222) is available until 2026-11-09.

Package `app.supernova.player`, version `0.135`, code `135`, certificate SHA-256 `79ed34c52c3e359756ade0634d7bbb6f8e94e42a059020c1220bd8c7092a9f5e` match the recorded signed 0.134 identity with code increased from 134 to 135. This establishes Android package/certificate/version update compatibility against that baseline; the installed Shield certificate and actual upgrade have not been tested. Back up using the existing app's normal backup process, then install as an update. Do not uninstall or clear application data. If using an already configured ADB connection: `adb install -r Supernova-0.135-QA.apk`. Verify retained library, progress and settings using [SHIELD_QA.md](SHIELD_QA.md).

Local artifact download returned `Forbidden` at GitHub's Azure artifact host. No fresh downloaded APK or archive digest verification is claimed; signing verification was performed in CI. The GitHub-reported SHA-256 `8db243c753051e5ba1860a548ac2c1c1caf474834bf7fa7039b1775fcc797ab1` identifies the artifact ZIP, not the APK. [SIGNED_QA_VERIFICATION.json](SIGNED_QA_VERIFICATION.json) records the successful runner steps, digest scope and limits.

## Outstanding gates and limitations

- Signing access is resolved: the administrator added the exact `codex/supernova-0.135` branch rule while retaining `codex/foundation-release`. The prepared `.github/workflows/supernova135-qa.yml` completed successfully using the existing key. No new key or local signing material was created. The original Foundation signing workflow/helpers remain unchanged.
- No ADB-connected Shield or configured emulator is available. Physical D-pad/focus/scroll/animation, playback/subtitle/audio/HDR, representative USB/network storage, permissions/performance and signed 0.134 upgrade require the exact signed QA artifact and device checks in [SHIELD_QA.md](SHIELD_QA.md).
- Credentialed provider coverage/quotas, inherited IntroDB org access, real-file segment alignment and frame-only HDR10+ metadata are unverified. Contract/public-document evidence is in [SOURCE_AUDIT.md](SOURCE_AUDIT.md).
- No reproducible evidence was supplied for the unspecified incomplete-import or diagnostic-overlay-flash reports. No causal fix is invented; existing instrumentation and functionality are retained.
- The fresh historical artifact download was blocked by the execution environment host policy. The preserved signed 0.134 repository record is the baseline evidence; no newly downloaded historical binary digest is claimed.
- Main merge and final release remain subject to the user's approval.
