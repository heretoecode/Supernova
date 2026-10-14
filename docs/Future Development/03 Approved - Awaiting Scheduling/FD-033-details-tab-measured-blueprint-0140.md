# FD-033 — measured reference blueprint and implementation handover (0.140)

**Status:** Design approved; blueprint geometry prepared for Codex; actual Android implementation and visual QA **not performed**. **Source:** Owner-supplied final *The Goonies* image, 1536×864 pixels, 14 October 2026. The source raster was measured locally; the raster itself has **not been committed to GitHub**. The companion [SVG geometry trace](FD-033-details-approved-reference-geometry.svg) is a tracing/guide **not a substitute for the source image**. Source SHA-256 to be recorded upon binary archival.

**Authority:** The final owner-supplied clean screenshot + explicit owner corrections in [approved specification](FD-033-details-tab-approved-design.md) and [decision ledger](FD-033-details-tab-design-decision-ledger.md). If the generated screenshot conflicts with written owner decisions, the written decision prevails. In particular, outer margins must be **4% of usable viewport** on left/right/bottom even though the raster visually measures about 2.2%/2.1%/3.5%. Do not infer that its pixel values are exact Android dp/sp values.

## Reference landmark register (image pixels; approximate ±3–8 px)
| Feature | Measured x/y or range in 1536×864 source | Derived image percentage | Status |
|---|---|---|---|
| Connected panel | x=34..1503, y=204..834 | x=2.21%..97.85%, y=23.61%..96.53% | Image landmark; **written 4% margins override** |
| Raised active Details tab | x=34..253, y=144..205 | x=2.21%..16.47%, y=16.67%..23.73% | Approximate tracing; border curves need implementation fitting |
| Upper row divider 1 | x≈590, y≈225..543 | x≈38.41% viewport | Measured |
| Upper row divider 2 | x≈1072, y≈225..543 | x≈69.79% viewport | Measured |
| Horizontal separator | x≈65..1478, y≈556 | y≈64.35% viewport | Measured |
| Lower row divider 1 | x≈539, y≈571..817 | x≈35.09% viewport | Measured |
| Lower row divider 2 | x≈1058, y≈571..817 | x≈68.88% viewport | Measured |
| Top-row content height | panel top 204 to separator 556 | 352 px (55.9% of panel height) | Approximate |
| Bottom-row content height | separator 556 to panel bottom 834 | 278 px (44.1% of panel height) | Approximate |

**Important:** The upper/lower dividers are intentionally **not aligned**: upper regions ~37.8% / 32.8% / 29.4% of panel width; lower regions ~34.4% / 35.3% / 30.3% of panel width. Percentages derive from pixel landmarks, are **approximate**, and must be reconciled with padding/gutters and written 4% viewport margins; do not copy the raw x coordinates into code as constants.

## Component register: reference observations versus Android design targets
| Component | Reference image observation | Implementation guidance (inferred; validate visually) |
|---|---|---|
| Content frame | One continuous dark-navy surface, fine pale outline, moderate radius | Single border/path connected to active tab; accent/focus colour is user-selected, white by default. Do not double-draw top border under active tab |
| Tab row | Details active raised tab, other three unboxed | Icon + label centred as a group; left/right D-pad changes active tab; no underline/glow/box on inactive tabs |
| Section headings | Bold white, no underline | Relative hierarchy must match screenshot; font family/size sampled only approximately from raster; choose sp via device screenshot comparison |
| Synopsis | Centre-aligned multi-line body, balanced wrapping | Full available synopsis where feasible; no independent scrolling; long text bounded readable fallback documented |
| Key Information | Two aligned columns, wide label/value gutter | All approved fields; missing values 'Unknown'; avoid metadata collisions |
| Reception | IMDb, Rotten Tomatoes, Metacritic, awards in four vertically stacked areas | Preserve slots, missing-value fallbacks; no invented scores; API and rights checks separate |
| Cast/Crew | **8 circular portraits each**, 4 columns × 2 rows | Fixed grid, names and roles beneath, generous heading-to-portrait separation; no extra items/View All |
| Technical | Label/value rows; path single line in approved written spec | Preserve filename with middle truncation if needed; screenshot's particular text is illustrative |
| Dividers | Thin, subdued vertical/horizontal strokes | Align to measured positions; separate upper and lower column grids |
| Padding | Approx panel inner inset 30–35 source px | Estimate only; measure visually and adapt with dp/insets; never call this owner-approved |
| Portrait diameter | Approx 50–57 source px | Estimate only; size by responsive layout, preserve circular crop and text legibility |
| Icons | Monochrome tab icons; Reception provider marks coloured | Reproduce reference treatment where legally and technically available; don't substitute arbitrary glyphs |

## Responsive constraints and acceptance
- 16:9 source image, **usable viewport** parent. Owner-specified left/right margin = **4% of viewport width**, bottom margin = **4% of viewport height**. Recompute inner layout proportionally after insets; retain overall composition at 1280×720, 1920×1080 and 3840×2160. Avoid naïve pixel multiplication for text; use Android dp/sp and measured readable bounds.
- Respect official title/logo at top left on scrolled Details view; hero synopsis, actions and metadata disappear. No extra top nav. Do not misread source image's 'SUPERNOVA' illustrative title as an instruction to replace the **selected film's official title art**.
- Preserve all six regions, non-focusable/non-scrollable Details content, 4×2 cast and crew, single-line path, user-accent border, no heading underlines, missing-data placeholders. The mock-up is illustrative, not factual metadata or casting evidence.
- Codex must compare **actual application screenshots** with the approved image, side-by-side and with visual difference overlays, on 720p/1080p/4K plus Shield where available. Log divergences, D-pad behaviour, custom focus colours, long text, absent data, episode/TV aggregation, and source licensing. Successful compilation alone is not visual acceptance.
- Owner-approved overall design; **exact font sizes, stroke widths, spacing and icon sizes have not been independently measured from an editable design**. Codex should calibrate against the raster and report final numeric Android metrics; avoid fake precision.
- **Release boundary:** FD-033 and FD-007 are authorised for planned 0.140, only after 0.139 acceptance; do not modify the ongoing 0.139 branch.

## Asset/archive caveat
The owner-provided 1536×864 JPEG remains available in the planning conversation and local preparation session, but **GitHub currently contains this SVG trace and text spec, not the original JPEG**. Binary archival is outstanding and must be resolved before claiming the reference asset is permanently reproducible from the repository alone.
