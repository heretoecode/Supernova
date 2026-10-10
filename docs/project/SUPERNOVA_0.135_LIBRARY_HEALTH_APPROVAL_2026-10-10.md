# Supernova 0.135 — Library Health promotion

Date: 2026-10-10
Status: **EXPLICITLY APPROVED FOR 0.135 PLANNING SCOPE** — supersedes earlier PARKED classification for Library Health.
Authority: User confirmed “yes, let's promote this to the next version” after reviewing earlier extensive Library Health discussions.

## Approved experience
- **Home informs:** show a discreet, non-blocking notice when library files need attention.
- **Network & Files diagnoses:** display a clear summary of library identification/availability/source problems; this cross-page integration does **not** itself approve the separate five pending Network & Files redesign/import checklist items.
- **Library Health resolves:** dedicated **three-panel** issue review/resolution interface, consistent with Supernova visual language and D-pad navigation.
- Five issue categories: **Unmatched Media**, **Incorrect Matches**, **Missing Metadata or Artwork**, **Unavailable Files**, **Source Problems**.
- Review item shows original filename, source/location as available, match/metadata status, candidate matches and a **Find a Match** action where relevant. Corrections should not remove or destroy underlying media.
- Clear empty state: **“Your library is healthy”** / **“No issues need your attention.”**
- Earlier proposed **Unmatched** toolbar filter on Movies and TV Shows is superseded by this centralised Library Health approach; do not reintroduce it as a duplicate.

## Engineering / design guardrails
- Examine existing NOVA-derived unscraped-media/metadata matching and source-health capabilities before introducing new data structures. Do not assume existing implementation is sufficient.
- Distinguish not-yet-scanned/in-progress from genuinely unmatched or inaccessible media; avoid false warnings.
- Preserve media files and existing user metadata when resolving identification issues; validate handling of offline sources and partial imports.
- Accessibility, focus restoration, D-pad behaviour, issue counts and empty states must be tested before release.
- This is **planning approval only**. No Codex handover, implementation, build, branch merge or protected-branch modification is authorised.

## Relationship to other decisions
- Supersedes the **Library Health — PARKED** entries in `docs/project/DECISIONS_AND_ROADMAP.md`, `docs/design/RECENT_PRODUCT_DECISIONS_2026-10-08.md` and `docs/project/RECENT_DISCUSSIONS_CLOSURE_REGISTER_2026-10-09.md` **for 0.135 scope only**; other parked features remain parked.
- The user-confirmed 0.135 checkbox register remains authoritative for the separate nine unselected checklist items: [0.135 user checklist](SUPERNOVA_0.135_USER_CHECKLIST_SCOPE_2026-10-09.md).
- [Playback HUD design](../design/PLAYBACK_HUD_0.135_APPROVED_2026-10-10.md) and [Custom Library Page approval](SUPERNOVA_0.135_CUSTOM_LIBRARY_PAGE_APPROVAL_2026-10-09.md) are separate confirmed 0.135 decisions.
