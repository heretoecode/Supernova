# Next version — global Settings toggle and row styling

**Status:** User-approved design direction; implementation pending. **Scope:** ALL eight Settings categories and their nested/subsection screens, not Audio & Subtitles alone. **Target:** next version after 0.143.

## User-observed problems
- Existing toggle's enabled state appears as a fully white-filled box; On versus Off is not sufficiently obvious.
- Individual settings are surrounded by persistent rounded boxes, creating unnecessary visual clutter.
- These are **global Settings UI issues**; do not implement a page-specific workaround.

## Required global design
1. Use one shared switch/toggle presentation for all boolean settings: visibly distinct On/Off states (thumb position and contrasting track colours), optional explicit On/Off labels where helpful. A filled white rectangle alone must not signal enabled state.
2. Separate **D-pad focus** from **setting value**: the blue focus indicator must not be mistaken for the On state; both states must remain recognisable while focused and unfocused.
3. Replace permanent rounded rectangles around individual setting rows with a clean list layout, consistent vertical spacing and subtle dividers where needed. Focused row may receive a temporary focus treatment; avoid making every row a card.
4. Apply via shared components/theme across all eight landing categories: Playback & Video; Audio & Subtitles; Library & Home; Streaming & Providers; Appearance; Connections & Integrations; System & Maintenance; About Supernova. Include nested subsections and shared dialogs where applicable; preserve distinct card styling on the eight-category landing grid itself.
5. Preserve all existing preference IDs/keys, values, defaults, visibility rules, dependencies, persistence, screen-reader semantics and D-pad navigation. Do not change setting functionality as part of a visual refresh.
6. Test enabled/disabled, focused/unfocused, disabled-by-dependency, long text, 720p/1080p/4K, 150% text, and physical Shield navigation. Include visual screenshots across representative categories and audit all categories for consistency.

## Related next-version tasks
See `NEXT_VERSION_SETTINGS_LANGUAGE_FLAGS_AND_COPY.md` for picker locale flags and wording, and other next-version design notes for Settings transitions and landing-grid spacing.

## Workflow
Include in next-version Codex handover. This document is not an implementation claim; no application code or release branch changed.
