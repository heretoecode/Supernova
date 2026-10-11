# Supernova — Global Responsive Layout Standard

**Date:** 2026-10-11
**Status:** User-approved global design direction; proposed engineering calibration targets. Documentation only: no app implementation or active Codex branch modification authorised.

## Goal
Preserve the approved Supernova UI's relative composition, spacing, typography, artwork proportions, focus treatment and usability across different **logical Android TV viewports**, display scaling settings, aspect ratios, text scale and safe-area insets. Eliminate inconsistent hardcoded sizing by using shared, bounded responsive tokens and per-component layout contracts.

## Crucial distinction: physical resolution is not layout size
Do not scale directly from raw 720p/1080p/4K pixel counts or TV diagonal. Android density and display scaling may present the same logical dp viewport at different physical resolutions. Use measured available logical dp width and height **after system insets, safe margins and overscan allowances**. Maintain comparable *normalised* proportions, not necessarily doubled physical pixel dimensions.

## Shared layout engine and design tokens
- Establish one design viewport and central tokens for horizontal/vertical safe margins, column gutters, grid gaps, poster/card widths, row heights, corner radii, icon sizes, focus expansion clearance, typography and dialog dimensions.
- Derive responsive dimensions from safe viewport width and height, with **clamped minimums and maximums**. Percentages are targets, not absolute requirements that override readability.
- Use consistent spacing rhythm, based on an 8dp baseline and bounded proportional scale. Respect fontScale and accessibility; never shrink labels to illegibility simply to fit.
- Layout constraints must account for focus enlargement and glowing borders without clipping adjacent rows, columns or screen edges.
- Prefer reflow, fewer visible columns/cards, adaptive truncation and scrolling over squeezing every element. Do not arbitrarily change approved content hierarchy or typography.
- Preserve focus target minimums, predictable D-pad movement and focus restoration. Responsive layout is **not a substitute** for separate focus/navigation fixes.
- The same component (e.g. card, row, panel, dialog) must use the same token contract everywhere; do not hardcode per-screen workarounds.

## Component contracts
1. **Top navigation:** stable safe-area positioning, consistent relative horizontal gaps, brand scale, selected/focused state and right-aligned actions; avoid crowding on narrow logical widths.
2. **Home hero:** preserve approved aspect ratio, focal artwork treatment, card proportions, info placement, animations, and safe focus expansion; do not centre-crop posters destructively.
3. **Home media rows:** adaptive visible card count, consistent card aspect ratios and gutters; reserve enough vertical focus expansion space to prevent row clipping; use coherent vertical rhythm.
4. **Movies and TV grids/lists:** responsive column count and metadata density; preserve title legibility and focus effects, don't compress fixed numbers of columns.
5. **Details pages:** adaptive artwork/info columns, typography hierarchy, cast/crew and metadata spacing; keep focus targets visible.
6. **Network & Files:** responsive three-column folder browser; use flexible width ratios **only while all columns remain usable**. On constrained logical viewports, use an alternate navigation/reflow pattern instead of squeezing columns. Preserve text truncation, visible folder selections and shared D-pad focus logic.
7. **Settings:** responsive menu/panel widths, safe gutters, row heights and focus outlines, avoiding oversized empty panels or clipped options.
8. **Dialogs:** use [Global Dialog Design Standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md), including viewport-proportional bounded width, padding, typography and accessible actions.
9. **Playback HUD:** scale and position controls within safe areas; preserve interaction hit/focus targets, readable metadata and seekbar proportions without overgrowing on large viewports.
10. **First-run Home placeholders:** use **the exact same geometry and responsive tokens** as genuine Home cards/rows, not a separate layout. Retain focus effects, no fake media, no playback. See [First-Run Placeholder Behaviour](SUPERNOVA_FIRST_RUN_HOME_PLACEHOLDER_BEHAVIOUR_2026-10-11.md).

## Responsive conformance and testing
- Audit existing UI components and classify issues as **sizing/layout**, **focus navigation**, **content/data** or **animation**; do not mark focus bugs fixed merely because layout was adjusted.
- Measure at representative 720p, 1080p, 4K output settings and varying Android display scaling/fontScale. Include narrow safe-area and longer-text cases. Check actual logical dp metrics, not just screenshot pixel dimensions.
- Compare screenshots with the existing approved Supernova designs using **normalised ratios**: margins/viewport, component widths/viewport, poster/card aspect ratio, inter-card spacing/card width, text hierarchy and focus expansion clearance.
- Assert no horizontal/vertical clipping, overlapping focus halos, off-screen action buttons, illegible text or compressed browser columns. Test D-pad focus, restoration and scroll after adaptive reflow.
- Fix **shared components/tokens first**, then verify affected screens. Document before/after metrics and Shield QA screenshots; no unverified claims that UI issues are fixed.
- Apply changes incrementally with visual regression checks; avoid wholesale visual redesign and preserve approved mockups, branding, signing and app identity.

## Delivery boundaries
This document defines a **global standard for future implementation**. It does not authorise modifying the active Codex development branch, initiating a new release, or changing the approved look. Codex must audit the existing UI and propose concrete token values and affected components before implementation, then validate on device.

Related: [Global Dialog Design Standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md), [Parked Discussions](PARKED_OUTSTANDING_PRODUCT_DISCUSSIONS_2026-10-11.md).
