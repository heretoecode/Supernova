# Repository Cleanup Plan

Status: **IN PROGRESS — preservation-first**

This record documents how the repository is being converted into durable project memory without destroying useful history.

## KEEP as current authority

- `/PROJECT_STATUS.md`
- `/README.md`
- `/docs/README.md`
- `/docs/project/DECISIONS_AND_ROADMAP.md`
- `/docs/qa/4.1.7-final-shield-qa.md`
- `/docs/preview-4.1.7/` final implementation/conformance/requirements/release records

## KEEP as historical evidence

- root-level `NOVA_*` handovers, UI audits and older Shield/screenshot evidence;
- older Preview 3.x/4.0/4.1.x records under `docs/`;
- earlier build/recovery/diagnostic records;
- `docs/preview-next/` as historical forward-looking work;
- legacy/upstream `doc/` technical documentation.

These records are **SUPERSEDED as current-state entry points**, not necessarily incorrect historical evidence.

## ADD / establish

- current status entry point and documentation index;
- durable decisions/roadmap record;
- current physical QA/fixes-only record;
- GitHub Release for 4.1.7 Final with the verified APK and checksums when artifact availability is confirmed;
- structured issue tracking for active defects/tasks once Issues are enabled;
- evidence index/policy for diagnostics, screenshots and videos;
- clean branch/release policy after current lineage is safely promoted.

## DO NOT DELETE YET

No historical branch, document, PR or evidence file should be deleted merely for tidiness until:
1. the authoritative baseline has been promoted safely;
2. unique information has been checked;
3. release evidence is durable;
4. a cold-start repository reconstruction test succeeds.

## Branch observations

At cleanup start:
- `main` was behind the 4.1.7 development lineage;
- `codex/preview-4.1.7` contained the current development history;
- final 4.1.7 application/test source is `7efb4195639205a6526281491af40c0dab1776b7`;
- later documentation/closeout commits can exist after the frozen application source;
- cleanup work is isolated on `project/authoritative-cleanup`.

Do not equate the latest documentation commit with the source SHA used to build the Final APK.

## Open repository-administration decisions

- **NEEDS DECISION:** exact fixes-release version number.
- **NEEDS DECISION:** when/how to promote the cleaned 4.1.7 lineage to `main`.
- **NEEDS DECISION:** final archive/delete treatment of old branches after preservation checks.
- **NEEDS DECISION:** disposition of the old Jules UI/mock-up PR after its unique design evidence is classified.
- **PROPOSED:** enable GitHub Issues and use them for active bug/task tracking.
- **PROPOSED:** use GitHub Releases as the canonical home for distributable APKs.

Package ID, new signing key and fresh-install identity transition are later product work, not repository-cleanup tasks.
