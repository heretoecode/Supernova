# Supernova — next-release backlog from 0.147 Shield review (2026-10-30)

**Status:** Owner requests fixes in the next release, NOT a hotfix to delivered 0.147. Owner approves the static library-background design direction. Documentation only; no implementation, APK or physical verification claimed.

## Physical Shield findings — must fix next release
1. **Custom Documentaries library missing statistics.** Owner's 16-second video shows Movies and TV Shows displaying library statistics, but the third custom Documentaries library does not show the equivalent stats/header. Audit custom page statistics aggregation, data loading, layout and empty/loading states. Apply consistent stats to all supported library pages. Avoid fabricating totals.
2. **Incorrect library tab focus after switching pages.** When navigating from Documentaries back to TV Shows, the TV Shows content appears while the top navigation selection/focus highlights Documentaries. Audit selected page vs D-pad focus vs asynchronously loaded content, rapid switches and Back/return paths. Ensure visible tab and page content stay synchronised and that focus is deterministic without stealing remote focus. Regression-test Home → Movies → TV Shows → Documentaries → TV Shows and repeated/rapid switches.
3. **Playback age-rating overlay absent on real Shield.** Owner reports no age rating appears at any stage of playback despite implementation and passing software tests in 0.147. Reproduce on movies and episodes with KNOWN configured-region certification and confirm metadata availability, country mapping, player output/start timing, rendering/compositing/z-order, fade and 10-second deadline. Distinguish genuinely missing certification (correctly hidden) from a broken overlay. Do not introduce guessed/foreign ratings. Require real Shield visual QA.

## Approved design change — standardise library background
- **Movies, TV Shows and ALL custom library pages (including Documentaries):** use the existing approved static Supernova Space Black/default background instead of dynamically changing background artwork on each focused item. No new background design or artwork generation.
- Keep library stats, sort/arrange controls, media rows and their focus states. Retain individual artwork in posters/cards and on movie/TV detail pages, where artwork is more visible.
- Remove or disable library-only focused-item background artwork fetch, crossfade, stale async background updates and transitions; avoid interfering with detail hero art, Home artwork, playback preparation or library poster thumbnails.
- Consistent appearance across all libraries and viewport sizes; no visible flash or retained previous-item background on entry/return. Validate D-pad performance and any measurable loading benefit rather than assuming speed gains.

## Acceptance / safety
- Treat these as 0.147 physical QA failures or new approved visual direction; do not mark fixed because unit tests pass. Document source changes, regression tests and real Nvidia Shield confirmation separately.
- Keep signed 0.147 available; do not modify it, PR #27, its frozen source, signing protection, main or active development branches. Create a new implementation branch from verified authoritative release ancestry when the next version is authorised.
- No public release/merge without separate approval. Link this record in the next-version Codex handover and release notes when implemented.
