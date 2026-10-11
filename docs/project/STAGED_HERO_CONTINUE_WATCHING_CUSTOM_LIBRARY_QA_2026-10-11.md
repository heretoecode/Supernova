# Next-version Shield QA additions — Hero, Continue Watching and Custom Library Pages

**Recorded:** 2026-10-11
**Status:** User-requested additions for the next Codex handover. Documentation only; do not start implementation, merge or create a release.

## Scope principle
Keep next version focused on interface consistency, navigation reliability and first-run experience. Investigate functional regressions and implement shared components rather than cosmetic per-page patches.

## 1. Hero carousel smoothness and loading
**User observed:** Transitions between hero cards are insufficiently smooth. Some card information (notably typography) visibly appears after the card is shown.

**Requirements:**
- Drastically smooth transitions between hero cards; preserve approved visual design, focus and interaction.
- Prioritise a **three-card ready window**: currently centred hero, immediate previous and immediate next. Prepare their artwork and **all visible metadata** (title, year, genres, runtime/seasons, tagline, button states) and final text layout ahead of the transition where possible.
- Prefetch and cache remaining carousel items using bounded memory/disk budgets and sensible eviction; avoid Shield memory pressure.
- Do not block navigation indefinitely on slow network sources. Use stable, non-jumping fallbacks where readiness cannot be guaranteed. Prevent text popping in, late layout shifts, image flashes and animation stutter.
- Test initial load, automatic and D-pad transitions, rapid reversal, wraparound, cold cache, warm cache, missing artwork/metadata and storage/network delays. Profile frame pacing rather than relying only on subjective appearance.

## 2. Hero More Info readability
Keep existing design/placement and focused state. Darken **unfocused** More Info background slightly for readability on light hero artwork. If helpful, use subtle white-text shadow/drop shadow, ensuring contrast on bright/dark backgrounds. Verify focus state remains visibly distinct.

## 3. Tagline typography
User likes tagline replacing description. Change **tagline only** to a restrained, contemporary handwritten-feel font (not calligraphy, feather-pen/script flourish). Preserve title and metadata typography. Check licence, font availability and on-device readability; validate via screenshot before accepting final style. No font file distribution required.

## 4. Continue Watching missing / incorrect Home membership
**Reproduction:** User played `Vigil` season 2 episode 1; progress is persisted and visibly displayed on its Recently Added card, but **Continue Watching row is absent**. Customize Home shows Continue Watching as `Show` although row is not rendered; toggling Hidden then Show does not restore row.

**Required behaviour:**
- Incomplete played items with valid resume progress are eligible for Continue Watching using the app's documented completion/eligibility rules.
- When visible and nonempty, Continue Watching row renders and updates after playback/return to Home. Toggling Hidden suppresses display **without deleting progress**; Show restores row when eligible items exist.
- Items currently eligible for Continue Watching should be excluded from Recently Added **as the user explicitly requested**; re-evaluate membership when progress completes, clears or otherwise ceases eligibility. Avoid unintended global hiding from other library views.
- Trace progress persistence → eligibility/query → Home row assembly → visibility preference → UI invalidation/refresh; do not assume root cause.
- Test `Vigil` S2E1 scenario, Show/Hidden/Show, return from player, restart app, multiple items, completed items and zero-history first run. Never fabricate Continue Watching placeholders.

## 5. Custom library page presentation parity
**User observed:** User-created library pages resemble Movies/TV Shows but differ in spacing/layout. Toolbar lacks icons for Sort, Ascending, List View and Edit. Edit Library is on left rather than right. Focus scales artwork/text internally without enlarging the whole card box, unlike Movies/TV Shows.

**Required behaviour:**
- **Reuse the actual Movies/TV Shows library page presentation and behaviour components** for custom pages (toolbar, layout, grid/list, media cards, focus animation, typography, spacing, iconography, sorting, responsive tokens), rather than approximating them in separate implementations.
- Custom pages vary by user-defined name, content type, filters and relevant supported configuration; preserve those functions.
- Include toolbar icons for Sort, Ascending, List View and Edit. Place Edit Library at right end of toolbar, per previous design agreement.
- Whole focused media **card container** must scale, including its box/background/border, with same expansion and focus effect as built-in Movies/TV Shows. Prevent row clipping and sibling overlap.
- Verify layout parity side-by-side at representative viewports and both grid/list modes. Regression-test custom filters, sort direction, Edit action and focus navigation.
- Do not unnecessarily redesign Movies or TV Shows to match the incorrect custom page; inherit from accepted built-in presentation.

## Explicitly deferred
- **Customize Home pop-up** visual refinements: user wants to see global responsive/dialog changes first; do not redesign it individually now. The Continue Watching **functional** visibility defect above IS in scope.
- Earlier Genres context menu, Filters, Content Type and Display Options cosmetic changes remain deferred pending responsive UI evaluation.
- Movie/TV Details redesign and Playback HUD redesign remain separate parked work, despite shared responsive corrections.

## Handover
Read alongside:
- [Staged first-run and responsive UI handover](STAGED_NEXT_CODEX_HANDOVER_FIRST_RUN_AND_RESPONSIVE_UI_2026-10-11.md)
- [Global responsive layout standard](SUPERNOVA_GLOBAL_RESPONSIVE_LAYOUT_STANDARD_2026-10-11.md)
- [Global dialog design standard](SUPERNOVA_GLOBAL_DIALOG_DESIGN_STANDARD_2026-10-11.md)
- [Global on-screen keyboard standard](SUPERNOVA_GLOBAL_ON_SCREEN_KEYBOARD_STANDARD_2026-10-11.md)
- [Custom library wizard focus/keyboard/exit findings](STAGED_CUSTOM_LIBRARY_WIZARD_FOCUS_KEYBOARD_AND_EXIT_2026-10-11.md)

Reconcile current Codex baseline and prior QA issues before work; require real Shield validation. Nothing here authorises implementation until the user explicitly starts the next handover.
