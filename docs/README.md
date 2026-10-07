# Supernova Documentation Index

This index separates **current authority** from **historical evidence**. If a historical document conflicts with the current status, follow `/PROJECT_STATUS.md` and the current records linked from it.

## Start here

1. `/PROJECT_STATUS.md` — current baseline, active work, constraints and session workflow.
2. `project/DECISIONS_AND_ROADMAP.md` — durable approved/parked/future project decisions.
3. `design/README.md` — current design authority and status rules.
4. `qa/next-version-authority.md` — **current next-version authority**: completed physical Shield outcomes, approved implementation scope, regression protections and conformance rules.
5. `qa/4.1.7-final-shield-qa.md` — original 35-item physical QA/fixes scope; retained as historical input to the newer next-version authority.
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

`preview-next/` contains earlier forward-looking implementation/preservation work. It is **not automatically the active next-release specification**. Current scope is defined by `/PROJECT_STATUS.md` and `qa/next-version-authority.md`.

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

## Active next-version development

- [Next-version authority](qa/next-version-authority.md) — authoritative continuation after physical Shield QA; includes reopened failures, approved redesigns, regression protections and implementation/conformance rules.
- [Post-4.1.7 Shield fixes implementation and 35-item traceability](qa/post-4.1.7-fixes-implementation.md) — historical development status of the tested candidate; its FIXED labels do not override later physical Shield results.

- [Previous post-4.1.7 signed fixes candidate](qa/post-4.1.7-fixes-candidate.md) — historical build at `61a1ae5`; later physical QA/design authority supersedes its implementation labels and old unresolved design wording.


## Audit evidence

- [Completed audit register](audits/README.md) — durable findings, recommendations, decisions, risks and follow-up from completed Supernova audits.

## Record-keeping rule

GitHub is the authoritative durable project record. Material discussion outcomes, plans, decisions, QA evidence, audit results, implementation/build evidence and unresolved dependencies must be written here rather than existing only in chat history.

## Current consolidated corrective candidate

- [Signed Shield-test candidate](qa/next-corrective-candidate.md) — exact source `b021c51`, APK/hash/certificate, successful complete validation and download. Physical Shield QA pending; not Final, main unmerged.
- [All-item conformance and physical checklist](qa/next-corrective-conformance.md) — original 35 items and consolidated additions; implementation/automation/render/Shield states kept separate.
- [Implementation and validation work record](qa/next-corrective-implementation.md) — decisions, investigations, failed gates and corrections, final evidence and limits.

The primary consolidated scope remains [next-version-authority.md](qa/next-version-authority.md). Later written design amendments govern historical conflicts. Release numbering remains NEEDS DECISION; language mapping is resolved; #35 broader artwork reliability remains observationally PARTIAL. The next action is user physical Shield QA of this exact candidate, recorded in GitHub.
