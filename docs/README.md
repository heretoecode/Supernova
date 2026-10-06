# Supernova Documentation Index

This index separates **current authority** from **historical evidence**. If a historical document conflicts with the current status, follow `/PROJECT_STATUS.md` and the current records linked from it.

## Start here

1. `/PROJECT_STATUS.md` — current baseline, active work, constraints and session workflow.
2. `project/DECISIONS_AND_ROADMAP.md` — durable approved/parked/future project decisions.
3. `design/README.md` — current design authority and status rules.
4. `qa/4.1.7-final-shield-qa.md` — current physical QA findings and bounded fixes-only scope.
5. `project/HISTORY_RECONCILIATION.md` — recovered historical decisions, superseded designs, rejections and future explorations needed to interpret older project history safely.

## Current 4.1.7 authority

The `preview-4.1.7/` directory is the detailed release/development record.

- `IMPLEMENTATION_REPORT.md` — final frozen source and artifact evidence.
- `RELEASE_NOTES.md` — delivered changes and explicit limitations.
- `REQUIREMENTS.md` — authoritative 115-entry implementation register for 4.1.7.
- `CONFORMANCE_REVIEW.md` — three-pass conformance evidence and retained historical checkpoints.
- `IMPLEMENTATION_LOG.md` — detailed chronological implementation history.
- `PREFLIGHT.md` — preflight and scope establishment.
- `PUTIO_API_IMPLEMENTATION.md` — put.io implementation state and external credential limitation.
- `INTERIM_SHIELD_QA.md` — historical interim evidence only; it is not the Final APK.
- `RECOVERY_2026-09-27.md` and `CONTINUATION_2026-10-05.md` — recovery/continuation records; historical once superseded by final report.

## Design authority

`design/` preserves the current visual/behaviour authority for Home, Movies/TV, Details, Search/Matching, Network & Files, Settings, Playback, the global visual system and parked future design. Later written decisions override conflicting older mock-ups. Missing original visual bytes are explicitly identified rather than recreated.

## Historical records

Older Preview 3.x / 4.0 / 4.1.x handovers, audits, build records, recovery notes and QA files remain useful evidence. They are **SUPERSEDED as statements of current project state** unless explicitly linked by a current authority file.

The older root-level `NOVA_*` handover/audit/QA files have been preserved under `archive/root-history/`. Older version-specific material already under `docs/` remains historical unless explicitly promoted by a current authority record.

The legacy `doc/` directory is inherited/upstream technical documentation. Do not confuse it with Supernova's project-state records under `docs/`.

`project/HISTORY_RECONCILIATION.md` is the bridge for historically important decisions recovered from older project records that should remain understandable without ChatGPT history. It does not override newer current authority.

## Preview-next material

`preview-next/` contains earlier forward-looking implementation/preservation work. It is **not automatically the active next-release specification**. Current scope is defined by `/PROJECT_STATUS.md` and `qa/4.1.7-final-shield-qa.md`.

## Build and delivery documentation

Existing build/delivery architecture records under `docs/` and CI material under `.github/` remain valid technical references where they match the current build. Final release identity/hash evidence should be recorded in the version's implementation report and, once established, its GitHub Release.

## Evidence policy

- Keep raw/high-volume diagnostic or visual evidence only when it materially supports QA or reproducibility.
- Prefer indexed evidence with source/version/time context over dumping unlabelled media into Git history.
- GitHub Releases are the intended durable home for distributable APK artifacts.
- Historical evidence must be labelled so it cannot be mistaken for the current baseline.
- Raw ChatGPT transcripts are not project authority; durable decisions from discussion must be reconciled into current GitHub records.

## Status vocabulary

Use **APPROVED**, **PARKED**, **REJECTED**, **SUPERSEDED**, and **NEEDS DECISION** consistently. Do not infer approval from an idea merely appearing in an old handover or discussion.

## Active fixes development

- [Post-4.1.7 Shield fixes implementation and 35-item traceability](qa/post-4.1.7-fixes-implementation.md) — development branch only; accepted baseline remains 4.1.7 Final, physical acceptance pending.
