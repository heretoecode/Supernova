# Supernova — Design Authority Index

Status: **ACTIVE project knowledge preservation**

This directory is the durable design-memory layer for Supernova. Its purpose is to make the repository sufficient for a new developer or AI to understand not only *what* a feature is called, but how approved UI and behaviour are intended to look and operate without access to historical ChatGPT conversations.

## Authority rules

- **APPROVED** — may be implemented as specified.
- **PARKED** — preserve the complete recoverable concept, but do not implement in the current fixes pass.
- **REJECTED** — retained only to prevent accidental revival.
- **SUPERSEDED** — historical design replaced by a later decision.
- **NEEDS DECISION** — do not guess.
- An approved mock-up is normative only for the aspects explicitly recorded as authoritative.
- Written later decisions override an older mock-up where they conflict.
- Missing original image bytes must be identified as missing; never fabricate a replacement and label it as the original.
- Historical handovers/audits may contain useful evidence, but current design records must state what remains authoritative.

## Preservation coverage

The preservation pass must reconcile, at minimum:

1. Home — top navigation, Featured/hero, startup/artwork behaviour, rows, Continue Watching, Recently Played/Added, customisation.
2. Movies and TV — grids/lists, controls, summaries, focus/scrolling and return-focus.
3. Details — hero, actions, tabs, More Like This, Extras, Details information, Cast & Crew, TV seasons/episodes.
4. Search and Find a Match.
5. Network & Files.
6. Settings and Integrations/provider selection.
7. Playback — loading/preparation screen, HUD, subtitles, audio, More, focus and return behaviour.
8. Metadata, provider availability, artwork and language presentation.
9. Global visual system — typography, focus, glow, spacing, backgrounds and top navigation.
10. Future/parked product work — enough detail to resume safely, not merely a feature name.

## Current implementation evidence already in GitHub

Earlier release records preserve substantial design evidence. Examples include:
- Preview 4.1.2 return handover and UI audit: global focus, top navigation, Home startup/Featured behaviour, compact Home editor, library summary, Details, Information, Search, file browser, Settings, provider selection and HUD.
- Preview 4.1.4 recovery/release records: five approved/reference images were read during implementation; Featured hero update behaviour, focus, Network & Files, Settings and HUD corrections are documented.
- Preview 4.1.7 requirements/implementation/conformance records: current implementation authority.
- Physical Shield QA: current fixes-only corrections.

These are inputs to preservation, not substitutes for consolidated design specifications.

## Visual asset gap

Some historical handovers explicitly record that approved/reference images were supplied to implementation agents, but those image bytes are not currently present on authoritative `main` as a complete design library. The preservation pass must recover actual visual assets wherever accessible. Where an original cannot be recovered, preserve its recoverable specification and provenance and mark **ORIGINAL VISUAL ASSET NOT RECOVERED**.

## Completion standard

This preservation work is complete only when a fresh developer/AI, given this repository and no ChatGPT history, can determine:
- current visual/behavioural intent;
- exact approved versus parked/rejected/superseded status;
- what an approved design requires beyond a feature name;
- which historical visual is authoritative and for what;
- unresolved decisions that must not be invented;
- links to implementation/QA evidence.

See `/PROJECT_STATUS.md` for current execution scope. This preservation pass does not authorise implementation of parked/future work.
