# Supernova — Project Status

> **START HERE.** This file is the authoritative entry point for the current Supernova project state. Historical documents may describe earlier states; where they conflict with this file or the linked current records, the current records take precedence.

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
