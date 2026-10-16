# FD-033 — Blueprint v3 approval notes, visual QA and 0.140 implementation scope

**Recorded:** 14 October 2026. **Owner decision:** The revised annotated v3 blueprint is visually much improved and approved to be included in the **0.140 implementation scope**; owner may still provide further visual corrections. Do not misstate this as final pixel-perfect approval or completed implementation. Earlier planning documents used **0.140**; owner confirms the target is **0.140**. Codex must verify the project's versioning convention before changing release identifiers; do not silently rename the in-progress 0.139 release.

## Source-of-truth order
1. Original owner-selected clean 1536×864 *The Goonies* reference screenshot (local conversation asset, not yet archived in GitHub).
2. Owner corrections in this document and the approved design decision ledger.
3. Revised **FD-033 component blueprint v3** (local 2400×1680 annotated PNG, not yet archived in GitHub).
4. [Measured geometry/component register](FD-033-details-tab-measured-blueprint-0140.md) and [SVG guide](FD-033-details-approved-reference-geometry.svg). Earlier v1/v2 guides are superseded where inconsistent.

**Do not use the SVG guide as the approved visual mock-up.** The clean image and v3 PNG remain binary-archival dependencies; repository documentation is text-only until those are committed.

## Owner corrections (non-negotiable)
- Active **Details** tab and panel must be a **single continuous dark surface with one continuous external accent/white outline**. No line beneath the Details tab, no duplicated panel-top stroke, no underline. Other tabs unboxed.
- All **six content regions** must match reference positions, not just their bounding boxes: upper Description, Key Information, Reception; lower Cast, Crew, Technical Information. Upper and lower column dividers are intentionally offset.
- Reception: **IMDb, Rotten Tomatoes, Metacritic, Awards** stacked in that order, with **three subtle horizontal separator rules** between adjacent rows. Reference-image approximate separator y-coordinates **325, 397, 471**, on the 1536×864 source; these are guides, not dp. Provider icon column x≈1105–1163; rating/value text starts x≈1198. Four grouped rows about **65–76 source px high**, awards may wrap. Keep provider icon aspect ratios, text baselines, line height, inset and separators consistent.
- Metadata: Key Information labels x≈618, values x≈824, baseline step ≈27–28 source px. Technical labels x≈1082, values x≈1207, aligned baselines, **path on one line**.
- Cast and Crew: **4 columns × 2 rows each**, round portraits approximately **50–55 source px diameter**, name above role, centred in cell. Preserve row/column spacing and no View All.
- Written **4% viewport left/right/bottom margins override narrower source-image margins**. Responsive layout must accommodate 720p, 1080p, 4K and Shield; exact Android sp/dp need screenshot calibration.

## v3 reference-derived typography/icon sizing register
**All measurements are estimates in the 1536×864 source-image pixel coordinate system, NOT exact Android sp/dp.** The v3 artwork labels these provisional, and that uncertainty must remain visible to Codex.

| Element | Approx source px | Style and spacing |
|---|---:|---|
| Section headings | 23–25 | Bold; upper heading baseline ~249, lower ~593; no underlines |
| Synopsis body | 20–22 | Regular, centred; ~33px line-baseline interval |
| Metadata labels and values | 15–17 | Regular; ~27–28px row interval |
| Reception primary scores | 21–24 | Bold numeric score; smaller denominator/unit beside it |
| Reception supporting text | 14–16 | ~17–20px line height |
| Reception provider icons | 48–55 | Approx bounding size; preserve provider-specific proportions |
| Cast/Crew portraits | 50–55 | Circular; four by two in each section |
| Cast/Crew names and roles | 13–15 | Name above role, centre-aligned |
| Technical labels and values | 15–17 | Seven aligned rows, path one line |
| Tab icon | 28–33 | Centred together with label |
| Tab label | 17–19 | No underline beneath active Details |
| Focus border | 1–2 | One continuous perimeter |
| Internal dividers | ~1 | Thin and muted, including Reception separators |

## Behaviour and acceptance
- All details content read-only and not independently focusable/scrollable. D-pad tab focus/selection remains correct. Accent-colour preference controls the connected outline (white default).
- Missing metadata uses the existing approved fallbacks; never invent scores, awards, provider rights, portraits or data. Reception data sourcing and licensing must be validated separately before live API implementation.
- Real device screenshots must be compared with the clean owner-approved reference and v3 blueprint at 720p, 1080p and 4K where supported. Inspect connected border, Reception **three rules**, provider icons, all font sizes, metadata columns, portrait grid, path overflow, long descriptions, TV cases and focus navigation. Record numerical final dp/sp, deviations and evidence. A successful build is **not** visual acceptance.
- Implement in **0.140 after completion/acceptance of the ongoing 0.139 work**. Never edit, reset, replace or merge the active 0.139 Codex branch merely to begin FD-033. Engineering decisions belong to Codex within the approved visual constraints.

## Remaining archival / verification tasks
- [ ] Commit the **original clean 1536×864 image** to GitHub as a binary reference.
- [ ] Commit the **2400×1680 v3 annotated PNG** to GitHub as a binary blueprint.
- [x] Commit the text specification, explicit Reception separators and provisional sizing register to GitHub.
- [ ] Codex implementation and visual QA on actual target devices.
