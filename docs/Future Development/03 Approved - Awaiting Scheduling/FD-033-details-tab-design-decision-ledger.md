# FD-033 — Complete owner decision ledger and proportional design handover
**Captured:** 13 October 2026. **Status:** Approved visual/behavioural design; future-release scheduling and implementation are **not authorised**. **Canonical companion:** [FD-033 specification](FD-033-details-tab-approved-design.md).

This ledger preserves **all substantive owner-confirmed details and revisions from the Details-tab mock-up discussion**. Later corrections override earlier experiments. Do **not** interpret illustrative mock-up data or imagery as an instruction to change real media metadata. The *approved* reference is the **last clean, pre-layered-glass mock-up**, showing *The Goonies*, a centred icon+Details label, a single continuous white border around the active tab and panel, no extra inner cards, and no section-heading underlines. The **subsequent six individual blue/glass background panels were explicitly rejected**. The approved image is present in the original planning conversation but is **not yet committed as a binary asset in this branch**; avoid claiming pixel-exact geometry has already been extracted.

## 1. Screen context and navigation — confirmed
1. Full image/mock-up uses a **16:9 television screen**, but the Details tabs **do not occupy the entire screen**. This is the lower portion of the existing media Details page, reached by scrolling down from its hero.
2. On entry to lower tabs, the hero's **Play/other playback controls, overflow (three-dot) menu, hero description and metadata row disappear**; **official title typography/logo remains at the upper left**. Leave clear space above the tab row for that logo. Do not add an app top-navigation bar or unrelated movie information.
3. The tab sequence is **Details**, **Extras**, **Seasons & Episodes**, **More Like This**. Use monochrome, familiar icons alongside single-line labels. Tabs irrelevant to the media type should follow existing app behaviour (e.g. no invented season content for a film).
4. **Only the active tab** is enclosed by a raised tab-shaped extension of the panel's **single continuous focus border**. Inactive tab labels/icons **float unboxed** on the background: no rectangles, pills, borders, active-style shadows or underlines.
5. The active tab icon and label must be **centred together** horizontally and vertically inside its tab shape; do not drift to the right.
6. One clean, thin outline rises **once** from the content frame, rounds over the focused tab, descends once, then runs across the rest of the content panel and around its sides and bottom. The focused tab and panel are **one connected shape**, **not two adjacent boxes**. **No horizontal line directly below the active Details label**, no blue glow/bar beneath it, no duplicate border up the left edge.
7. Standard focus border is **white**, but it **must use the user's selected Supernova accent/focus colour**. Other inactive labels remain undecorated. Corner curvature should be **moderate and restrained**, less rounded than earlier mock-ups; not sharp/square.
8. The remote **Left/Right** changes tab focus; the focused heading controls which panel is displayed. **Nothing within the Details panel can be selected or focused**. The focus stays on the tab heading; do not add interactive arrows, View All or read-more controls. Other tabs may have their own interactions, to be specified separately.
9. Existing hero playback actions must remain as they are when the hero is visible. No **My List** button or other invented control.

## 2. Panel geometry and visual hierarchy — confirmed
1. Screen reference is **100% viewport width × 100% viewport height**. Left/right outer margin = **4% of viewport width each**; bottom margin = **4% of viewport height**. Measure the top position from the *approved* reference so the retained official title typography fits.
2. The panel is a single clean **dark-navy Supernova surface**, with existing styling and translucent/background treatment, **not** six individual blue rounded boxes. Do not add gradients, ship artwork or illustration behind the synopsis, or new ornamental backgrounds merely to create depth.
3. Primary **upper row**, in order: **Description | Key Information | Reception**. Supporting **lower row**, in order: **Cast | Crew | Technical Information**. All six remain visible together, in the same fixed composition.
4. Description, Key Information and Reception are visually primary. Cast/Crew are secondary and Technical Information last. Maintain understated, fine column dividers and a horizontal upper/lower separator. The horizontal separator below Reception was **lowered slightly** to allow its awards area to breathe; lower sections may lose some height but still must fit their full required content.
5. Section headings are **Description, Key Information, Reception, Cast, Crew, Technical Information**. Use clear bold/strong typographic hierarchy. **No underlines whatsoever on any section heading**, no arbitrary accent strokes, no full-width underline and no underlined technical values. Earlier trials of heading underlines were explicitly discarded. No numeric labels such as **Cast (8)** or **Crew (8)**.
6. Preserve comfortable internal padding and, especially, **more vertical space between Cast/Crew headings and their first portrait row**.
7. Layout must be **read-only and non-scrolling** within the Details panel, both vertically and horizontally. Do not add inner focusable widgets.
8. Keep official app fonts, established icon design, colours and spacing. The screen's overall colour is dark/navy; do not invent an unrelated navigation bar or styling system.

## 3. Description — confirmed
1. Body copy is **centred within its section**, not pinned to the upper-left. Use the entire allocated region naturally, including vertical balance and comfortable line lengths.
2. The owner requested a larger, more legible synopsis than early iterations, then specifically **reduced it slightly** after one oversized version. Use the **final approved mock-up's medium-large type**, not the biggest experiment.
3. Improve line spacing and balanced wrapping. The word **Description** remains a normal section heading; these typography adjustments apply to **body text**, not the heading.
4. Display the full available synopsis where it fits; the hero's preview is line-limited but the Details panel is intended to show more. Exceptionally long synopses are an engineering fit problem, not an invitation to redesign or scroll. **Owner has not yet decided between minimum-size text reduction and ellipsis**; previous assistant recommended modest size reduction first, truncation only if necessary. Do not mark that preference as confirmed.
5. Do not place an illustrative pirate-ship illustration or extra background artwork behind the synopsis (that appeared only in a later rejected visual experiment).

## 4. Key Information — confirmed
1. Keep **Original Title, Also Known As/Alternative Names, Release Date, Runtime, Genres, Country, Language, Production Company, Budget and Revenue** and any existing relevant key information subject to available space.
2. Preserve a two-column **label : value** presentation, with the gap between label and value **at least approximately doubled** relative to the earlier cramped design. Example of the specific concern: Production Company vs Amblin Entertainment.
3. If a data point is missing, show **Unknown**. Avoid blanks, broken layouts or placeholders that change the section's structure.
4. Handle long alternative names, genres and company names within the fixed region. Don't confuse official age certification with a US-only OMDb `Rated` value.

## 5. Reception — confirmed
1. This is an **existing Details-tab Reception section**, not a new tab. Preserve its location in the **upper-right** and expand the information it contains.
2. Four **always-visible slots**: **IMDb audience rating (and vote count)**; **Rotten Tomatoes score**; **Metacritic/Metascore**; **Awards/Recognition**.
3. Show ratings and relevant supporting context where supplied. **If a rating is missing, retain its label/slot and show “No rating available”**. If awards data is missing, keep the section and show **“Unavailable”**. Never silently collapse the entire slot.
4. Awards should show **win and nomination totals**, **named achievements**, the awards/recognition received (e.g. named major award when supported), not merely a total number. Give this block room so its descriptive lines aren't crowded against the horizontal lower divider.
5. No click arrow, expansion affordance or selectable rating row. All information appears **inside Reception**, read-only.
6. Supplementary OMDb IMDb/Rotten Tomatoes/Metacritic data is **owner-approved in principle**; TMDb/current providers remain primary. OMDb access/terms, legal suitability, coverage, cache, matching and request limits remain Codex's implementation/validation responsibilities. Do not claim the API was live-tested with credentials. Direct licensed IMDb dataset integration was not approved and was previously ruled out due to licensing concerns.
7. Age classification and descriptive content advisories are **separate concepts**. User-country official age rating should not be replaced by OMDb's US-centric `Rated`; any universal advisory wording uses its own source/handling and is not automatically a new Reception control.

## 6. Cast and Crew — confirmed
1. Exactly the **first/up to eight available cast entries and eight crew entries**, each in a **4 columns × 2 rows** visual grid. All available eight in each must fit simultaneously without scrolling.
2. Each person: small **circular portrait**, **name**, and **character** (cast) or **job/credit** (crew) below. Keep the typography legible and spacing even.
3. No **View All**, no person-count suffix on headings, no extra entries, no repeated/duplicated names or unnecessary focus/click affordances.
4. Earlier image generations produced one-row cast, too many crew entries, duplicates and crowded heading/portraits; those were explicitly rejected. Restore the **four-by-two** grid and adequate header-to-portrait gap.
5. Synthetic sample people and their likenesses are not verified data and are not an implementation fixture.

## 7. Technical Information — confirmed
1. Keep the lower-right technical section, with video/audio/container/size/source/date-added/path and other relevant existing details.
2. The **file path must be a single line at all times**; never wrap onto a second line. Prefer middle truncation that preserves the filename when necessary. Avoid accidental underlines on the date or any technical value.
3. Correct technical data should correspond to the media context. The 0.138 forensic audit notes that TV details currently aggregate indexed episodes and storage; episode metadata refresh and season credit/video gateway issues need engineering validation. Do not present those findings as undecided visual design questions.

## 8. Proportional, cross-resolution specification — confirmed direction, measurements pending
1. **Measure the last approved original mock-up**, not an invented illustration or rejected layered-glass mock-up. Derive numeric **x/y/width/height** for the overall panel, active tab, all six sections, internal gutters, heading positions, upper/lower row split, divider positions, portrait grids and text blocks.
2. Express screen-relative positions in **% of usable viewport** and internal geometry in **% of parent region**, documenting which denominator is used. Record tab geometry, border thickness, corner radii, icon/label spacing, typography hierarchy, line heights, and horizontal/vertical padding.
3. Preserve the **same composition** at **1280×720, 1920×1080, 3840×2160**, and different physical television sizes. Resolution, Android density, overscan and font scaling must be handled properly; use dp/sp and bounded readable type rather than blindly multiplying pixel sizes.
4. The **4% side and bottom margins are actual owner-specified numerical values**. **All other precise percentages are not yet measured or owner-approved**; do not fabricate or present approximations as exact.
5. Codex should deliver numeric layout tables, reference overlays/visual diffs, and tests at the three resolutions plus Shield where possible. Text overflow and missing-data cases need explicit QA. Do not claim screenshot equivalence until measured and verified.

## 9. Broader tab/page discussions — preserve without conflating
- **Extras:** Existing Home-style rows, focus and spacing conventions; streaming-service logos transparent, monochrome and unboxed. The Details panel's non-interactivity applies **only to Details**, not to playable extras.
- **Seasons & Episodes:** One TV-show Details page, season headings and episode rows styled consistently with Home, only locally available episodes shown; no separate season pages. Preserve season/episode selection and scroll position when returning.
- **More Like This:** Included in the agreed tab sequence, but the **exact recommendation sources/content and row behaviour remain less fully defined**. Do not invent these from the Details screenshot.
- Hero-card metadata, playback actions and upper-card design are outside this lower-tab mock-up and should remain intact except for the agreed disappear-on-scroll behaviour.

## 10. Investigation and implementation boundaries
- The isolated forensic audit at [commit 89166c3](https://github.com/heretoecode/Supernova/commit/89166c38dfbfcc205701e6aee33b254ad98b7f6d) examined **signed 0.138 source** `b3504c2c4b1725f3070416d14b63e53ae1db3d74` and documented **44 lower fact labels**, page/variant behaviour and test gaps. It found existing information rows reject focus but the page scrolls; no full lower-tab synopsis; TV aggregate technical data; unattached Versions; season credits/videos gateway limitations; potentially delayed episode technical updates. These findings are **source-audit evidence**, not proof the new mock-up was implemented or Shield-validated.
- Do not merge the forensic audit or this documentation into the active **0.139** branch without explicit owner approval. The current 0.139 Codex run is separate and should continue undisturbed.
- This document records decisions for future discussion/implementation. No APK, code change, merge, or release assignment is authorised.

## 11. Chronological correction history (latest wins)
1. Initial tab redesign: monochrome icons, tab order, hero logo retained on scroll, full-width lower panel.
2. Owner requested **all eight cast and crew**, no View All, no selectable Details content; Reception has no expansion arrows.
3. Owner set hierarchy: **Description + Key Information + Reception** above **Cast + Crew + Technical**.
4. Owner added **alternative names, budget, revenue**, **Unknown** fallback, fixed rating/awards slots and **single-line path**.
5. Description was moved from upper-left to **centred**, enlarged, then **slightly reduced** to the final approved size. Key label/value gap widened.
6. Reception's lower separator moved down slightly; Cast/Crew headings lost count suffixes; portraits spaced farther below headings.
7. Section-heading underline experiments (full-width, short accent strokes, text-width underlines) were **all rejected**. **No underlines**.
8. Active tab focus evolved from separate boxed labels to **one connected, continuous thin user-accent outline** around the entire tab+panel; inactive tabs **floating/unboxed**; no line/glow beneath Details; tab corner radius reduced; Details icon+text **centred**.
9. Owner approved the clean final mock-up, requested its GitHub documentation and a **resolution-independent proportional specification**.
10. A later attempt at **six individually shaded/rounded layered-glass panels**, including synopsis artwork, was **rejected**. **Do not use that image** as reference. Return to the previously approved clean design.

## 12. Genuine unresolved items (do not reopen settled design)
- **Numeric measurements** of the final approved image have not yet been extracted. This is an execution task for Codex/design tooling, not a new owner preference.
- **Exceptional-length synopsis overflow**: user has not yet explicitly chosen the final fallback. Earlier assistant recommended reducing body font within a safe minimum, then ellipsis if necessary. Seek approval only if the actual case cannot be resolved within established readability standards.
- **More Like This** recommendation behaviour/content is not fully settled, separate from this approved Details panel.
- **Other tab interactions** beyond their established broad rules may need separate design approval. No reason to block the already approved Details layout.
- **Original image asset** is not yet archived as a GitHub binary on this documentation branch; the conversational visual reference is authoritative until that is done.
