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

Physical NVIDIA Shield QA of the signed post-4.1.7 candidate is substantially complete. The candidate remains **unmerged and not physically accepted as Final**. Several items that the earlier development traceability record labelled FIXED/VERIFIED failed physical conformance, while other items passed and are now regression protections.

The authoritative next-version implementation scope is now:

- **[docs/qa/next-version-authority.md](docs/qa/next-version-authority.md)** — START HERE for the complete physical-QA outcome, approved next-version work, conformance rules and exclusions.
- Updated `docs/design/` authorities — current design/behaviour decisions.
- `docs/qa/post-4.1.7-fixes-implementation.md` — historical development traceability for the tested candidate; its FIXED labels do **not** override later physical Shield results.

The next version continues from the existing `codex/post-4.1.7-shield-fixes` implementation. Do not restart, reset, discard or replace work that physically passed. The release is primarily correction/stability/polish, with the explicitly approved Home Featured exposed-card redesign and approved Details/Extras redesign included. The version number remains **NEEDS DECISION**.

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

### Post-4.1.7 fixes candidate — unmerged branch

The signed post-4.1.7 fixes candidate is available on `codex/post-4.1.7-shield-fixes`; exact APK source is `61a1ae5a21be90d185d448362c9729eb6c33cc42`. See the [candidate identity/build/validation record](docs/qa/post-4.1.7-fixes-candidate.md) and historical [35-item development traceability](docs/qa/post-4.1.7-fixes-implementation.md). Automated/build validation was green, but subsequent physical Shield QA found multiple conformance failures. **The candidate is not Final and has not been merged into main.** The authoritative continuation is [docs/qa/next-version-authority.md](docs/qa/next-version-authority.md). Cast & Crew and final Audio/Subtitles/More design are now decided there; #35 artwork reliability remains observational; fixes-release numbering remains unresolved. Existing application ID, certificate and version fields are retained.


## Durable-record rule

GitHub is the authoritative Supernova project memory. At the end of each work session, material discussion outcomes, plans, approvals/rejections/parked decisions, QA results, completed audit findings, implementation/build evidence and unresolved dependencies must be recorded here rather than left only in ChatGPT conversation history. Completed audits are indexed at `docs/audits/README.md`.
