# Product decisions — overnight discussion, 7–8 October 2026

Status: durable **approved design directions**, not automatic implementation scope. Existing 4.1.7 fixes-only scope remains frozen.

## Smart Home Rows — APPROVED
- **Show/Hide** controls whole-row visibility, separately from row membership.
- **Dynamic Genre Rule ON** automatically adds matching future library items.
- **Dynamic Genre Rule OFF** freezes automatic membership changes; manually added items remain and manual additions stay possible.
- Sort direction is independently reversible.
- Do not equate hidden row with disabled dynamic rule.

## Up Next / watched / reset — APPROVED
- Up Next overlays/integrates with inherited **Binge Watching** rather than silently replacing its working behaviour.
- Watched threshold is **90%** of playback; manually seeking near the end alone does not mark an item watched. Continue playback/post-credits behaviour must not be broken.
- **Reset Progress** is confirmed and applies to the selected item only: clears playback position and watched state, not the media entry or whole series.

## Multiple Versions — APPROVED
- Automatic default chooses highest quality in this priority order: **4K Dolby Vision/HDR10+ → 4K HDR10 → 4K SDR → 1080p HDR → 1080p SDR → 720p → 480p** (within actually supported/playable variants).
- Dedicated **Versions (n)** action beside Play on Details.
- Selected version's compact resolution/HDR label updates on Details; technical panel follows selected version.
- User's manual version choice persists. Do not silently override it with automatic quality ranking.

## Advanced Subtitles — KEEP EXISTING
- Retain inherited NOVA subtitle appearance, positioning, synchronisation, language selection, downloads and format handling as closely as possible.
- No new presets or sliders; do not redesign merely for modernisation. Revisit only if real testing identifies a defect.

## Release/version history presentation — DESIGN DIRECTION
- Pre-1.0 numbering proceeds sequentially (0.1, 0.2, etc.).
- Latest release details expanded by default; older releases collapsed and expandable by TV D-pad.
- Do not confuse this future UI direction with current application versioning or authorise a branding/package change.

## Boundaries
- Seek thumbnails, Profiles, AI, Anime, Trakt reviews, wider Library Health and other exploratory work remain parked unless explicitly activated.
- Network & Files, Settings and APK walkthrough were intentionally reserved for separate detailed review; do not infer final approvals from their mention here.
- This file records decisions, not a new Codex handover or permission to modify application code.
