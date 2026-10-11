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
