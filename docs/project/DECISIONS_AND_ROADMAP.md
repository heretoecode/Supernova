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

## PARKED / NEEDS DECISION — architecture and AI exploration

Historical project discussion explored selective Kotlin/Jetpack Compose modernisation and AI-assisted product capabilities. These are preserved as future exploration, **not approved implementation scope**.

Before any Kotlin/Compose work, decide migration boundaries, compatibility with the inherited Java/XML codebase, TV focus/accessibility behaviour, test strategy and whether migration provides enough value to justify risk. Do not perform a wholesale rewrite by assumption.

AI concepts discussed included natural-language/smart search, richer discovery/recommendation assistance and an “Ask Supernova”-style experience. Before activation, define whether processing is local or remote, data/provider contracts, costs, failure/offline behaviour, what viewing/library context may be used, explicit privacy/consent boundaries and a non-AI fallback. Do not upload watch history or library data merely because an AI feature is being explored.

## Historical reconciliation

Read `docs/project/HISTORY_RECONCILIATION.md` for recovered older decisions, superseded designs, explicit rejections, visual provenance, scanning/focus invariants and historical workflow context that remain useful for interpreting the project.

No item in the parked/future sections should be treated as permission to implement it during a fixes-only task.
