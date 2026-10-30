# Edit Library — strict UI blueprint, DRAFT for owner approval (30 October 2026)

**Authority:** This document captures the owner's iterative design decisions and the source audit in draft PR #28. It is a **draft design specification, NOT implementation authorisation**. The supplied generated mock-ups are illustrations only and contain fabricated fields, sample counts, artwork and values. Codex MUST NOT implement anything solely because it appears in an image. Any conflicting prior five-step/three-panel design is superseded **for proposed UI geometry only**, not underlying persisted behaviour.

**Read first:** `docs/design/EDIT_LIBRARY_SOURCE_AUDIT_AND_REDESIGN_PROPOSAL_2026-10-30.md` on branch `docs/edit-library-audit-and-proposal-2026-10-30` (draft PR #28), maintenance-investigation mapping, current 0.147 delivered source and existing GLOBAL_VISUAL_SYSTEM / shared controls. The audit is the authoritative parity inventory and defect analysis. Do not rebuild the already-working custom library feature, alter media/index/backup schema, or change main/PR #27.

## A. Owner decisions already confirmed
1. Do **not** show the global top navigation bar in **design mock-ups**. This is NOT approval to remove the real app's shared navigation.
2. Do not force a three-panel design; use as many regions as functionally justified. Current mock-up uses left section rail, editing workspace and Live Preview, subject to final geometry review.
3. One custom library slot already exists and works. This work refines Edit Library only.
4. No page icons, page description, Home visibility, or global-search inclusion options. These are not editor capabilities. Name is the only Page Details field.
5. Combine Page Details and Content Type into ONE section; four left-rail sections: **Page Details & Content Type**, **Filters**, **Display Options**, **Review**. Content Type has **Movies**, **TV Shows**, **Both** (mock-up label “Mixed” may be used only after owner approval; persisted value remains Both).
6. Live Preview: no hero image, banner, page description or decorative art. Statistics immediately below heading; then **actual matching library items** (posters where actual artwork exists). Reflect draft content type and filters; never show TV items for Movies-only selection. Show honest loading/empty/error states and count; avoid fabricated runtime, counts or posters.
7. No persistent bottom **Cancel / Previous / Next** actions. Directly select any of the four rail sections with D-pad. Remote Back is the exit request; if dirty, offer **Save Page / Discard Changes / Continue Editing**. If clean, return to opener. Keep a clearly reachable **Save Changes** action in Review. Preserve existing save validation/failure and delete confirmation; deletion remains available only for an existing page and requires confirmation, and never deletes media.
8. Use one existing static Supernova Space Black background and a single consistent scene/safe area, not layered dynamic artwork behind editor/nav. Do not add a second app header.
9. Live Preview focus outline must remain visibly complete on **all four sides** while inner contents scroll. Use stationary focus frame separate from scroll child; ensure consistent corner radius and inset.
10. Focus, selected section, selected choice and enabled toggle states are **four distinct visual concepts**. Never rely on accent fill alone to communicate ON/OFF or checked choice. Use selected accent colour for focus/choice according to global system, neutral unfocused surfaces.
11. The user's preference is direct D-pad navigation between rail, form and preview, not wizard Next/Previous controls.

## B. Strict content inventory — NO invented settings
**Section 1: Page Details & Content Type**
- Library Name: one editable field, trim/validate 1–40 characters, preserve keyboard Done/Cancel.
- Content Type: exactly Movies / TV Shows / Both, mutually exclusive; current selection distinct from focus.
- No page icon, description, home toggle, global-search toggle.

**Section 2: Filters**
- Genres: actual indexed choices; multi-select; All Genres reset; Any/All match rule; preserve existing draft, no Done button unless picker infrastructure requires it (then resolve before final approval).
- Year: existing **single-year** selector or All; **do not** implement mock-up's arbitrary 2020–2026 year range.
- Watched: All / Watched / Unwatched / In Progress.
- Minimum rating: Any / 5 / 6 / 7 / 8 / 9, preserving valid stored values and unknown handling.
- Original Language: conditional, actual indexed language tags; retain selected value if temporarily absent.
- Studio / Network: conditional, actual indexed values; retain saved selection.
- **Do not add** Include Subfolders, Special Features, excluded studios, a country facet, source picker, arbitrary metadata fields, filter sliders or toggles shown in generated image. Existing hidden collection compatibility filter is retained in persistence without inventing an editor control.

**Section 3: Display Options**
- View: Grid / List.
- Sort: Title / Year / Date Added (wizard's persisted choices; do not remove existing shared toolbar sort options).
- Direction: Ascending / Descending.
- **Do not add** Show metadata indicators, Show watched status, Show item count on tiles or other new toggles merely depicted by image.

**Section 4: Review**
- Read-only grouped summary of actual name, content type, selected filters and display options, showing truthful values.
- **Save Changes** action for existing library / **Save Page** for creation; preserve validation, atomic write/failure handling and opener intent.
- **Delete Page** for existing page only, visually separated/destructive with confirmation and safe default Cancel; never delete media, history, source identities.
- No fabricated settings or duplicate Previous/Next/Cancel footer.

**Live Preview (visible alongside each section)**
- Fixed title, truthful statistics (items, movie/show counts and supported real indexed size/source metrics from existing model), actual matching posters/items in draft order; don't fabricate “total runtime” if source cannot supply it.
- Data-backed preview, up to 20 actual items as in current source unless later approved; visible count/list cap indication.
- Fixed outer focus frame, separately scrolling inner list, no top banner; preserve snapshot error/empty/load distinctions.

## C. Navigation/behaviour contract
- Left rail Up/Down moves among four steps, OK/Right enters active section. Form Left/Back returns to the same rail selection when not in a picker. Right to preview from a defined field/edge; preview Left/Back returns to last valid form control. Up/Down within preview scrolls content and never moves stationary outline.
- Back on the rail requests editor exit. Dirty dialog has Save Page, Discard Changes, Continue Editing; Continue Editing restores invoking control. Explicit top-nav exits preserve the intended destination, not Home by default. Clean exit returns to opener.
- Focus restoration uses stable semantic IDs, not detached View references. Do not rebuild all panels on every field change; preserve form/preview scroll and editor state.
- Top nav remains part of the real app and its existing global contract, though excluded from visual mock-up crop. Editor nav exits are guarded; never allow blocked underlying library to steal focus.
- Reconcile draft PR #28 EL-01–EL-08 and acceptance matrix; fix source-confirmed discarded destinations/stale focus, investigate physical stats/tab mismatch without claiming resolved.

## D. Visual design tokens — proposed measurable baseline, **NOT YET OWNER-APPROVED**
The mock-ups are not dimensionally trustworthy. The following **candidate** tokens must be checked against the app's approved global visual system, physical 1080p Shield screenshots, 720p/4K scaling and text accessibility before being frozen:
- Logical design viewport: 1920×1080 reference, TV safe margins 48 px left/right and 40 px top/bottom (candidate only).
- Scene: existing approved Space Black drawable, not a newly generated gradient. Single content heading “Edit Library”, candidate 38 px; explanatory subtitle candidate 20 px. Do not duplicate global nav.
- Candidate layout: rail 22%, editor 35%, preview 43% of usable content width; 16 px inter-region gap, 18 px inner padding; never force fixed percentages where text/focus clips.
- Candidate panel radius 16 px, neutral 1 px outline, focus 2 px accent outline inset ≥4 px; 12 px gap between option rows, minimum focusable row height 56 px, section label 24 px, body 18–20 px. Use existing font family, not a new typeface.
- Candidate radio/choice: neutral outlined card, icon only if it maps to existing app icon asset, label and explicit selected marker; distinguish focused vs checked. Use **existing icon assets** and shared icon weights, not AI-generated symbols.
- Candidate statistics: equal-width tiles, 12 px gap, icon + value + label, all real source-backed; preserve responsive wrapping.
- Candidate posters: four columns at 1080p if real aspect ratios and focus fit; responsive columns, actual artwork and proper fallbacks; do not hard-code illustrative posters.
- All token values are **proposals**, not locked implementation numbers. Codex must produce an exact measured token table and annotated per-section normal/large-text/720p/1080p/4K layouts for owner approval. Once approved, replace candidates with **locked** values and exact drawable/font/color IDs. No guessing or “approximately like mock-up” implementation.

## E. Explicit exclusions from generated 2×2 image
Image contains fake title/poster associations, “Release Year 2020–2026”, “Studios Exclude”, Include Subfolders, Include Special Features, metadata-indicator toggle, watched-status toggle, item-count-on-tiles toggle and runtime numbers. These are **not approved** and MUST NOT be coded. Example poster art, counts and ratings are not test fixtures or live data. The generated image is not confirmed uploaded to GitHub.

## F. Required implementation gate
1. Codex reads audit, existing maintenance mapping, source and this blueprint.
2. Produce four **accurate** annotated designs with all controls and candidate geometry replaced by measured values, plus D-pad/focus graph, back/dirty-exit paths, normal/large-text/narrow cases. Do not invent functionality.
3. Owner explicitly approves visuals and exact metrics. Amend this blueprint with **LOCKED** tokens, IDs and acceptance screenshots; mark approval and source commit.
4. Only then implement on a dedicated authorised branch, preserving schema/data and existing branches; run automated regressions and real Shield QA including all focus-frame corners, first-entry stats and custom→TV tab highlight.
5. Keep approved mock-ups in GitHub only after verified upload. Documentation branch commits do not merge into main or authorise code changes.

## G. Outstanding owner decisions
- Exact final region widths, type sizes, spacing and card treatment (pending measured mock-ups).
- Whether Review's Save is placed inside centre content or as a pinned in-panel action (no global footer).
- Whether Delete Page is in Review or retained in Page Details; source currently has it in Page Details. **Do not move without owner sign-off.**
- Whether “Both” or “Mixed (Movies & TV Shows)” is displayed.
- Exact stats fields where existing data supports them.
