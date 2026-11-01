# Supernova — next Codex fixes (agreed 1 November 2026)

**Status:** Implementation committed on `codex/agreed-fixes-2026-11-01`; automated regression, compilation and lint checks passed; physical Shield QA and final packaged flag rendering pending. See [implementation and validation](2026-11-01-validation.md) for actual evidence and limitations.

1. Library page 3: statistics visible immediately, no scrolling.
2. Home / Filters: stop remote-focus flickering.
3. Settings left navigation: remove focus-triggered ticks; preserve genuine option checkmarks.
4. Settings toggles: clearly distinguish On, Off and Unavailable; preserve consistent focused-row outline.
5. Audio and Subtitle language selection: restore language flags, with fallbacks.
6. Provider / Done icons: restore provider logos; use appropriate action icon for Done.
7. Reception: display ratings/reviews or explicit “Unavailable”; never blank.
8. Network & Files: remove only large background panel behind six existing cards. Preserve all six cards, layout and focus effects. No mock-up required.
9. Playback HUD: keep “Ends at” visible while paused.
10. Playback HUD: improve spacing between duration and finishing-time timestamps.

## Excluded / parked
All playback settings redesigns, including standalone subtitle window and subtitle appearance grouping. No implementation of these designs in this handover.

## Codex guidance
Inspect latest repository status and docs first. Preserve active branches, uncommitted work and current functionality. Do not reset, discard, merge or start an unrelated release. Implement only approved fixes. Add targeted regression tests and build validation. Report implemented vs tested vs outstanding, with Shield physical QA pending unless actually performed.
