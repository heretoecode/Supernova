# Next version — in-player Playback Settings structure and Play/Pause control

**Status:** User-reported visual and navigation problems; pending investigation and implementation. **Scope:** In-player playback overlay (distinct from the eight-category global Settings UI). **Target:** next version after 0.143.

## Evidence
User supplied screenshots IMG_5048.jpeg, IMG_5049.jpeg, IMG_5050.jpeg, IMG_5051.jpeg, IMG_5052.jpeg and video IMG_5047.mp4. In-player Playback Settings currently shows four tabs (Subtitles, Audio, Display, Playback) on a large blue panel with weak visual hierarchy, considerable empty space and floating-looking text/controls. Display tab presents only `Format`, requiring a second click to reveal options. User dislikes existing Play/Pause control appearance on playback HUD.

## Required design and functionality
1. **Play/Pause:** Redesign icon/control appearance and focused/unfocused states to align with the rest of the approved playback HUD, with consistent sizing, hit target and alignment to seek bar. Preserve single-click play/pause behaviour and reliable Shield D-pad focus.
2. **In-player Settings:** Keep existing four tabs but give each a structured layout with consistent tab alignment, section headings, row spacing, selected/current values and subtle dividers. Avoid adding a permanent rounded box around every row. Make all settings readable, navigable and visually grouped; retain current functionality.
3. **Display:** Eliminate unnecessary intermediate `Format` submenu. Show aspect/format choices directly in Display tab, with single-select indicator and visible unavailable state: Original, Fullscreen, Stretched, 4:3, 16:9, 1.85:1, 2.39:1, Optimized (if supported). Audit exact supported values in source; do not introduce unsupported modes. Changing selection must apply correctly and reflect actual current mode.
4. **Audio/Subtitles/Playback:** Organise tracks, enhancements, timing, appearance, speed and play mode into clear groups; show current values and avoid unexplained blank space. Keep all existing options and dynamic availability/disabled states. Preserve correct source-language fallback icons/flags based on locale-specificity.
5. **Testing:** Verify all tabs, direct format selection, audio/subtitle track changes, remote focus/back navigation, 720p/1080p/4K, large text, disabled options, HUD interaction and playback continuity on Nvidia Shield. Capture screenshots/videos and compare against approved design rather than claiming parity solely from code tests.

## Design governance
Review previous approved playback Settings designs/mockups and requirements in repository before implementation. If the implemented screen diverges, document why and restore approved design intent. Do not assume these screenshots prove root cause. This is a documentation-only backlog addition; do not modify app code or merge to main.
