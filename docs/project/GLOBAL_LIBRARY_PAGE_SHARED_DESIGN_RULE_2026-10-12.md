# Supernova — Shared Library Page Design Rule

Status: agreed design/architecture requirement for next Codex handover; not evidence current code is fully shared, and not authorisation to implement immediately.

## Scope
Movies, TV Shows and the user-created third Library page are **configurations of one common Library page design**. They should share layout, spacing, placement, buttons/controls (including any Library-specific styling exceptions), focus outlines, D-pad navigation, focus restoration, artwork presentation, animations, context menus and applicable display features. Content source, filtering and page configuration may differ.

## Change propagation
When user reports a layout, placement, visual, focus, navigation or common feature defect on any one of these pages, interpret it as a candidate **shared Library page** issue. Fix shared components/styles/behaviour and validate all three page configurations; do not make a one-off page-specific patch unless a real configuration-specific distinction is established and documented. A requested design change to a common Library component should propagate to all three.

## Interaction with global UI standards
App-wide button/focus/typography tokens apply where appropriate. Library-specific button/control exceptions are permitted, but should themselves be shared across all three Library page types. Do not indiscriminately replace specialised controls with generic buttons.

## Validation
Codex should inspect whether these three pages truly use shared implementation; consolidate divergence as needed, without changing user-defined content. Test Movies, TV Shows and custom Library page for each shared change, including focus, D-pad, layout, context menu and rendering. Distinguish intended content differences from accidental visual/behaviour divergence.

No original QA images/videos to GitHub or Codex without user's explicit approval.

## Explicit user reinforcement — custom Library is not a separate design

The user reiterates that the **already-created, media-filled user custom Library page** must follow **exactly the same existing shared page design and applicable features as Movies and TV Shows**, including button locations, layout/alignment, artwork, typography, focus and D-pad behaviour, menus and animations. This is NOT a request about the custom page editor or Customise Home interface. Existing custom Library alignment/placement improvements remain insufficient. **Source implementation and approved design documentation of Movies/TV Shows are authoritative**, not approximations from user photos. Codex should audit actual page implementations and reuse/consolidate shared structures/components so future common changes propagate to all three; content sources, filtering, titles and genuinely contextual controls may differ. No photos required before source audit; request targeted visual evidence only for residual discrepancies. Preserve all existing features and avoid arbitrary page-specific patches.

## 0.137 user visual evidence and parity criteria (2026-10-12)

User supplied three photos (IMG_4900.jpeg Movies, IMG_4901.jpeg TV Shows, IMG_4902.jpeg custom Documentaries) and two short videos (IMG_4903.mp4 standard Library grid browsing; IMG_4904.mp4 custom Library browsing) **in conversation only**. Do not upload raw images/videos to GitHub or Codex without consent. Representative frames reviewed; video-wide frame-by-frame focus diagnosis still requires technical verification.

Movies/TV Shows reference: library heading and compact grouped stats, consistent Filter / Sort Date Added / Newest First / List View toolbar layout, divider, poster grid. Custom Documentaries diverges visibly: very compact/misaligned stats below title, separate Edit Library button on far right, Filter / Sort / Descending / List View controls differently sized/placed, grid spacing and poster/card treatment differences. User reports **zoom/focus animation mismatch** on custom page compared with standard library; treat as explicit QA reproduction target, do not infer root cause from photos alone. Preserve contextual Edit Library functionality while aligning it with shared layout conventions; do not delete it merely to make toolbar identical.

Acceptance: inspect and reuse shared source components/styles from Movies and TV Shows; identical common feature positions, geometry, text baseline/spacing, stats layout, card zoom/focus scale, clipping, animations, poster gaps, row spacing and D-pad traversal on all three page types. Differences only for genuine page-specific content/actions. Test focus movement through rows, edges and scrolling, including no clipped zoomed cards or abnormal under-poster indicators. Avoid recreating page independently from pictures.

## 0.137 custom Library Filters action and focus-return defect (user report, 2026-10-12)

On the already-created custom Library page, **Edit Library** correctly opens the five-step Edit Library guide. However, **Filters** also opens that same five-step Edit Library guide instead of an expected filter interface. Audit whether this is an unintended handler/routing reuse; preserve the intended Filters semantics as established by the standard Movies/TV Shows Library pages and the custom page's legitimate filter options. Do not presume root cause or delete Edit Library.

When the user opens that guide **from Filters** and presses Back, D-pad focus returns to **Edit Library** rather than the invoking **Filters** button. Fix focus restoration to the exact invoking control when still present/enabled, including Back, Cancel, completion and relevant re-entry paths. Test both entry points separately, with standard and custom Library configurations, and avoid new focus jumps. This is an **explicit 0.138 regression requirement**; do not assume the global shared Library page refactor will incidentally fix it. No user media required to start source investigation.
