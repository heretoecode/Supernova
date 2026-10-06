# Supernova — Decisions and Roadmap

This file records durable project-level decisions that affect future work but are not necessarily part of the current implementation scope. Current execution status is always summarised by `PROJECT_STATUS.md`.

## APPROVED — repository as authoritative project memory

GitHub is the durable source of truth for Supernova. A new developer or AI should be able to resume safely without access to prior ChatGPT conversations.

Session workflow:
- Start by reading `PROJECT_STATUS.md` and linked current records.
- Discuss/design/test as needed.
- At session end, update GitHub with durable decisions, issues, QA evidence and state changes.
- Do not store raw conversation transcripts as project authority.

## APPROVED — future identity transition, but not now

After a stable baseline, the intended future direction is a clean Supernova identity:
- fresh install;
- new application/package ID;
- new signing key;
- complete Supernova branding;
- clean app state;
- no requirement for upgrade compatibility from the old app;
- old and new builds should ideally coexist temporarily for Shield testing.

The exact implementation/signing mechanism is **NEEDS DECISION**. This work is explicitly outside the immediate post-4.1.7 fixes release.

## PARKED — broader audits

After the stable corrective baseline, the following broader audits remain parked:
- broader physical Shield QA;
- Movies/TV controls;
- List View data/columns and column sorting;
- full Settings;
- metadata/fields;
- provider/source behaviour;
- broader artwork behaviour;
- Trakt;
- OpenSubtitles;
- TMDB capabilities;
- architecture/baseline;
- inherited Unscraped Media / potential Library Health.

## PARKED / NEEDS DECISION — future product work

- Library Health — **PARKED**
- Profiles — **PARKED**
- Smart Collections — **NEEDS DECISION**, not current scope
- Optional fourth/custom library page — future work; editable/reconfigurable direction retained, not current scope
- Discovery Page — **PARKED**

No item in this section should be treated as permission to implement it during a fixes-only task.
