# Supernova — Design Authority Index

Status: **ACTIVE durable project authority**

This directory is the repository's design-memory layer. A future developer/AI must be able to understand the approved appearance, behaviour, parked work and unresolved decisions without access to ChatGPT history.

## Authority/status rules
- **APPROVED** — may be implemented when in active scope.
- **PARKED** — preserve completely; do not implement until activated.
- **REJECTED** — retained to prevent accidental revival.
- **SUPERSEDED** — historical direction replaced by later authority.
- **NEEDS DECISION** — do not guess.
- Later written decisions override older mock-ups where they conflict.
- A mock-up is normative only for explicitly approved aspects; generated accidental UI is not a requirement.
- Missing original image bytes are marked missing, never recreated and mislabeled as original.

## Surface authorities
- [HOME.md](HOME.md) — Home, Featured, rows, Continue Watching, Customise Home; current hero vs parked exposed-card redesign.
- [MOVIES_TV.md](MOVIES_TV.md) — grid/list, toolbar, filters, Unmatched, focus/return and artwork.
- [DETAILS.md](DETAILS.md) — Hero, tabs, Seasons & Episodes, More Like This, Extras, information, Cast & Crew.
- [SEARCH_MATCHING.md](SEARCH_MATCHING.md) — Search keyboard/results and Find a Match/manual correction.
- [NETWORK_FILES.md](NETWORK_FILES.md) — three-panel model, scanning, sources, browser and protocols.
- [SETTINGS.md](SETTINGS.md) — three-panel shell, hierarchy, providers and language presentation.
- [PLAYBACK.md](PLAYBACK.md) — loading/preparation, primary HUD, tracks, More and restoration.
- [GLOBAL_VISUAL_SYSTEM.md](GLOBAL_VISUAL_SYSTEM.md) — typography, focus, dividers, navigation, blur/fade and language iconography.
- [PARKED_FUTURE.md](PARKED_FUTURE.md) — future identity, Profiles, Smart Collections, Discovery, Library Health and broader audits.

## Recovered historical design evidence
The preservation pass reconciled current repository authority with older release/audit records and Project Library design records. Historical approved visual identifiers have been retained in relevant surface documents. Some original generated-image bytes are no longer available as a complete recoverable set; this is explicitly recorded rather than hidden.

Historical records also show that implementation agents for earlier previews inspected supplied approved/reference images (six in the 4.1.2 lineage; five in the 4.1.4 handover). Their implementation/audit results remain evidence even where the original image bytes are absent.

## Current coding boundary
The active next development job remains the frozen **35-item fixes-only** Shield QA scope in `docs/qa/4.1.7-final-shield-qa.md`. Parked design work in this directory does not expand that scope. In particular, the parked Home exposed-card Featured redesign is not activated by being documented here.

## Repository self-containment standard
A fresh developer/AI should be able to determine from GitHub:
1. current baseline and next job;
2. what each major surface should look like and how focus/navigation behaves;
3. what is approved versus parked/rejected/superseded;
4. which design details remain unresolved and must not be invented;
5. historical implementation/QA evidence;
6. known missing visual assets.

The repository stores durable project knowledge, not raw chat transcripts. When a chat produces a durable Supernova decision, the relevant authority file must be updated.

## Visual asset recovery policy
If an original approved mock-up becomes accessible later:
1. commit it under `docs/design/assets/`;
2. preserve original bytes;
3. add a manifest entry with source/date/status/surfaces;
4. state exactly which aspects are authoritative;
5. retain superseded images when they are useful historical evidence.

Never infer an image from an opaque mock-up identifier.
