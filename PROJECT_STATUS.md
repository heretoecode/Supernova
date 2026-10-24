# Supernova — Project Status

Current delivery: **Supernova 0.142 signed QA**, isolated branch `codex/supernova-0.142-implementation-2026-10-23`. Exact application source `b347707defdbf2ab3ae83d920ba9fa7af3936220`; [verified APK](https://drive.google.com/file/d/1CxWm1pxf5K005ZzIO4RWKzQyPF0YQcu4/view?usp=drivesdk), code 142, SHA-256 `af03d26719b00601ca08da5ad562c1f196ec536c6397ac5b4603c2337d7c8514`. Existing certificate and complete Drive readback verified. [Release report](docs/0.142/RELEASE_REPORT.md), [delivery receipt](docs/0.142/SIGNED_QA_DELIVERY.json), [Shield quickcheck](docs/0.142/QA_QUICKCHECK.md).

Approved six-section Network & Files redesign and five-step Edit Library readability correction are included. Full exact-source Android suite (1,094 cases), tooling (102), native comparisons and lint (zero errors) pass. Physical Shield/provider/storage/focus/upgrade acceptance remains pending. Playback HUD source is unchanged; final redesign requires design approval. PR20 remains draft, main unmerged, 0.142 is not final accepted release. Earlier APKs, release branches and historical records remain preserved.

## Preserved 0.141 delivery evidence

Delivered 0.141: **Supernova 0.141 implemented and software/binary-validated**, isolated branch `codex/supernova-0.141-implementation-2026-10-22`, application source `4f0937f1fe8826f50d2c0e844b0837e8a2360a46`. [Release report](docs/0.141/RELEASE_REPORT.md), [requirements ledger](docs/0.141/IMPLEMENTATION.md), [complete playback migration](docs/0.141/PLAYBACK_MIGRATION.md), [physical Shield checklist](docs/0.141/SHIELD_QA.md). Local and exact-source CI suites: 1,068 cases/226 classes, zero failures/errors/skips; 95 tooling tests; lint zero errors. Native 64 decode comparisons, 24 rebuilt FFmpeg libraries, 88 packaged native hashes, branding/runtime/licence and complete artifact download verification pass. Original-certificate signed QA is **delivered and fully download-verified**: [Supernova_0.141.apk](https://drive.google.com/file/d/1zl46kal8Q0x0gG5KFK_NBtfgBENrj0uC/view?usp=drivesdk), 87,899,088 bytes, SHA-256 `239d0d5273e1b59806d56e96e3667759d49db3a1926817f2123af6ddb9045e51`. Signing run 39939855407 uses frozen source `4f0937f1`. PR #19 remains draft pending physical Shield testing; real Put.io sign-in and signed upgrade remain unverified. Main, 0.140 and historical branches are preserved; no main merge or maintenance programme.

## Preserved0.140 delivery record

> Previous delivery record for the authorised **0.140 implementation branch**. Main and earlier release branches are preserved. The historical 4.1.7 record below remains available for lineage; its former “current work” and exclusions do not override the owner's later 0.140 handover.

## Current authorised development

- Accepted corrective signed 0.139 baseline: `a4841c1dbaac5e1037fb19f9f11882848b435e0b`, independently checked against the complete Drive APK. [Baseline receipt](docs/0.140/BASELINE_0139_BINARY_VERIFICATION.json).
- Implementation branch: `codex/supernova-0.140-implementation-2026-10-16`; [draft PR 18](https://github.com/heretoecode/Supernova/pull/18). No main merge is authorised.
- Exact signed QA APK source: `866f21f11f27dea4846ddd3cac57d98b281eecbb`; package `app.supernova.player`, version **0.140 / Android code 140**; established signing identity retained.
- Signed QA **delivered and verified**: [APK](https://drive.google.com/file/d/1JEYAXNKw4gGPgEx0xUjATDXES1bI158T/view?usp=drivesdk), 87,890,896 bytes, SHA-256 `b6d59e82c03cd99844f49d4647d2f2a985c484c819d92064542eec84f33788f5`. Final exact-source CI passes **1,047 application tests / zero failures, errors or skips; 88 tooling tests; lint zero errors / 1,775 warnings**, native comparisons and established-certificate/payload checks. Full Drive readback and in-place HTML update are verified. Physical acceptance remains open; this is not a public/main release.
- [Release report](docs/0.140/RELEASE_REPORT.md), [test ledger](docs/0.140/TEST_RESULTS.md), [visual evidence](docs/0.140/VISUAL_QA.md), [diagnostic investigation](docs/0.140/DIAGNOSTIC_FINDINGS.md) and [unperformed target-device checklist](docs/0.140/SHIELD_QA.md).
- OMDb production access remains disabled by owner instruction. No authorised account/binding/entitlement is assumed. Real Put.io/device/SAF/provider validation, the original diagnostic ZIP, annotated FD-033 v3 and FD-007 reference assets remain unavailable. The recovered overall FD-033 JPEG is archived exactly.
- Unified Playback Settings, broader Playback HUD redesign and the separate 14-finding HUD audit were excluded from 0.140 and are now authorised for **0.141**.

## Preserved historical 4.1.7 project-status record

The following status described the earlier Preview baseline. Its present-tense labels are historical; the authorised release record above supersedes them for this branch.

## Current accepted baseline

- Product: Supernova, a custom Android TV / NVIDIA Shield video player derived from NOVA Video Player.
- Accepted development baseline: **Preview 4.1.7 Final**.
- Authoritative application/test source commit: `7efb4195639205a6526281491af40c0dab1776b7`.
- Development lineage: `codex/preview-4.1.7`.
- Final APK: `Supernova-4.1.7-FINAL-7efb4195.apk`.
- APK SHA-256: `4070ee3d259b3cf57bb10a26fbc789363cf812e8b81fe7f3101522634705681a`.
- Application ID: `org.courville.nova.markpreview`.
- Version: `6.4.63-mark.4.1.7-preview` / versionCode `6040083`.
- Physical target: NVIDIA Shield Android TV.

The word "Final" here means the accepted 4.1.7 development APK. It does not mean all physical Shield QA is complete. The cleaned 4.1.7 lineage was promoted to `main` on 6 October 2026; `main` is now the authoritative repository branch.

## Authoritative 4.1.7 records

Read `docs/README.md` for the documentation map. Read these when implementation detail or evidence is needed:

- `docs/preview-4.1.7/IMPLEMENTATION_REPORT.md` — frozen source, artifact, hashes and validation evidence.
- `docs/preview-4.1.7/RELEASE_NOTES.md` — 4.1.7 delivered changes and limits.
- `docs/preview-4.1.7/REQUIREMENTS.md` — 115-entry requirements register.
- `docs/preview-4.1.7/CONFORMANCE_REVIEW.md` — conformance evidence and historical checkpoints.
- `docs/preview-4.1.7/IMPLEMENTATION_LOG.md` — detailed development history.
- `docs/qa/4.1.7-final-shield-qa.md` — post-delivery physical Shield findings and the frozen fixes-only scope.

## Current work

The next implementation task is a **strict fixes/corrections pass based on the 4.1.7 Final Shield QA**. It is not a new feature release.

The fixes are defined in `docs/qa/4.1.7-final-shield-qa.md`. Do not silently broaden that scope.

The version number for this fixes release is **NEEDS DECISION**.

## Explicit non-goals for the immediate fixes pass

- Do not begin the package/application-ID transition.
- Do not create or replace the signing identity.
- Do not begin the fresh-install identity phase.
- Do not begin Smart Collections, Library Health, Profiles, custom library pages or other parked/future features.
- Do not merge into `main` merely as part of implementing the fixes.
- Do not treat historical/interim 4.1.7 artifacts as the Final baseline.
- Do not invent production put.io OAuth credentials.

## Project status vocabulary

Use these labels in project records:

- **APPROVED** — explicitly accepted direction/requirement.
- **PARKED** — deliberately deferred; not current scope.
- **REJECTED** — considered and explicitly not wanted.
- **SUPERSEDED** — once valid, replaced by a later decision/state.
- **NEEDS DECISION** — unresolved and must not be guessed.

## Workflow

### Start of a work session

1. Read this file.
2. Read the current QA/work-scope record and any linked authoritative specification needed for the task.
3. Inspect the live repository/branch state before changing code.
4. Report the current baseline, active scope, constraints and next task.
5. Do not infer current state from an older handover when a newer authoritative record exists.

### End of a work session

Update GitHub with durable project knowledge from the session. Record decisions, bugs, QA results, approved requirements, parked/rejected ideas, implementation state and next actions in the appropriate current records. Do **not** paste chat transcripts into the repository and do not convert casual discussion into an approved requirement.

The repository is intended to be sufficient for a new developer or AI with no ChatGPT conversation history to resume the project safely.

## Repository authority

The cleaned Preview 4.1.7 lineage was promoted to `main` on 6 October 2026 by PR #5. `main` is now the authoritative repository branch. The accepted 4.1.7 Final APK remains tied to the frozen application/test source commit `7efb4195639205a6526281491af40c0dab1776b7`; later `main` commits may contain documentation or repository-maintenance changes and must not be mistaken for that APK source.

## Foundation readiness checkpoint — 9 October 2026

Approved Foundation implementation and exact-source FFmpeg rebuild are retained only on codex/foundation-release. Cold keyless CI run 37929976741 passed at 64e1a2eb: 419 application tests, 36 Python tests, zero lint errors, 64 four-ABI native comparisons, actual app.supernova.player / 0.133 / code 133 and all runtime/native/branding/legal gates. Corrected public certificate validation succeeded. Three review passes clear pre-signing readiness; no signed Foundation APK exists and protected readiness/signing has not been enabled. Final user approval is required next, followed by public protected environment variable enablement and the existing prepare workflow. Physical Shield acceptance follows verified signed delivery. Main and frozen fixes branch remain unchanged. See [report](docs/foundation/IMPLEMENTATION_REPORT.md), [CI evidence](docs/foundation/CI_REHEARSAL_INDEX.json), [readiness](docs/foundation/READINESS.json) and [QA](docs/foundation/QA_CHECKLIST.md).

Latest authorised signing status: user approved app source 11766e59 and enabled protected readiness; fresh validation succeeded. Signed assembly passed but final publication failed closed on a reproduced Build Tools 37 output-format mismatch. Workflow-only correction 4a3d741e pins reviewed 36.0.0 and preserves app source 11766e59. A fresh prepare dispatch is pending; no additional signing approval is needed. No verified signed APK has been delivered. See [current signing status](docs/foundation/SIGNING_BUILD_STATUS.md).
