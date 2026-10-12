# Next version: compact, non-scrollable Details tab and global action buttons — 2026-10-12

**Status:** Explicit user requirements for next release, independent of later full lower-details-page redesign. Documentation-only; no code changes or Codex handover authorised yet.

## Details tab content and layout

- Five existing information sections should be visible **together without vertical scrolling** within the lower Details content area: **Cast, Crew, Key Information, Reception, Technical Information**.
- Cast maximum **8** entries, Crew maximum **8**, with no See All/Show More/expand option; see `NEXT_RELEASE_CAST_CREW_EIGHT_ITEM_CAP_2026-10-12.md`. If fewer entries exist, no placeholders.
- Make Key Information, Reception and Technical Information **compact**: label on the **left**, corresponding value to the **right on the same row** (e.g. `Year    2024`, `Resolution    4K UHD`). Replace current stacked label/value layout, eliminate excessive padding/box height; consistent shared typography, columns and spacing.
- Fit all five sections without reducing text below readable TV sizes. Audit existing screen geometry, possible layout arrangements, overflow/long strings and small display configurations; verify with real render and Shield tests. Do not silently hide valid required sections just to fit. Missing values/sections may collapse appropriately.
- **No items inside the lower Details tab should be focusable or clickable.** Focus stays on the **Details tab label**; D-pad Left/Right changes tabs. Distinguish this from **upper Details Card** where Play/Streaming/More remain focusable. Audit focus traversal and ensure no invisible focus stops.
- Lower tab styling: **white underline when focused**, mirroring existing top navigation, **no full-width separator**; selected state remains legible when focus elsewhere.

## Action buttons on upper details page

- Current Play and More appear as bare text at rest. Apply the previously approved **global Supernova button design** to Play, More, Streaming when applicable, and any other upper details actions.
- Buttons must visibly read as buttons in **unfocused/resting state**, with consistent border/background, typography, dimensions, spacing and distinct D-pad focus treatment per the existing global design (also used for hero More Info and Home Customise Home). Do not invent a separate details-only button style.
- Implement on existing details page as part of next version; do **not** defer until new Details Card is completed. When migrated into the Details Card, retain shared design and actual button focus.
- Button availability varies by item; do not force irrelevant actions.

## Release and QA

- User explicitly requests all above in next version **even if no further lower-details-page discussion happens**. Include with already agreed global metadata, upper Details Card design direction and other QA requirements in the consolidated handover, but reconcile scope and implementation approval before coding.
- Test no-scroll for titles with 0/8/>8 cast and crew, long names, various technical/reception fields, TV vs movie; test Left/Right tab focus and upper button rest/focus visuals.
- These are **design/acceptance requirements, not claims of implemented functionality**. No changes to active development branch without explicit authorisation.

## Approved spacing clarification — compact label/value cards

For Key Information, Reception and Technical Information, labels (e.g. `Year`, `Resolution`) and their values (e.g. `2024`, `4K UHD`) sit on the **same line** with a **comfortable, deliberate gap**: neither crowded together nor pushed to opposite edges of an excessively wide box. Use a restrained two-column alignment with consistent label widths or appropriate flexible grid, ensuring readable proximity and clear association. Card/container widths and padding should fit their contents comfortably, with **no excessive empty space**. Avoid assuming `justify-content: space-between` across a very wide container is acceptable. Test representative short/long labels and values on actual Android TV dimensions and align consistently across all three sections. This is a next-release acceptance criterion.
