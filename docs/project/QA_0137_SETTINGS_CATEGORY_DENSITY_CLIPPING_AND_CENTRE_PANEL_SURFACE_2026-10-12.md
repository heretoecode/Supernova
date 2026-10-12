# 0.137 Shield QA — Settings navigation density, clipping and centre panel surface

**Status:** User-reported observations and design direction, documented for 0.138 scope reconciliation; **not yet independently verified by images/video/source**. No code changed.

## Confirmed by user's Shield observations
- Settings initially focuses topmost left-panel item; moving Up reaches Home; Down traverses categories correctly. All settings categories are now accessible; preserve this improvement.
- User estimates about 13 left-panel category buttons, only ~8 visible simultaneously. Individual category buttons (e.g. Playback, Video) seem unnecessarily tall.
- Bottom About category/button is visibly **partially clipped** in user's current layout. Needs independent layout-boundary correction; don't assume density adjustment alone fixes it.
- Centre Settings panels currently have an extra dark **inner inlay/background around options** that left/right panels lack. User explicitly requests **complete removal** of this inner background and a consistent **single translucent dark blue-black panel surface** across the Settings page. Preserve necessary controls, text and their own borders/focus affordances; avoid decorative extra colour layers/structures.

## Desired design and technical audit
1. Audit actual Settings category count, layout dimensions, focus/scroll containers, TV resolution, font scaling and clipping.
2. Compact left category item heights, vertical padding and inter-item gaps **without making labels/focus targets cramped**. Aim for all categories visible at once on a standard Shield/16:9 layout **when feasible**; do not hard-code 13 at expense of readability. On smaller screens/larger accessibility text, allow scrolling with fully visible focused first/last items.
3. Fix About clipping independently; validate focus and scroll-to-visible, bottom padding, overscan/safe-area handling and different display sizes.
4. Remove extra centre panel inlay, not the base panel or control affordances. Use consistent single transparent dark blue-black panel background across left/centre/right where appropriate; share styling rather than page-specific colour patches.
5. Compare user-supplied photos/video **when available** before final pixel-level implementation. Do not upload user's raw screenshots/video to GitHub/Codex without permission.
6. Test D-pad traversal from Settings initial item, Up to Home, Down through all categories, return/focus restoration, focused About visibility, and controls on different panels.

## Planning
User's current product direction is incremental app-wide polish, consistency, layout refinement and issue fixes rather than an expanding backlog of major features. The user approved the complete previously enumerated 0.138 scope; **these Settings observations arrived afterwards**. Include them as additional proposed 0.138 QA/design work pending explicit scope confirmation if necessary, without displacing already-approved work.
