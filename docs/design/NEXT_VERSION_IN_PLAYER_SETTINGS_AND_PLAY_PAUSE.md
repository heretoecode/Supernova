# Next version — in-player Playback Settings structure and Play/Pause control

**Status:** User-confirmed correction against previously approved mock-ups; implementation pending. **Scope:** In-player playback overlay (distinct from the eight-category global Settings UI). **Target:** next version after 0.143.

## Authoritative visual references
The user has re-supplied the two previously approved **Concept A4** mock-ups:
- `IMG_4968(1).jpeg` — Audio tab: compact dark translucent settings panel; tabs **Audio / Subtitles / Video / More**; two-column content with **Track** on the left and **Sync / Enhancements** on the right; selected track uses a small accent check; Audio delay is inline; Night mode is an inline switch; unavailable spatialisation is shown as a value; Audio filters is a direct row.
- `IMG_4967(1).jpeg` — Video tab: same panel/tab structure; **Format options are inline**, not hidden behind a Format submenu. Left column shows Best fit, Fill screen, Stretch, 16:9, 4:3, Original size; right column contains Video filters and Video track.

These mock-ups are the **design baseline**, not loose inspiration. Reproduce their structure, hierarchy, proportions, dark translucent treatment, compact footprint, focus treatment and inline controls as closely as practical within the actual Android TV/player architecture. Do not replace them with the current large blue full-height panel design.

Values/text in the mock-ups were explicitly illustrative. Source-of-truth player capabilities and current Supernova terminology must determine the final options; preserve additional supported capabilities rather than deleting them merely because an illustrative mock-up omitted them.

## Current regression/evidence
User supplied 0.143 screenshots `IMG_5048.jpeg`, `IMG_5049.jpeg`, `IMG_5050.jpeg`, `IMG_5051.jpeg`, `IMG_5052.jpeg` and video `IMG_5047.mp4`. Current in-player Playback Settings uses a large blue panel, weak hierarchy, considerable empty space/floating controls, and tabs **Subtitles / Audio / Display / Playback**. Display exposes only `Format`, requiring a second click before options appear. This materially diverges from Concept A4.

## Required correction
1. Restore the **Concept A4 design language and information architecture**. Audit any later approved naming changes before implementation; do not casually redesign the reference.
2. **Video/format:** format/aspect choices must be directly visible in the Video tab. No intermediate `Format` submenu. Use a clear single-select/current-state indicator and focus outline.
3. **Audio:** use the approved two-column structure: tracks on left; sync/enhancements on right. Current values/actions should be visible inline where practical.
4. **Subtitles and More:** derive their layouts from the same A4 component system: compact grouped sections, aligned rows/values, subtle dividers, no floating text and no permanent card around every row. Preserve all existing supported functions.
5. **Focus/navigation:** Shield D-pad focus must move predictably among tabs, columns and rows; focus treatment must be distinct from selected/value state. Back returns to playback without unexpected intermediate layers.
6. **Playback HUD Play/Pause:** redesign the current play/pause control to visually belong to this approved playback system, preserving reliable one-click behaviour and alignment with the raised seek-bar work.
7. **Testing:** compare physical Shield screenshots/video against Concept A4 at 1080p and 4K, plus automated focus/state tests. A code-level implementation is not sufficient evidence of visual parity.

## Acceptance gate
Do not report this item as complete until there is explicit visual comparison against both Concept A4 reference images. Any unavoidable deviation must be documented with the technical reason and must not alter the approved design intent.

## Workflow
Include in the next-version Codex handover together with the playback HUD/artwork refinements. Documentation only at this stage: no app code, APK or merge to main.
