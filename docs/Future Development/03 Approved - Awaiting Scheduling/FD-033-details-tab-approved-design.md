# FD-033 — Details tab: owner-approved visual and interaction specification
**Approved:** 13 October 2026
**Status:** APPROVED — AWAITING RELEASE SCHEDULING. **Not implemented by this documentation change.**
**Scope:** Details tab beneath the existing movie/TV hero; not a new full-screen details page, not the hero card, and not a top navigation bar.
**Reference:** Final owner-approved visual mock-up in the Supernova planning conversation (13 October 2026; the image depicts *The Goonies* and the connected outlined Details tab). This document is the durable text handover; the original rendered image is in the conversation and is **not embedded in GitHub**.

## Display context and overall composition
- Render on the existing 16:9 TV screen. Scrolling down from the hero hides the hero's synopsis, metadata, playback actions and overflow actions; the **official title typography remains visible at upper left**. The tabs/panel begin lower on the screen to allow that typography breathing room.
- No additional top navigation bar, invented actions, My List, View All, or unrelated elements.
- Preserve established Supernova dark navy/translucent appearance, official typography where available, existing fonts, icon family, spacing conventions, and theme/accent behaviour.
- Keep the connected tab/content frame approximately 4% inset from left/right/bottom screen edges, with restrained corner radii (not pill-like) and thin borders.
- The **entire active Details tab and the entire connected content panel** share **one continuous thin focus-coloured outline**. The standard focus colour is white, but use the user's selected app focus/accent colour. The outline rises once around the active Details tab, descends at its right edge, continues across the panel's top and around its outer sides/bottom; no doubled left border, internal underline, gap or second focus border.
- The active tab's icon and label are visually **centred together** within the active tab shape. No blue glow or underline below Details; the content area visually joins the active tab with **no horizontal border separating them**.
- Inactive **Extras**, **Seasons & Episodes**, and **More Like This** are floating icon-and-text labels in their positions: **no boxes, outlines, underlines or focus decoration** when inactive. Left/Right remote input changes active tab and connected focus frame. Do not add selectable content to Details.
- Tabs appear in order: **Details → Extras → Seasons & Episodes → More Like This**. Show relevant tabs for the content type according to existing product decisions; do not invent movie seasons.

## Read-only Details panel and fixed layout
- All information on the Details panel is **non-focusable and non-selectable**. Remote focus remains on the active tab heading; Left/Right switches tabs. The panel must be readable **without vertical or horizontal scrolling** at the supported TV viewport. No clickable rating/award arrows.
- Upper, visually primary group, left to right: **Description | Key Information | Reception**. Lower, supporting group: **Cast | Crew | Technical Information**. Thin subtle separators between columns and between the upper/lower group, with the horizontal separation slightly lowered to give Reception and awards breathing room.
- Section headings are plain bold text: **Description, Key Information, Reception, Cast, Crew, Technical Information**. **No underlines of any length**, no blue heading accents, and no parenthetical cast/crew counts.
- Maintain generous vertical separation between **Cast/Crew headings and the first portraits**; never crowd the headings.
- Do not stretch or shrink text to illegibility. Codex should use responsive measurements, typography and graceful text handling to preserve the approved hierarchy.

## Description
- Show the full available synopsis within its allocated area rather than the hero's line-limited preview.
- Centre the body copy within the description region horizontally and use the vertical area naturally, with balanced line lengths, comfortable leading and moderately enlarged type relative to ordinary metadata; **not** as large as the earlier oversized mock-up. The owner explicitly approved the slightly reduced type in the final design.
- Handle unusually long descriptions without adding a scrollable/read-more control; design an appropriate responsive readable fallback, and document any impossible edge cases for QA. Do not silently claim that arbitrary-length synopsis text always fits.

## Key Information
- Preserve the existing key facts and include **Original Title, Also Known As / Alternative Names, Release Date, Runtime, Genres, Country, Language, Production Company, Budget, Revenue**, when sources support them. Existing relevant facts may remain subject to fit.
- Use a clear **label–value** two-column arrangement with a visibly wider gap (owner requested at least double the earlier narrow gap).
- If a key-information data point is absent, render **Unknown** rather than leaving a broken/blank row. Handle long values gracefully.

## Reception — expanded supplementary ratings and recognition
- Always show **IMDb**, **Rotten Tomatoes**, **Metacritic / Metascore**, and **Awards / Recognition** as stable, visible slots.
- Display IMDb audience rating and votes; Rotten Tomatoes percentage where available; Metascore where available; wins/nominations counts and **named awards/achievements** where reliable data exists. Existing reception information should not be unnecessarily removed.
- Missing individual rating: **No rating available**. Missing recognition: **Unavailable**. No fabricated score or award.
- OMDb is an approved *candidate* supplementary source for these data, alongside existing providers; **do not assume live access, coverage, redistribution rights or licensing approval**. Validate source IDs, data quality, caching, quotas and OMDb's terms before enabling. TMDb remains the main metadata provider; OMDb's US `Rated` must not override country-specific certification.
- Rating/award areas are informational, not buttons, links, arrows or focus targets.

## Cast and Crew
- Show **up to eight cast and up to eight crew** entries in each section, in **four columns by two rows**, with small circular portraits, readable names and characters/jobs underneath.
- No View All button, count badge, extra entries, repeated people, or horizontal/vertical scrolling. Missing portraits and long names need sensible fallbacks.
- Do not treat synthetic portraits/names in the illustrative mock-up as factual reference data or a mandate to duplicate them.

## Technical Information
- Preserve technical facts available for the selected movie/TV context, e.g. Video, Audio, Container, Size, Source, Added and Path; use correct per-item vs TV aggregate semantics.
- **File path must remain one line only**, never wrap; truncate appropriately (prefer preserving the filename) when it cannot fit. Do not underline dates or other data values.
- Existing forensic audit identified TV aggregation, episode refresh and season metadata gaps; these are Codex engineering investigation/QA items, not open owner design decisions.

## Navigation, implementation boundaries and QA
- Preserve existing hero focus/playback controls above; no new hero controls are approved by this design.
- Verify focus traversal, active tab frame geometry, custom focus colours, screen size scaling, text legibility, portrait rows, missing-data placeholders, very long metadata/path, TV/episode differences and no Details-content focus or scroll on a real NVIDIA Shield. Synthetic mock-ups do not constitute device validation.
- Technical edge-case solutions belong to Codex; escalate only genuine product/design choices, not implementation details.
- **Do not merge or modify the active 0.139 branch.** Schedule this design in a later authorised release. The separate forensic audit is at `docs/details-tab-forensic-audit/README.md` on `docs/details-tab-forensic-audit-2026-10-13`, commit `89166c38dfbfcc205701e6aee33b254ad98b7f6d`, inspected 0.138 source `b3504c2c4b1725f3070416d14b63e53ae1db3d74`.
