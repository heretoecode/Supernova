# Supernova 0.138 Shield QA walkthrough — working findings and shared UI contracts
Date: 2026-10-12. Status: owner-reported runtime observations and owner-approved design intentions; not source-verified or implemented. Proposed 0.139 scope pending owner handover. Keep Codex's maintenance investigation isolated and unchanged.

## Terminology and shared foundation
“Third Library” means a user-created Library configured via the five-step Edit Library flow. Movies, TV Shows, and every custom Library must use the same **Library View Foundation** for header/stats layout, controls, grid, focus graph, visual focus, spacing and animations. The custom Library additionally offers Edit Library. One shared change must affect all Library types; validate implementation centralisation rather than assume it.

## Library 0.138 verified by owner
- Custom Library appearance, header quantities/storage, controls, media positioning, zoom and focus animation now visually match Movies/TV Shows.
- Custom Library Filters opens view filters, not Edit Library (previous defect resolved).
- Movies controls Left/Right bounded correctly; no wrap after rightmost control.
- These are observed visual/runtime results, not proof of code inheritance.

## Shared Library focus contract — required
- Selecting a Library tab loads its page **without moving focus** from the selected top navigation tab. No automatic descent.
- Down from top navigation focuses the **leftmost currently available control**, regardless of its identity or last-used focus.
- Up from **any** Library control returns to the selected Library tab.
- Down from **any** Library control focuses the **first (leftmost) media item below the divider in the currently displayed filtered/sorted grid**, not the geometrically aligned item under that control or a fixed title.
- Left/Right moves through controls in their visual order without unintended wrap.
- Returning from controls to tab then Down starts at leftmost control again.
- Focus must remain visible on the active tab until deliberate navigation into page. Back-navigation should never leave an invisible focus state.
- Current third-Library defects: selecting tab jumps focus to Filters; Up from controls cannot reach tab; Back forced to exit and can restore prior page with no visible focus. Movies controls currently descend to different grid columns according to horizontal control position. Investigate/fix shared focus routing and verify all three.

## Shared Library control sizing — owner-approved design
- Keep current icon and label sizes and icon-to-label spacing.
- Reduce oversized visible container height and notably reduce left/right internal padding; width should fit content.
- All labels on ONE line without wrap or unnecessary truncation; long Sort label receives naturally wider button.
- Equal fixed gaps between adjacent controls, left-anchored packed row; when widths shrink, subsequent controls move left (do not retain original button x-positions).
- Consistent border/radius/focus appearance; focus must not shift neighbour positions.
- Apply to all Libraries including Edit Library control; retain usable D-pad interaction target. Previous generated ~56px heights/widths were illustrative, not implementation measurements. No full-screen generated mockup should override approved existing UI.

## Home hero — 0.138 findings and design
- Manual single-tap Left/Right works; holding Left/Right on focused hero also scrolls Recently Added underneath (defect). Focus remains hero.
- Automatic hero carousel transition differs visibly from preferred manual transition; make automatic match manual motion/timing/easing without focus theft.
- Hero has very large library-like selection; owner favours 10 active titles, mixed Movies/TV, sensible rotation without constant changes; policy to finalise.
- Official transparent title logos apparently absent; investigate. Fallback title lower, appropriate size, one line.
- Hero metadata one line: **Rated 15** (etc.) badge at left, year, max 2–3 genres subject to space, TMDB score. **Remove vote count from hero**. Age badge properly centred/padded.
- Tagline below metadata with modest but distinct vertical gap; same font family, size, weight as metadata; slightly reduce current metadata font size; no script font.
- Remove technical pills from hero only, preserve on approved other surfaces.
- Artwork fidelity: no Supernova-applied darkening, grading or bottom gradients across Home hero and media row images; retain original colours. Localised text shadow/contrast treatment okay. Investigate whether current dark bottom is overlay vs intrinsic art.
- Continue Watching absent despite resume state visible in Dune Details; investigate independently.

## Critical playback failure
- Dune: Part Two and Tulsa King attempted ~13:30–13:31 Dublin time; “Preparing playback” then returned to Details, playback never starts. Owner called crash; process crash not verified. Release-blocking candidate, root cause unknown. Await diagnostic ZIP **at end of walkthrough**, do not repeatedly request it or interrupt QA. Preserve user data and update path.

## Release/process boundaries
- 0.138 signed QA installed over 0.137; onboarding deferred until future clean-install test.
- Codex ongoing read-only comprehensive maintenance investigation on isolated docs branch; do not alter it or start 0.139 implementation until owner authorises.
- 0.139 provisional combined fixes and expanded diagnostics, then smoke test, then separate maintenance implementation on stable baseline.
- This file records conversation-derived requirements; not a claim that fixes, APK, diagnostics, tests, source investigation, or screenshots were committed. Confirm implementation with source and Shield QA before marking fixed.
