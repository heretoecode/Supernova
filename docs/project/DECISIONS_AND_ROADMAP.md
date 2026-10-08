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

## PARKED / APPROVED FUTURE DESIGN — current classification (8 October 2026)

- **APPROVED DESIGN, NOT CURRENT IMPLEMENTATION:** Exactly **one** editable custom Library Page, fixed after Home / Movies / TV Shows; five-step create/edit wizard, filters, unsaved-exit protection and delete confirmation. This supersedes the older "Smart Collections needs decision" and generic optional-fourth-page wording. Details and remaining defaults: `docs/design/PARKED_FUTURE.md`, `docs/design/MOVIES_TV.md`.
- **APPROVED FUTURE DIRECTION, NOT CURRENT IMPLEMENTATION:** Library Health within the seven-section Network & Files design; exact operational UX delegated within safeguards. Supersedes older blanket "Library Health parked" classification; see `docs/design/NETWORK_FILES.md`.
- **PARKED:** Profiles; Discovery Page / Coming Soon; Anime; AI additions; seek thumbnails; Trakt ratings/reviews integration. No expansion beyond one custom Library Page.
- **INVESTIGATION ONLY / NEEDS DECISION:** Third-party multi-source ratings (MDBList/OMDb and other providers) — verify data provenance, licence, attribution, quotas and accuracy before approval. No ratings implementation authorised.
- **REJECTED from current scope:** Companion app; new subtitle styling/presets and synchronisation redesign (existing NOVA subtitles retained); unapproved additional custom pages.
- **NEEDS DECISION / AUDIT:** Historical Supernova 0.x version-to-APK mapping; package/signing migration; old backup import and secrets handling; backup state roundtrip for all newly implemented/approved features.

All these are planning classifications. Do not enlarge the fixes-only release or infer that approved designs have been implemented.

## PARKED / NEEDS DECISION — architecture and AI exploration

Historical project discussion explored selective Kotlin/Jetpack Compose modernisation and AI-assisted product capabilities. These are preserved as future exploration, **not approved implementation scope**.

Before any Kotlin/Compose work, decide migration boundaries, compatibility with the inherited Java/XML codebase, TV focus/accessibility behaviour, test strategy and whether migration provides enough value to justify risk. Do not perform a wholesale rewrite by assumption.

AI concepts discussed included natural-language/smart search, richer discovery/recommendation assistance and an “Ask Supernova”-style experience. Before activation, define whether processing is local or remote, data/provider contracts, costs, failure/offline behaviour, what viewing/library context may be used, explicit privacy/consent boundaries and a non-AI fallback. Do not upload watch history or library data merely because an AI feature is being explored.

## Historical reconciliation

Read `docs/project/HISTORY_RECONCILIATION.md` for recovered older decisions, superseded designs, explicit rejections, visual provenance, scanning/focus invariants and historical workflow context that remain useful for interpreting the project.

No item in the parked/future sections should be treated as permission to implement it during a fixes-only task.


## Retiring-chat reconciliation — 8 October 2026

The following current design authorities capture decisions recoverable from the 7–8 October discussion. Read the **latest dated approval** within each file rather than superseded proposal text:

| Authority | Durable decision / correction |
|---|---|
| `docs/design/DETAILS.md` | Multiple versions: compatible-quality auto-ranking; explicit selected version persists; Versions (N) on individual film/episode only; compact selected-file resolution/HDR in hero metadata even for single copy; show-level aggregate stays distinct. |
| `docs/design/HOME.md` | Dynamic genre membership vs Show/Hide, per-row ascending/descending sort, Continue Watching Reset Progress with confirmation; Featured alternatives that are explicitly parked must not override accepted hotfix. |
| `docs/design/MOVIES_TV.md` | Permanently outlined icon+label toolbar controls; accent focus; full divider below; custom-page Edit at far right, same styling. Older divider-segment-only styling is superseded. |
| `docs/design/SEARCH_MATCHING.md` | Local search with real typo/article/punctuation/alternate-title support; reuse stored original titles, investigate additional verified TMDb aliases; don't infer international aliases already implemented. |
| `docs/design/PARKED_FUTURE.md` | One custom page, five wizard steps, no Exclusion Options, unsaved exit and delete safety; sequential 0.1/0.2/.../0.10/0.11/0.12 release numbering; current release notes always expanded, historical notes expandable and monochrome; full secret-free backup and complete restore; 7–8 October approval status. |
| `docs/design/PLAYBACK.md` | Four-button HUD; segment-skip rules and five-second pill; Binge Up Next; subtitle system left as-is at user's final request. |
| `docs/design/SETTINGS.md`, `docs/design/NETWORK_FILES.md` | Existing approved future directions; detailed walkthroughs intentionally last, not implied approval to implement. |
| `docs/qa/home-hotfix-candidate.md`, `PROJECT_STATUS.md` | Signed Home replacement candidate and QA state remain authoritative for implementation; physical Shield validation pending. Planning discussions must not promote candidate to accepted Final. |

**Evidence and mock-ups:** Verified that `docs/design/references/details-information-approved.png`, `network-files-main-approved.png`, and `network-files-submenus-approved.png` exist on the active branch. Other historical mock-ups are referenced in their design documents; do not treat illustrative chat layouts as approved image assets. Existing `docs/design/approved-mockups/` and any new-chat reconciliation were not exhaustively enumerated by this pass; their exact asset inventory remains to be checked before claiming full coverage.

**Scope of this audit:** This reconciles the conversation material available in the retiring chat context and the current documented authorities; it is **not a verified read of every message in the complete historical chat**, nor a complete file-by-file inventory of every directory. Any earlier content not available here is an explicit recovery limitation. Preserve concurrent newer documentation and do not overwrite its decisions.
