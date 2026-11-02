# Playback Settings — Subtitles side panel (APPROVED 2 November 2026)

**Status:** APPROVED by the owner (design only; not yet implemented). Target: 0.152.
**See also:** the complete three-tab design in [PLAYBACK_SETTINGS_SIDE_PANEL_APPROVED_2026-11-02.md](PLAYBACK_SETTINGS_SIDE_PANEL_APPROVED_2026-11-02.md), which supersedes this file where they differ (Subtitle Track stepper, three tabs, in-panel lists).
**Supersedes:** the *size and position* of the centred "Image B" Subtitles panel (`docs/design/2026-11-01-subtitles-implementation-blueprint.md` on `main`). Row content, order, icons, controls and colours from Image B are **kept**.

**Approved image:** [subtitles-side-panel-approved-2026-11-02.png](approved-mockups/playback-settings/subtitles-side-panel-approved-2026-11-02.png) — rendered at 1920×1080 by [render-subtitles-side-panel.js](approved-mockups/playback-settings/render-subtitles-side-panel.js). The film scene and subtitle line are illustrative placeholders, not product content.

## Why
On the Shield (owner photo, 2 Nov 2026) the centred panel covered ~60% of the width and ~85% of the height, hiding the picture whose subtitles are being adjusted.

## Layout (1920×1080 logical; scale with dp for other resolutions)
- Panel docked **top-right**: 40px from the top and right edges; **640px wide** (one third of the screen). **Height fits its rows** (owner, 2 Nov 2026): no empty space below Subtitle Delay, but not cramped — ~26–30px inner padding top and bottom, as drawn. Rounded corners ~26px, existing cyan outline and glow, existing navy panel colours.
- A soft dark gradient (~90px) on the panel's left edge separates it from the video; **no** full-screen dim. Video keeps playing and stays visible on the left two thirds.
- Header: gear icon + "Playback Settings". Tabs in one row of four equal widths: Subtitles · Audio · Video · More (active tab filled blue with glow, as today).
- Section headings unchanged: SUBTITLE SELECTION, SUBTITLE APPEARANCE.
- Rows ~60px high, icon column ~44px, label left, value right.
- Separators are **straight, square-ended 1.5px lines** under each row (owner check, 2 Nov 2026: no curved ends).
- Focus = glowing outline with **rounded corners** around the whole row (as Text Colour in the image); only the focus outline is rounded.

## Rows (order and controls unchanged except where noted)
1. Subtitles — toggle
2. Subtitle Track — language flag + name + ›
3. Download Subtitles — "Find & Download" + ›
4. Text Size — Small | Medium | Large
5. Text Colour — five swatches, selected ringed
6. Vertical Position — Bottom | Middle | Top
7. Outline — toggle
8. Background — toggle
9. **Background Opacity — stepper `‹ 100% ›`** *(changed: owner chose the stepper so the label and value have room; matches Subtitle Delay)*. Left/Right steps 0 → 25 → 50 → 75 → 100%. **Dimmed (~40% opacity) while Background is off**, since it has no effect then.
10. Subtitle Delay — `− 0 ms +` (100 ms steps, unchanged)

## Behaviour
- Changes apply live; the subtitle preview remains visible on the left while adjusting.
- Remote: Up/Down moves between rows; Left/Right changes the focused row's value (or moves between tabs on the tab row); OK toggles switches and opens Track/Download; Back closes the panel and restores HUD focus.
- Toggles use the shared 44×26dp switch face (0.151).

## Decided (2 Nov 2026)
- **Panel height:** fitted to the rows (owner).
- **Subtitles while the panel is open:** **not shifted** (owner delegated; decision recorded here). Subtitles stay exactly where the renderer normally draws them, so they never jump when the panel opens or closes, and the subtitle engine is untouched. Because the fitted panel ends above the usual bottom subtitle area, a normal bottom subtitle stays fully visible beneath it (as drawn). Very long two-line subtitles, or Middle/Top positions, may run under the panel's left edge while it is open; accepted.

## Still to confirm before implementation
- Other tabs: approved — see the complete design file.

## Acceptance (Shield)
Owner photo on the Shield matching this layout; picture visible on the left; Background Opacity stepper works and dims when Background is off; all ten rows reachable and working with the remote; no regression in subtitle rendering.
